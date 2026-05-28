package org.example.business.acceptance.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.common.model.acceptance.domain.AcceptanceStatus;
import org.example.business.acceptance.dto.AcceptanceConverter;
import org.example.common.model.acceptance.vo.AcceptanceHeadVO;
import org.example.common.model.acceptance.dto.AcceptanceQuery;
import org.example.common.model.acceptance.dto.AcceptanceSaveRequest;
import org.example.common.model.acceptance.vo.AcceptanceVO;
import org.example.common.model.acceptance.dto.StatusChangeRequest;
import org.example.common.model.acceptance.entity.Acceptance;
import org.example.common.model.acceptance.entity.AcceptanceItem;
import org.example.common.model.acceptance.entity.Rectification;
import org.example.business.mapper.AcceptanceItemMapper;
import org.example.business.mapper.AcceptanceMapper;
import org.example.business.mapper.RectificationMapper;
import org.example.common.auth.UserContextHolder;
import org.example.common.dto.PageResult;
import org.example.common.exception.BusinessException;
import org.example.common.result.Result;
import org.example.common.result.ResultCode;
import org.example.feign.client.AiFeignClient;
import org.example.common.model.acceptance.dto.AcceptanceSummaryRequest;
import org.example.common.model.acceptance.vo.AcceptanceSummaryResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AcceptanceService {

    private final AcceptanceMapper acceptanceMapper;
    private final AcceptanceItemMapper itemMapper;
    private final RectificationMapper rectificationMapper;
    private final OperationLogService operationLogService;
    private final AiFeignClient aiFeignClient;
    private final ObjectMapper objectMapper;

    public PageResult<AcceptanceHeadVO> page(AcceptanceQuery q) {
        LambdaQueryWrapper<Acceptance> w = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(q.getOrderNo()))      w.like(Acceptance::getOrderNo, q.getOrderNo());
        if (StrUtil.isNotBlank(q.getSupplierName())) w.like(Acceptance::getSupplierName, q.getSupplierName());
        if (StrUtil.isNotBlank(q.getStatus()))       w.eq(Acceptance::getStatus, q.getStatus());
        w.orderByDesc(Acceptance::getCreateTime);
        Page<Acceptance> page = acceptanceMapper.selectPage(new Page<>(q.getCurrent(), q.getSize()), w);
        return PageResult.from(page, AcceptanceConverter::toHeadVO);
    }

    public AcceptanceVO detail(Long id) {
        Acceptance head = acceptanceMapper.selectById(id);
        if (head == null) throw new BusinessException(ResultCode.NOT_FOUND);
        AcceptanceVO vo = new AcceptanceVO();
        vo.setHead(AcceptanceConverter.toHeadVO(head));
        vo.setItems(itemMapper.selectList(new LambdaQueryWrapper<AcceptanceItem>()
                .eq(AcceptanceItem::getAcceptanceId, id)
                .orderByAsc(AcceptanceItem::getId))
                .stream().map(AcceptanceConverter::toItemVO).toList());
        vo.setRectifications(rectificationMapper.selectList(new LambdaQueryWrapper<Rectification>()
                .eq(Rectification::getAcceptanceId, id)
                .orderByDesc(Rectification::getCreateTime))
                .stream().map(AcceptanceConverter::toRectificationVO).toList());
        return vo;
    }

    @Transactional
    public AcceptanceVO save(AcceptanceSaveRequest req) {
        boolean isNew = req.getId() == null;
        Acceptance head;
        if (isNew) {
            head = new Acceptance();
            head.setStatus(AcceptanceStatus.PENDING.name());
        } else {
            head = acceptanceMapper.selectById(req.getId());
            if (head == null) throw new BusinessException(ResultCode.NOT_FOUND);
            checkEditable(head);
        }
        head.setOrderNo(req.getOrderNo());
        head.setSupplierId(req.getSupplierId());
        head.setSupplierName(req.getSupplierName());
        head.setArrivalDate(req.getArrivalDate());
        head.setInspectorId(req.getInspectorId() != null ? req.getInspectorId() : UserContextHolder.get().getUserId());
        head.setInspectorName(StrUtil.nullToDefault(req.getInspectorName(), UserContextHolder.get().getUsername()));
        head.setRemark(req.getRemark());
        if (isNew) acceptanceMapper.insert(head);
        else acceptanceMapper.updateById(head);

        // 全量替换明细行
        if (!isNew) {
            itemMapper.delete(new LambdaQueryWrapper<AcceptanceItem>().eq(AcceptanceItem::getAcceptanceId, head.getId()));
        }
        if (req.getItems() != null) {
            for (AcceptanceSaveRequest.ItemPayload p : req.getItems()) {
                AcceptanceItem it = new AcceptanceItem();
                it.setAcceptanceId(head.getId());
                it.setDeviceCode(p.getDeviceCode());
                it.setDeviceName(p.getDeviceName());
                it.setDeviceType(p.getDeviceType());
                it.setSpec(p.getSpec());
                it.setUnit(p.getUnit());
                it.setQty(p.getQty());
                it.setResult(StrUtil.nullToDefault(p.getResult(), "QUALIFIED"));
                it.setDefectDesc(p.getDefectDesc());
                itemMapper.insert(it);
            }
        }

        operationLogService.writeManually(
                isNew ? "CREATE" : "UPDATE",
                "ACCEPTANCE", String.valueOf(head.getId()),
                (isNew ? "创建验收单 " : "更新验收单 ") + head.getOrderNo(),
                null, null);

        return detail(head.getId());
    }

    @Transactional
    public void delete(Long id) {
        Acceptance head = acceptanceMapper.selectById(id);
        if (head == null) throw new BusinessException(ResultCode.NOT_FOUND);
        checkEditable(head);
        itemMapper.delete(new LambdaQueryWrapper<AcceptanceItem>().eq(AcceptanceItem::getAcceptanceId, id));
        rectificationMapper.delete(new LambdaQueryWrapper<Rectification>().eq(Rectification::getAcceptanceId, id));
        acceptanceMapper.deleteById(id);
        operationLogService.writeManually("DELETE", "ACCEPTANCE", String.valueOf(id), "删除验收单 " + head.getOrderNo(), null, null);
    }

    @Transactional
    public AcceptanceVO changeStatus(Long id, StatusChangeRequest req) {
        Acceptance head = acceptanceMapper.selectById(id);
        if (head == null) throw new BusinessException(ResultCode.NOT_FOUND);

        AcceptanceStatus from = AcceptanceStatus.valueOf(head.getStatus());
        AcceptanceStatus to;
        try {
            to = AcceptanceStatus.valueOf(req.getNewStatus());
        } catch (IllegalArgumentException e) {
            throw new BusinessException("非法状态：" + req.getNewStatus());
        }

        if (!AcceptanceStatus.canTransit(from, to)) {
            throw new BusinessException(
                    String.format("不允许从 %s 流转到 %s", from.getText(), to.getText()));
        }

        if (to == AcceptanceStatus.RECTIFYING) {
            if (StrUtil.isBlank(req.getRectificationContent())) {
                throw new BusinessException("进入整改中必须填写整改内容");
            }
            Rectification r = new Rectification();
            r.setAcceptanceId(id);
            r.setContent(req.getRectificationContent());
            r.setOwner(req.getRectificationOwner());
            r.setDeadline(req.getRectificationDeadline());
            r.setFinished(0);
            rectificationMapper.insert(r);
        }
        if (to == AcceptanceStatus.RECTIFIED) {
            // 把所有该单的整改记录置为已完成
            List<Rectification> ongoing = rectificationMapper.selectList(new LambdaQueryWrapper<Rectification>()
                    .eq(Rectification::getAcceptanceId, id).eq(Rectification::getFinished, 0));
            for (Rectification r : ongoing) {
                r.setFinished(1);
                rectificationMapper.updateById(r);
            }
        }

        head.setStatus(to.name());
        acceptanceMapper.updateById(head);

        operationLogService.writeManually("STATUS_CHANGE", "ACCEPTANCE", String.valueOf(id),
                String.format("状态：%s → %s", from.getText(), to.getText()), from.name(), to.name());
        return detail(id);
    }

    public AcceptanceSummaryResult aiSummary(Long id) {
        Acceptance head = acceptanceMapper.selectById(id);
        if (head == null) throw new BusinessException(ResultCode.NOT_FOUND);
        List<AcceptanceItem> items = itemMapper.selectList(new LambdaQueryWrapper<AcceptanceItem>()
                .eq(AcceptanceItem::getAcceptanceId, id).orderByAsc(AcceptanceItem::getId));

        AcceptanceSummaryRequest req = AcceptanceSummaryRequest.builder()
                .orderNo(head.getOrderNo())
                .supplierName(head.getSupplierName())
                .arrivalDate(head.getArrivalDate() == null ? "" : head.getArrivalDate().toString())
                .remark(head.getRemark())
                .items(items.stream().map(it -> AcceptanceSummaryRequest.Item.builder()
                        .deviceCode(it.getDeviceCode())
                        .deviceName(it.getDeviceName())
                        .spec(it.getSpec())
                        .unit(it.getUnit())
                        .qty(it.getQty() == null ? "" : it.getQty().toPlainString())
                        .result(it.getResult())
                        .defectDesc(it.getDefectDesc())
                        .build()).toList())
                .build();

        Result<AcceptanceSummaryResult> resp = aiFeignClient.summarizeAcceptance(req);
        if (resp == null || !resp.isSuccess() || resp.getData() == null) {
            throw new BusinessException(resp == null ? "AI 调用无响应"
                    : (resp.getMessage() == null ? "AI 调用失败" : resp.getMessage()));
        }
        AcceptanceSummaryResult result = resp.getData();
        try {
            head.setSummaryJson(objectMapper.writeValueAsString(result));
            acceptanceMapper.updateById(head);
        } catch (JsonProcessingException e) {
            log.warn("write summary_json failed", e);
        }
        operationLogService.writeManually("AI_SUMMARY", "ACCEPTANCE", String.valueOf(id),
                "生成 AI 验收结论：" + result.getConclusionText(), null, null);
        return result;
    }

    @Transactional
    public AcceptanceVO saveSummary(Long id, AcceptanceSummaryResult summary) {
        Acceptance head = acceptanceMapper.selectById(id);
        if (head == null) throw new BusinessException(ResultCode.NOT_FOUND);
        try {
            head.setSummaryJson(objectMapper.writeValueAsString(summary));
        } catch (JsonProcessingException e) {
            throw new BusinessException("保存摘要失败：" + e.getMessage());
        }
        acceptanceMapper.updateById(head);
        operationLogService.writeManually("UPDATE", "ACCEPTANCE", String.valueOf(id),
                "保存 AI 摘要", null, null);
        return detail(id);
    }

    private void checkEditable(Acceptance head) {
        AcceptanceStatus s = AcceptanceStatus.valueOf(head.getStatus());
        if (AcceptanceStatus.isFinal(s)) {
            throw new BusinessException("当前状态(" + s.getText() + ")不可编辑");
        }
    }
}
