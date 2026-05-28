package org.example.supplier.dto;

import cn.hutool.core.bean.BeanUtil;
import org.example.supplier.entity.Supplier;

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
