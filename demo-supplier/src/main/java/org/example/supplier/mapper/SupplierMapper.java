package org.example.supplier.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.common.model.supplier.entity.Supplier;

@Mapper
public interface SupplierMapper extends BaseMapper<Supplier> {
}
