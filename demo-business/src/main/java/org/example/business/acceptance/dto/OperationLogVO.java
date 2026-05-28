package org.example.business.acceptance.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.common.dto.BaseVO;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
public class OperationLogVO extends BaseVO {

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
