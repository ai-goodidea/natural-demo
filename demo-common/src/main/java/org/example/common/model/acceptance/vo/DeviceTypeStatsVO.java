package org.example.common.model.acceptance.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "DeviceTypeStatsVO", description = "按设备类型聚合的验收统计")
public class DeviceTypeStatsVO {

    @Schema(description = "设备类型")
    private String deviceType;

    @Schema(description = "数量合计")
    private BigDecimal totalQty;

    @Schema(description = "合格数量合计")
    private BigDecimal qualifiedQty;

    @Schema(description = "合格率（百分比，保留两位小数）")
    private double qualifiedRate;
}
