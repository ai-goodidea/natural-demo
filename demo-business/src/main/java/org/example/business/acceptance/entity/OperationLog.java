package org.example.business.acceptance.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.common.entity.BaseEntity;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_operation_log")
public class OperationLog extends BaseEntity {

    private Long userId;
    private String userName;
    private LocalDateTime opTime;
    private String opType;
    private String targetType;
    private String targetId;
    private String summary;
    private String beforeJson;
    private String afterJson;
}
