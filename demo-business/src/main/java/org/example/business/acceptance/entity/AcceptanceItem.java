package org.example.business.acceptance.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.common.entity.BaseEntity;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_acceptance_item")
public class AcceptanceItem extends BaseEntity {

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
