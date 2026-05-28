package org.example.common.model.supplier.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.common.dto.BaseVO;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(name = "SupplierVO", description = "供应商视图对象")
public class SupplierVO extends BaseVO {

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
