package org.example.common.model.acceptance.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Schema(name = "AcceptanceSaveRequest", description = "新建 / 更新验收单请求")
public class AcceptanceSaveRequest {

    @Schema(description = "验收单 ID（更新时必填）")
    private Long id;

    @NotBlank
    @Schema(description = "采购订单号", requiredMode = Schema.RequiredMode.REQUIRED)
    private String orderNo;

    @Schema(description = "供应商 ID")
    private Long supplierId;

    @Schema(description = "供应商名称")
    private String supplierName;

    @Schema(description = "到货日期")
    private LocalDate arrivalDate;

    @Schema(description = "验收人 ID（不传则用当前登录用户）")
    private Long inspectorId;

    @Schema(description = "验收人姓名")
    private String inspectorName;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "明细行列表")
    private List<ItemPayload> items;

    @Data
    @Schema(name = "AcceptanceSaveRequest.ItemPayload", description = "明细行载荷")
    public static class ItemPayload {

        @Schema(description = "明细行 ID（保留字段）")
        private Long id;

        @Schema(description = "设备编码")
        private String deviceCode;

        @Schema(description = "设备名称")
        private String deviceName;

        @Schema(description = "设备类型")
        private String deviceType;

        @Schema(description = "规格型号")
        private String spec;

        @Schema(description = "单位")
        private String unit;

        @Schema(description = "数量")
        private BigDecimal qty;

        @Schema(description = "验收结果：QUALIFIED / CONCESSION / RETURNED")
        private String result;

        @Schema(description = "缺陷描述")
        private String defectDesc;
    }
}
