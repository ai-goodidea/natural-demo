package org.example.common.model.supplier.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.common.entity.BaseEntity;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_supplier")
@Schema(name = "Supplier", description = "供应商实体")
public class Supplier extends BaseEntity {

    @Schema(description = "供应商编码", example = "SUP-001")
    private String code;

    @Schema(description = "供应商名称")
    private String name;

    @Schema(description = "联系人")
    private String contact;

    @Schema(description = "联系电话")
    private String phone;

    @Schema(description = "地址")
    private String address;

    @Schema(description = "信用分", example = "90.5")
    private BigDecimal creditScore;

    @Schema(description = "状态：1=合作中 0=停用", example = "1")
    private Integer status;
}
