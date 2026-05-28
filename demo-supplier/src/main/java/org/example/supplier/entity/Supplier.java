package org.example.supplier.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.common.entity.BaseEntity;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_supplier")
public class Supplier extends BaseEntity {

    private String code;
    private String name;
    private String contact;
    private String phone;
    private String address;
    private BigDecimal creditScore;
    /** 1=合作中 0=停用 */
    private Integer status;
}
