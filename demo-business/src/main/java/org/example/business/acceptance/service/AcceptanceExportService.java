package org.example.business.acceptance.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.example.business.acceptance.entity.Acceptance;
import org.example.business.acceptance.entity.AcceptanceItem;
import org.example.business.mapper.AcceptanceItemMapper;
import org.example.business.mapper.AcceptanceMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AcceptanceExportService {

    private final AcceptanceMapper acceptanceMapper;
    private final AcceptanceItemMapper itemMapper;

    public void export(OutputStream out) throws IOException {
        List<Acceptance> heads = acceptanceMapper.selectList(new LambdaQueryWrapper<Acceptance>()
                .orderByDesc(Acceptance::getCreateTime));
        Map<Long, List<AcceptanceItem>> itemsByHead = itemMapper.selectList(new LambdaQueryWrapper<>()).stream()
                .collect(Collectors.groupingBy(AcceptanceItem::getAcceptanceId));

        try (Workbook wb = new XSSFWorkbook()) {
            Sheet sheet = wb.createSheet("验收记录");
            // 表头
            String[] headers = {
                    "验收单ID", "采购订单号", "供应商", "到货日期", "验收人", "状态", "备注",
                    "设备编码", "设备名称", "设备类型", "规格型号", "单位", "数量", "验收结果", "缺陷描述"
            };
            CellStyle headerStyle = wb.createCellStyle();
            Font bold = wb.createFont(); bold.setBold(true); headerStyle.setFont(bold);
            Row hr = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell c = hr.createCell(i);
                c.setCellValue(headers[i]);
                c.setCellStyle(headerStyle);
                sheet.setColumnWidth(i, 4500);
            }

            int rowIdx = 1;
            for (Acceptance h : heads) {
                List<AcceptanceItem> items = itemsByHead.getOrDefault(h.getId(), List.of());
                if (items.isEmpty()) {
                    Row r = sheet.createRow(rowIdx++);
                    fillHead(r, h);
                } else {
                    for (AcceptanceItem it : items) {
                        Row r = sheet.createRow(rowIdx++);
                        fillHead(r, h);
                        r.createCell(7).setCellValue(nullSafe(it.getDeviceCode()));
                        r.createCell(8).setCellValue(nullSafe(it.getDeviceName()));
                        r.createCell(9).setCellValue(nullSafe(it.getDeviceType()));
                        r.createCell(10).setCellValue(nullSafe(it.getSpec()));
                        r.createCell(11).setCellValue(nullSafe(it.getUnit()));
                        r.createCell(12).setCellValue(it.getQty() == null ? "" : it.getQty().toPlainString());
                        r.createCell(13).setCellValue(nullSafe(it.getResult()));
                        r.createCell(14).setCellValue(nullSafe(it.getDefectDesc()));
                    }
                }
            }
            wb.write(out);
        }
    }

    private void fillHead(Row r, Acceptance h) {
        r.createCell(0).setCellValue(h.getId() == null ? "" : String.valueOf(h.getId()));
        r.createCell(1).setCellValue(nullSafe(h.getOrderNo()));
        r.createCell(2).setCellValue(nullSafe(h.getSupplierName()));
        r.createCell(3).setCellValue(h.getArrivalDate() == null ? "" : h.getArrivalDate().toString());
        r.createCell(4).setCellValue(nullSafe(h.getInspectorName()));
        r.createCell(5).setCellValue(nullSafe(h.getStatus()));
        r.createCell(6).setCellValue(nullSafe(h.getRemark()));
    }

    private String nullSafe(String s) { return s == null ? "" : s; }
}
