package org.example.common.model.acceptance.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.common.dto.BaseVO;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(name = "RectificationVO", description = "整改记录视图对象")
public class RectificationVO extends BaseVO {

    @Schema(description = "所属验收单 ID")
    private Long acceptanceId;

    @Schema(description = "整改内容")
    private String content;

    @Schema(description = "整改责任人")
    private String owner;

    @Schema(description = "整改截止日期")
    private LocalDate deadline;

    @Schema(description = "完成标志：0=未完成 1=已完成")
    private Integer finished;
}
