package org.example.business.acceptance.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.common.entity.BaseEntity;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_acceptance")
public class Acceptance extends BaseEntity {

    private String orderNo;
    private Long supplierId;
    private String supplierName;
    private LocalDate arrivalDate;
    private Long inspectorId;
    private String inspectorName;
    /** 见 AcceptanceStatus */
    private String status;
    private String remark;
    @TableField("summary_json")
    private String summaryJson;
}
