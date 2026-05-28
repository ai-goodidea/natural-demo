package org.example.business.acceptance.dto.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentStyle;
import com.alibaba.excel.enums.poi.HorizontalAlignmentEnum;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 验收单明细行（导出 Sheet2：验收明细）。
 * 携带头部冗余字段（验收单ID/采购订单号/供应商），方便单独看明细 sheet 时可关联回头信息。
 */
@Data
@ColumnWidth(18)
public class AcceptanceItemExcelDTO {

    /** 字符串文本格式 + 加宽列，避免 19 位雪花 ID 被 Excel 显示成科学计数法 */
    @ExcelProperty("验收单ID")
    @ColumnWidth(26)
    @ContentStyle(dataFormat = 49, horizontalAlignment = HorizontalAlignmentEnum.LEFT)
    private String acceptanceId;

    @ExcelProperty("采购订单号")
    private String orderNo;

    @ExcelProperty("供应商")
    @ColumnWidth(26)
    private String supplierName;

    @ExcelProperty("设备编码")
    private String deviceCode;

    @ExcelProperty("设备名称")
    @ColumnWidth(22)
    private String deviceName;

    @ExcelProperty("设备类型")
    private String deviceType;

    @ExcelProperty("规格型号")
    @ColumnWidth(22)
    private String spec;

    @ExcelProperty("单位")
    @ColumnWidth(10)
    private String unit;

    @ExcelProperty("数量")
    @ColumnWidth(10)
    private BigDecimal qty;

    @ExcelProperty("验收结果")
    @ColumnWidth(14)
    private String resultText;

    @ExcelProperty("缺陷描述")
    @ColumnWidth(40)
    private String defectDesc;
}
