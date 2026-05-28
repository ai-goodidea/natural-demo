package org.example.business.acceptance.dto.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentStyle;
import com.alibaba.excel.enums.poi.HorizontalAlignmentEnum;
import lombok.Data;

/**
 * 验收单头信息（导出 Sheet1：验收单头信息）
 */
@Data
@ColumnWidth(20)
public class AcceptanceHeadExcelDTO {

    /**
     * 验收单 ID。雪花算法生成的 19 位长整型如果按数值写入，
     * Excel 会按精度限制截断为类似 1.93E+18 的科学计数法，因此这里：
     *   1) 字段类型用 String，让 EasyExcel 直接按文本写；
     *   2) ContentStyle 显式设置数据格式 "@"（文本），即便用户手动改单元格类型也保留前导显示；
     *   3) 列宽放大到能完整显示 19 位数字。
     */
    @ExcelProperty("验收单ID")
    @ColumnWidth(26)
    @ContentStyle(dataFormat = 49, horizontalAlignment = HorizontalAlignmentEnum.LEFT)
    private String id;

    @ExcelProperty("采购订单号")
    private String orderNo;

    @ExcelProperty("供应商")
    @ColumnWidth(28)
    private String supplierName;

    @ExcelProperty("到货日期")
    @ColumnWidth(14)
    private String arrivalDate;

    @ExcelProperty("验收人")
    @ColumnWidth(14)
    private String inspectorName;

    @ExcelProperty("状态")
    @ColumnWidth(14)
    private String statusText;

    @ExcelProperty("明细数量")
    @ColumnWidth(12)
    private Integer itemCount;

    @ExcelProperty("AI 结论")
    @ColumnWidth(16)
    private String aiConclusionText;

    @ExcelProperty("备注")
    @ColumnWidth(40)
    private String remark;

    @ExcelProperty("创建时间")
    @ColumnWidth(22)
    private String createTime;
}
