package org.example.business.acceptance.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class AcceptanceSaveRequest {

    private Long id;

    @NotBlank
    private String orderNo;

    private Long supplierId;
    private String supplierName;
    private LocalDate arrivalDate;
    private Long inspectorId;
    private String inspectorName;
    private String remark;

    private List<ItemPayload> items;

    @Data
    public static class ItemPayload {
        private Long id;
        private String deviceCode;
        private String deviceName;
        private String deviceType;
        private String spec;
        private String unit;
        private java.math.BigDecimal qty;
        private String result;
        private String defectDesc;
    }
}
