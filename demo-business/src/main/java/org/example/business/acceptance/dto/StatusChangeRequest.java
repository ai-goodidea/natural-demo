package org.example.business.acceptance.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

@Data
public class StatusChangeRequest {
    @NotBlank
    private String newStatus;

    /** 进入"整改中"时必填 */
    private String rectificationContent;
    private String rectificationOwner;
    private LocalDate rectificationDeadline;
}
