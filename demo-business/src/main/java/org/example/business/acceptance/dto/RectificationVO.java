package org.example.business.acceptance.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.common.dto.BaseVO;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
public class RectificationVO extends BaseVO {

    private Long acceptanceId;
    private String content;
    private String owner;
    private LocalDate deadline;
    private Integer finished;
}
