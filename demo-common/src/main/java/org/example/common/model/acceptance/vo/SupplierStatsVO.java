package org.example.common.model.acceptance.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "SupplierStatsVO", description = "按供应商聚合的验收统计")
public class SupplierStatsVO {

    @Schema(description = "供应商名称")
    private String supplierName;

    @Schema(description = "验收单批次数")
    private long batchCount;

    @Schema(description = "已通过数")
    private long passedCount;

    @Schema(description = "整改中/已整改数")
    private long rectifiedCount;

    @Schema(description = "退货数")
    private long returnedCount;

    @Schema(description = "通过率（百分比）")
    public double getPassedRate() {
        return batchCount == 0 ? 0 : round(passedCount * 100.0 / batchCount);
    }

    @Schema(description = "整改率（百分比）")
    public double getRectifyRate() {
        return batchCount == 0 ? 0 : round(rectifiedCount * 100.0 / batchCount);
    }

    @Schema(description = "退货率（百分比）")
    public double getReturnRate() {
        return batchCount == 0 ? 0 : round(returnedCount * 100.0 / batchCount);
    }

    private double round(double v) {
        return Math.round(v * 100.0) / 100.0;
    }
}
