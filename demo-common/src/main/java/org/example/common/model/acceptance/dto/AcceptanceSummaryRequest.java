package org.example.common.model.acceptance.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "AcceptanceSummaryRequest", description = "提交给 AI 做摘要的验收单载荷")
public class AcceptanceSummaryRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "采购订单号")
    private String orderNo;

    @Schema(description = "供应商名称")
    private String supplierName;

    @Schema(description = "到货日期（字符串）")
    private String arrivalDate;

    @Schema(description = "验收备注")
    private String remark;

    @Schema(description = "明细行列表")
    private List<Item> items;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(name = "AcceptanceSummaryRequest.Item", description = "AI 摘要载荷的明细行")
    public static class Item {
        @Schema(description = "设备编码")
        private String deviceCode;
        @Schema(description = "设备名称")
        private String deviceName;
        @Schema(description = "规格型号")
        private String spec;
        @Schema(description = "单位")
        private String unit;
        @Schema(description = "数量（字符串形式）")
        private String qty;
        @Schema(description = "验收结果")
        private String result;
        @Schema(description = "缺陷描述")
        private String defectDesc;
    }
}
