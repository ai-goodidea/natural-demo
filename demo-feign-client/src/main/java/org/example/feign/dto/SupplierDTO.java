package org.example.feign.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class SupplierDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private String code;
    private String name;
    private String contact;
    private String phone;
    private String address;
    private BigDecimal creditScore;
    private Integer status;
    private LocalDateTime createTime;
}
