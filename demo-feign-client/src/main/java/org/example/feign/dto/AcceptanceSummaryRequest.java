package org.example.feign.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AcceptanceSummaryRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String orderNo;
    private String supplierName;
    private String arrivalDate;
    private String remark;
    private List<Item> items;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Item {
        private String deviceCode;
        private String deviceName;
        private String spec;
        private String unit;
        private String qty;
        private String result;
        private String defectDesc;
    }
}
