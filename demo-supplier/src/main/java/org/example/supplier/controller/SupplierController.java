package org.example.supplier.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.common.dto.PageResult;
import org.example.common.entity.PageQuery;
import org.example.common.result.Result;
import org.example.supplier.dto.SupplierCreateRequest;
import org.example.supplier.dto.SupplierUpdateRequest;
import org.example.supplier.dto.SupplierVO;
import org.example.supplier.service.SupplierService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/supplier")
@RequiredArgsConstructor
public class SupplierController {

    private final SupplierService supplierService;

    @GetMapping("/{id}")
    public Result<SupplierVO> getById(@PathVariable Long id) {
        return Result.success(supplierService.getVoById(id));
    }

    @GetMapping("/page")
    public Result<PageResult<SupplierVO>> page(PageQuery q) {
        return Result.success(supplierService.pageVo(q));
    }

    @PostMapping
    public Result<SupplierVO> create(@Valid @RequestBody SupplierCreateRequest request) {
        return Result.success(supplierService.create(request));
    }

    @PutMapping
    public Result<SupplierVO> update(@Valid @RequestBody SupplierUpdateRequest request) {
        return Result.success(supplierService.update(request));
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.success(supplierService.removeById(id));
    }
}
