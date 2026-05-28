package org.example.common.model.supplier.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(name = "SupplierCreateRequest", description = "新建供应商请求")
public class SupplierCreateRequest {

    @NotBlank
    @Schema(description = "供应商编码", requiredMode = Schema.RequiredMode.REQUIRED, example = "SUP-001")
    private String code;

    @NotBlank
    @Schema(description = "供应商名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(description = "联系人")
    private String contact;

    @Schema(description = "联系电话")
    private String phone;

    @Schema(description = "地址")
    private String address;

    @Schema(description = "信用分")
    private BigDecimal creditScore;

    @Schema(description = "状态：1=合作中 0=停用")
    private Integer status;
}
