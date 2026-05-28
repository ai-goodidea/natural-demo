package org.example.business.acceptance.service;

import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.common.model.acceptance.domain.AcceptanceStatus;
import org.example.common.model.acceptance.dto.AcceptanceQuery;
import org.example.business.acceptance.dto.excel.AcceptanceHeadExcelDTO;
import org.example.business.acceptance.dto.excel.AcceptanceItemExcelDTO;
import org.example.common.model.acceptance.entity.Acceptance;
import org.example.common.model.acceptance.entity.AcceptanceItem;
import org.example.business.mapper.AcceptanceItemMapper;
import org.example.business.mapper.AcceptanceMapper;
import org.example.common.model.acceptance.vo.AcceptanceSummaryResult;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 验收单 Excel 导出 —— 基于阿里 EasyExcel 4.x。
 * 输出两个 Sheet：
 *   Sheet1「验收单头信息」：每行 = 一张验收单的主信息
 *   Sheet2「验收明细」：每行 = 一行设备明细，带头信息冗余字段，便于关联
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AcceptanceExportService {

    private static final DateTimeFormatter DATETIME_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static final Map<String, String> RESULT_TEXT = Map.of(
            "QUALIFIED", "合格",
            "CONCESSION", "让步接收",
            "RETURNED", "退货"
    );

    private final AcceptanceMapper acceptanceMapper;
    private final AcceptanceItemMapper itemMapper;
    private final ObjectMapper objectMapper;

    public void export(AcceptanceQuery query, OutputStream out) {
        List<Acceptance> heads = acceptanceMapper.selectList(buildWrapper(query));

        Map<Long, List<AcceptanceItem>> itemsByHead = heads.isEmpty()
                ? Map.of()
                : itemMapper.selectList(new LambdaQueryWrapper<AcceptanceItem>()
                        .in(AcceptanceItem::getAcceptanceId,
                                heads.stream().map(Acceptance::getId).toList())
                        .orderByAsc(AcceptanceItem::getAcceptanceId)
                        .orderByAsc(AcceptanceItem::getId))
                .stream().collect(Collectors.groupingBy(AcceptanceItem::getAcceptanceId));

        List<AcceptanceHeadExcelDTO> headRows = new ArrayList<>(heads.size());
        List<AcceptanceItemExcelDTO> itemRows = new ArrayList<>();
        for (Acceptance h : heads) {
            List<AcceptanceItem> items = itemsByHead.getOrDefault(h.getId(), Collections.emptyList());
            headRows.add(toHeadRow(h, items.size()));
            for (AcceptanceItem it : items) {
                itemRows.add(toItemRow(h, it));
            }
        }

        ExcelWriter writer = EasyExcel.write(out).build();
        try {
            WriteSheet headSheet = EasyExcel.writerSheet(0, "验收单头信息")
                    .head(AcceptanceHeadExcelDTO.class).build();
            writer.write(headRows, headSheet);

            WriteSheet itemSheet = EasyExcel.writerSheet(1, "验收明细")
                    .head(AcceptanceItemExcelDTO.class).build();
            writer.write(itemRows, itemSheet);
        } finally {
            writer.finish();
        }
    }

    private LambdaQueryWrapper<Acceptance> buildWrapper(AcceptanceQuery q) {
        LambdaQueryWrapper<Acceptance> w = new LambdaQueryWrapper<>();
        if (q != null) {
            if (StrUtil.isNotBlank(q.getOrderNo()))      w.like(Acceptance::getOrderNo, q.getOrderNo());
            if (StrUtil.isNotBlank(q.getSupplierName())) w.like(Acceptance::getSupplierName, q.getSupplierName());
            if (StrUtil.isNotBlank(q.getStatus()))       w.eq(Acceptance::getStatus, q.getStatus());
        }
        w.orderByDesc(Acceptance::getCreateTime);
        return w;
    }

    private AcceptanceHeadExcelDTO toHeadRow(Acceptance h, int itemCount) {
        AcceptanceHeadExcelDTO dto = new AcceptanceHeadExcelDTO();
        dto.setId(h.getId() == null ? "" : h.getId().toString());
        dto.setOrderNo(h.getOrderNo());
        dto.setSupplierName(h.getSupplierName());
        dto.setArrivalDate(h.getArrivalDate() == null ? "" : h.getArrivalDate().toString());
        dto.setInspectorName(h.getInspectorName());
        dto.setStatusText(statusText(h.getStatus()));
        dto.setItemCount(itemCount);
        dto.setAiConclusionText(extractAiConclusion(h.getSummaryJson()));
        dto.setRemark(h.getRemark());
        dto.setCreateTime(h.getCreateTime() == null ? "" : h.getCreateTime().format(DATETIME_FMT));
        return dto;
    }

    private AcceptanceItemExcelDTO toItemRow(Acceptance h, AcceptanceItem it) {
        AcceptanceItemExcelDTO dto = new AcceptanceItemExcelDTO();
        dto.setAcceptanceId(h.getId() == null ? "" : h.getId().toString());
        dto.setOrderNo(h.getOrderNo());
        dto.setSupplierName(h.getSupplierName());
        dto.setDeviceCode(it.getDeviceCode());
        dto.setDeviceName(it.getDeviceName());
        dto.setDeviceType(it.getDeviceType());
        dto.setSpec(it.getSpec());
        dto.setUnit(it.getUnit());
        dto.setQty(it.getQty());
        dto.setResultText(resultText(it.getResult()));
        dto.setDefectDesc(it.getDefectDesc());
        return dto;
    }

    private String statusText(String status) {
        if (StrUtil.isBlank(status)) return "";
        try {
            return AcceptanceStatus.valueOf(status).getText();
        } catch (IllegalArgumentException ignored) {
            return status;
        }
    }

    private String resultText(String result) {
        if (StrUtil.isBlank(result)) return "";
        return RESULT_TEXT.getOrDefault(result, result);
    }

    private String extractAiConclusion(String summaryJson) {
        if (StrUtil.isBlank(summaryJson)) return "";
        try {
            AcceptanceSummaryResult r = objectMapper.readValue(summaryJson, AcceptanceSummaryResult.class);
            return StrUtil.nullToDefault(r.getConclusionText(),
                    StrUtil.nullToDefault(r.getConclusion(), ""));
        } catch (JsonProcessingException e) {
            log.debug("parse summaryJson failed: {}", e.getMessage());
            return "";
        }
    }
}
