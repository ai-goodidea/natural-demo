package org.example.business.acceptance.dto;

import lombok.Data;

@Data
public class AcceptanceQuery {
    private long current = 1;
    private long size = 10;
    private String orderNo;
    private String supplierName;
    private String status;
}
