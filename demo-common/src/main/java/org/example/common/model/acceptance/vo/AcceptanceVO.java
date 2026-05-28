package org.example.common.model.acceptance.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
@Schema(name = "AcceptanceVO", description = "验收单详情（含头信息、明细、整改记录）")
public class AcceptanceVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "头信息")
    private AcceptanceHeadVO head;

    @Schema(description = "明细行列表")
    private List<AcceptanceItemVO> items;

    @Schema(description = "整改记录列表")
    private List<RectificationVO> rectifications;
}
