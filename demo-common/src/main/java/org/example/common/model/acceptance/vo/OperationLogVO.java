package org.example.common.model.acceptance.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.common.dto.BaseVO;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(name = "OperationLogVO", description = "操作日志视图对象")
public class OperationLogVO extends BaseVO {

    @Schema(description = "操作人 ID")
    private Long userId;

    @Schema(description = "操作人姓名")
    private String userName;

    @Schema(description = "操作时间")
    private LocalDateTime opTime;

    @Schema(description = "操作类型")
    private String opType;

    @Schema(description = "目标对象类型")
    private String targetType;

    @Schema(description = "目标对象 ID")
    private String targetId;

    @Schema(description = "操作摘要")
    private String summary;

    @Schema(description = "变更前 JSON")
    private String beforeJson;

    @Schema(description = "变更后 JSON")
    private String afterJson;
}
