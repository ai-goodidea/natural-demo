package org.example.supplier.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SupplierCreateRequest {

    @NotBlank
    private String code;

    @NotBlank
    private String name;

    private String contact;
    private String phone;
    private String address;
    private BigDecimal creditScore;
    private Integer status;
}
