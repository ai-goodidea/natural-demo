package org.example.common.model.acceptance.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.common.entity.BaseEntity;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_acceptance_item")
@Schema(name = "AcceptanceItem", description = "验收明细行实体")
public class AcceptanceItem extends BaseEntity {

    @Schema(description = "所属验收单 ID")
    private Long acceptanceId;

    @Schema(description = "设备编码")
    private String deviceCode;

    @Schema(description = "设备名称")
    private String deviceName;

    @Schema(description = "设备类型")
    private String deviceType;

    @Schema(description = "规格型号")
    private String spec;

    @Schema(description = "单位")
    private String unit;

    @Schema(description = "数量")
    private BigDecimal qty;

    @Schema(description = "验收结果：QUALIFIED / CONCESSION / RETURNED")
    private String result;

    @Schema(description = "缺陷描述")
    private String defectDesc;
}
