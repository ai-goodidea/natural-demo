package org.example.business.acceptance.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.common.dto.BaseVO;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
public class AcceptanceItemVO extends BaseVO {

    private Long acceptanceId;
    private String deviceCode;
    private String deviceName;
    private String deviceType;
    private String spec;
    private String unit;
    private BigDecimal qty;
    /** QUALIFIED / CONCESSION / RETURNED */
    private String result;
    private String defectDesc;
}
