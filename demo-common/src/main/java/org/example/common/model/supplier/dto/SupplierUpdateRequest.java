package org.example.common.model.supplier.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(name = "SupplierUpdateRequest", description = "更新供应商请求")
public class SupplierUpdateRequest {

    @NotNull
    @Schema(description = "供应商主键 ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;

    @Schema(description = "供应商编码")
    private String code;

    @Schema(description = "供应商名称")
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
