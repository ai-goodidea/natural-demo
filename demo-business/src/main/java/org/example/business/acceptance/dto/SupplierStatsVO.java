package org.example.business.acceptance.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupplierStatsVO {
    private String supplierName;
    private long batchCount;
    private long passedCount;
    private long rectifiedCount;
    private long returnedCount;

    public double getPassedRate() {
        return batchCount == 0 ? 0 : round(passedCount * 100.0 / batchCount);
    }
    public double getRectifyRate() {
        return batchCount == 0 ? 0 : round(rectifiedCount * 100.0 / batchCount);
    }
    public double getReturnRate() {
        return batchCount == 0 ? 0 : round(returnedCount * 100.0 / batchCount);
    }
    private double round(double v) {
        return Math.round(v * 100.0) / 100.0;
    }
}
