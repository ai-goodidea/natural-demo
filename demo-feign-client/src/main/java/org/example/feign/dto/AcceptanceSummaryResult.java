package org.example.feign.dto;

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
public class AcceptanceSummaryResult implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 设备信息汇总 */
    private String deviceSummary;
    /** 验收项目概述 */
    private String inspectionOverview;
    /** 发现的问题 */
    private String issues;
    /** 最终结论：QUALIFIED / CONCESSION / RETURNED */
    private String conclusion;
    /** 结论中文文案 */
    private String conclusionText;
}
