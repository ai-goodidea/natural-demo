package org.example.common.model.acceptance.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.common.dto.BaseVO;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(name = "AcceptanceHeadVO", description = "验收单头信息视图对象")
public class AcceptanceHeadVO extends BaseVO {

    @Schema(description = "采购订单号")
    private String orderNo;

    @Schema(description = "供应商 ID")
    private Long supplierId;

    @Schema(description = "供应商名称")
    private String supplierName;

    @Schema(description = "到货日期")
    private LocalDate arrivalDate;

    @Schema(description = "验收人 ID")
    private Long inspectorId;

    @Schema(description = "验收人姓名")
    private String inspectorName;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "AI 摘要 JSON 原文")
    private String summaryJson;
}
