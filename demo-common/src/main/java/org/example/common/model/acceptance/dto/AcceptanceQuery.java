package org.example.common.model.acceptance.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "AcceptanceQuery", description = "验收单列表查询条件")
public class AcceptanceQuery {

    @Schema(description = "当前页", example = "1")
    private long current = 1;

    @Schema(description = "每页大小", example = "10")
    private long size = 10;

    @Schema(description = "采购订单号（模糊）")
    private String orderNo;

    @Schema(description = "供应商名称（模糊）")
    private String supplierName;

    @Schema(description = "状态")
    private String status;
}
