package org.example.business.acceptance.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceTypeStatsVO {
    private String deviceType;
    private BigDecimal totalQty;
    private BigDecimal qualifiedQty;
    private double qualifiedRate;
}
