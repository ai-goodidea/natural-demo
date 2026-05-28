package org.example.supplier.dto;

import cn.hutool.core.bean.BeanUtil;
import org.example.common.model.supplier.dto.SupplierCreateRequest;
import org.example.common.model.supplier.dto.SupplierUpdateRequest;
import org.example.common.model.supplier.entity.Supplier;
import org.example.common.model.supplier.vo.SupplierVO;

public final class SupplierConverter {

    private SupplierConverter() {}

    public static SupplierVO toVO(Supplier entity) {
        if (entity == null) return null;
        SupplierVO vo = new SupplierVO();
        BeanUtil.copyProperties(entity, vo);
        return vo;
    }

    public static Supplier toEntity(SupplierCreateRequest req) {
        Supplier s = new Supplier();
        BeanUtil.copyProperties(req, s);
        return s;
    }

    public static void applyUpdate(SupplierUpdateRequest req, Supplier entity) {
        BeanUtil.copyProperties(req, entity, "id", "createTime", "updateTime");
    }
}
