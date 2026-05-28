package org.example.common.model.acceptance.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.common.entity.BaseEntity;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_operation_log")
@Schema(name = "OperationLog", description = "操作日志实体")
public class OperationLog extends BaseEntity {

    @Schema(description = "操作人 ID")
    private Long userId;

    @Schema(description = "操作人姓名")
    private String userName;

    @Schema(description = "操作时间")
    private LocalDateTime opTime;

    @Schema(description = "操作类型，如 CREATE / UPDATE / DELETE / STATUS_CHANGE / AI_SUMMARY")
    private String opType;

    @Schema(description = "目标对象类型，如 ACCEPTANCE")
    private String targetType;

    @Schema(description = "目标对象 ID")
    private String targetId;

    @Schema(description = "操作摘要")
    private String summary;

    @Schema(description = "变更前 JSON 快照")
    private String beforeJson;

    @Schema(description = "变更后 JSON 快照")
    private String afterJson;
}
