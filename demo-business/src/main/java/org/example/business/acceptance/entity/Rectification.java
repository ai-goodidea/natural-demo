package org.example.business.acceptance.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.common.entity.BaseEntity;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_rectification")
public class Rectification extends BaseEntity {

    private Long acceptanceId;
    private String content;
    private String owner;
    private LocalDate deadline;
    private Integer finished;
}
