package org.example.supplier.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SupplierUpdateRequest {

    @NotNull
    private Long id;

    private String code;
    private String name;
    private String contact;
    private String phone;
    private String address;
    private BigDecimal creditScore;
    private Integer status;
}
