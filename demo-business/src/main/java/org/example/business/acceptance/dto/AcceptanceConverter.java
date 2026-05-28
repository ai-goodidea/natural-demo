package org.example.business.acceptance.dto;

import cn.hutool.core.bean.BeanUtil;
import org.example.business.acceptance.entity.Acceptance;
import org.example.business.acceptance.entity.AcceptanceItem;
import org.example.business.acceptance.entity.OperationLog;
import org.example.business.acceptance.entity.Rectification;

public final class AcceptanceConverter {

    private AcceptanceConverter() {}

    public static AcceptanceHeadVO toHeadVO(Acceptance e) {
        if (e == null) return null;
        AcceptanceHeadVO vo = new AcceptanceHeadVO();
        BeanUtil.copyProperties(e, vo);
        return vo;
    }

    public static AcceptanceItemVO toItemVO(AcceptanceItem e) {
        if (e == null) return null;
        AcceptanceItemVO vo = new AcceptanceItemVO();
        BeanUtil.copyProperties(e, vo);
        return vo;
    }

    public static RectificationVO toRectificationVO(Rectification e) {
        if (e == null) return null;
        RectificationVO vo = new RectificationVO();
        BeanUtil.copyProperties(e, vo);
        return vo;
    }

    public static OperationLogVO toOperationLogVO(OperationLog e) {
        if (e == null) return null;
        OperationLogVO vo = new OperationLogVO();
        BeanUtil.copyProperties(e, vo);
        return vo;
    }
}
