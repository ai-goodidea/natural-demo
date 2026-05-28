package org.example.supplier.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.common.dto.PageResult;
import org.example.common.entity.PageQuery;
import org.example.common.model.supplier.dto.SupplierCreateRequest;
import org.example.common.model.supplier.dto.SupplierUpdateRequest;
import org.example.common.model.supplier.vo.SupplierVO;
import org.example.common.model.supplier.entity.Supplier;

public interface SupplierService extends IService<Supplier> {

    SupplierVO getVoById(Long id);

    PageResult<SupplierVO> pageVo(PageQuery query);

    SupplierVO create(SupplierCreateRequest request);

    SupplierVO update(SupplierUpdateRequest request);
}
