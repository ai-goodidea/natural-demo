package org.example.supplier.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.common.dto.PageResult;
import org.example.common.entity.PageQuery;
import org.example.common.exception.BusinessException;
import org.example.common.result.ResultCode;
import org.example.supplier.dto.SupplierConverter;
import org.example.supplier.dto.SupplierCreateRequest;
import org.example.supplier.dto.SupplierUpdateRequest;
import org.example.supplier.dto.SupplierVO;
import org.example.supplier.entity.Supplier;
import org.example.supplier.mapper.SupplierMapper;
import org.example.supplier.service.SupplierService;
import org.springframework.stereotype.Service;

@Service
public class SupplierServiceImpl extends ServiceImpl<SupplierMapper, Supplier> implements SupplierService {

    @Override
    public SupplierVO getVoById(Long id) {
        Supplier entity = getById(id);
        if (entity == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        return SupplierConverter.toVO(entity);
    }

    @Override
    public PageResult<SupplierVO> pageVo(PageQuery query) {
        LambdaQueryWrapper<Supplier> q = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(query.getKeyword())) {
            String kw = query.getKeyword();
            q.like(Supplier::getName, kw)
                    .or().like(Supplier::getCode, kw)
                    .or().like(Supplier::getContact, kw);
        }
        q.orderByDesc(Supplier::getCreateTime);
        Page<Supplier> page = page(new Page<>(query.getCurrent(), query.getSize()), q);
        return PageResult.from(page, SupplierConverter::toVO);
    }

    @Override
    public SupplierVO create(SupplierCreateRequest request) {
        Supplier entity = SupplierConverter.toEntity(request);
        save(entity);
        return SupplierConverter.toVO(entity);
    }

    @Override
    public SupplierVO update(SupplierUpdateRequest request) {
        Supplier entity = getById(request.getId());
        if (entity == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        SupplierConverter.applyUpdate(request, entity);
        updateById(entity);
        return SupplierConverter.toVO(entity);
    }
}
