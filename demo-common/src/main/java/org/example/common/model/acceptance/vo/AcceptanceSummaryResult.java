package org.example.common.model.acceptance.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "AcceptanceSummaryResult", description = "AI 生成的验收摘要结论")
public class AcceptanceSummaryResult implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "设备信息汇总")
    private String deviceSummary;

    @Schema(description = "验收项目概述")
    private String inspectionOverview;

    @Schema(description = "发现的问题")
    private String issues;

    @Schema(description = "最终结论：QUALIFIED / CONCESSION / RETURNED")
    private String conclusion;

    @Schema(description = "结论中文文案")
    private String conclusionText;
}
