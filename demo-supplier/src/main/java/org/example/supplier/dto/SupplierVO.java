package org.example.supplier.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.common.dto.BaseVO;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
public class SupplierVO extends BaseVO {

    private String code;
    private String name;
    private String contact;
    private String phone;
    private String address;
    private BigDecimal creditScore;
    /** 1=合作中 0=停用 */
    private Integer status;
}
