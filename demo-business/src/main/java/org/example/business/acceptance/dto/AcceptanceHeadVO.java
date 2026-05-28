package org.example.business.acceptance.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.common.dto.BaseVO;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
public class AcceptanceHeadVO extends BaseVO {

    private String orderNo;
    private Long supplierId;
    private String supplierName;
    private LocalDate arrivalDate;
    private Long inspectorId;
    private String inspectorName;
    /** 见 AcceptanceStatus */
    private String status;
    private String remark;
    private String summaryJson;
}
