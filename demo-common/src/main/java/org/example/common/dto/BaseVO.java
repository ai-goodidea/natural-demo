package org.example.common.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class BaseVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
