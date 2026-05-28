package org.example.supplier.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.common.dto.PageResult;
import org.example.common.entity.PageQuery;
import org.example.supplier.dto.SupplierCreateRequest;
import org.example.supplier.dto.SupplierUpdateRequest;
import org.example.supplier.dto.SupplierVO;
import org.example.supplier.entity.Supplier;

public interface SupplierService extends IService<Supplier> {

    SupplierVO getVoById(Long id);

    PageResult<SupplierVO> pageVo(PageQuery query);

    SupplierVO create(SupplierCreateRequest request);

    SupplierVO update(SupplierUpdateRequest request);
}
