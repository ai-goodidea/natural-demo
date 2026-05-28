package org.example.common.model.acceptance.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.common.entity.BaseEntity;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_acceptance")
@Schema(name = "Acceptance", description = "验收单实体")
public class Acceptance extends BaseEntity {

    @Schema(description = "采购订单号")
    private String orderNo;

    @Schema(description = "供应商 ID")
    private Long supplierId;

    @Schema(description = "供应商名称（冗余）")
    private String supplierName;

    @Schema(description = "到货日期")
    private LocalDate arrivalDate;

    @Schema(description = "验收人 ID")
    private Long inspectorId;

    @Schema(description = "验收人姓名")
    private String inspectorName;

    @Schema(description = "状态：PENDING/IN_PROGRESS/PASSED/RETURNED/RECTIFYING/RECTIFIED")
    private String status;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "AI 摘要 JSON 原文")
    @TableField("summary_json")
    private String summaryJson;
}
