package org.example.common.model.acceptance.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

@Data
@Schema(name = "StatusChangeRequest", description = "验收单状态流转请求")
public class StatusChangeRequest {

    @NotBlank
    @Schema(description = "目标状态", requiredMode = Schema.RequiredMode.REQUIRED,
            example = "RECTIFYING")
    private String newStatus;

    @Schema(description = "整改内容（流转到 RECTIFYING 时必填）")
    private String rectificationContent;

    @Schema(description = "整改责任人")
    private String rectificationOwner;

    @Schema(description = "整改截止日期")
    private LocalDate rectificationDeadline;
}
