package org.example.business.acceptance.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
public class AcceptanceVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private AcceptanceHeadVO head;
    private List<AcceptanceItemVO> items;
    private List<RectificationVO> rectifications;
}
