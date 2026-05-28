package org.example.common.log;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OperationLogEntry {
    private Long userId;
    private String userName;
    private LocalDateTime opTime;
    private String opType;
    private String targetType;
    private String targetId;
    private String summary;
}
