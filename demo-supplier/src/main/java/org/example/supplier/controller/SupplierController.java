package org.example.supplier.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.common.dto.PageResult;
import org.example.common.entity.PageQuery;
import org.example.common.model.supplier.dto.SupplierCreateRequest;
import org.example.common.model.supplier.dto.SupplierUpdateRequest;
import org.example.common.model.supplier.vo.SupplierVO;
import org.example.common.result.Result;
import org.example.supplier.service.SupplierService;
import org.springframework.web.bind.annotation.*;

@Tag(name = "供应商管理", description = "供应商 CRUD 接口")
@RestController
@RequestMapping("/supplier")
@RequiredArgsConstructor
public class SupplierController {

    private final SupplierService supplierService;

    @Operation(summary = "根据 ID 查询供应商")
    @GetMapping("/{id}")
    public Result<SupplierVO> getById(@Parameter(description = "供应商主键 ID") @PathVariable Long id) {
        return Result.success(supplierService.getVoById(id));
    }

    @Operation(summary = "分页查询供应商")
    @GetMapping("/page")
    public Result<PageResult<SupplierVO>> page(PageQuery q) {
        return Result.success(supplierService.pageVo(q));
    }

    @Operation(summary = "新建供应商")
    @PostMapping
    public Result<SupplierVO> create(@Valid @RequestBody SupplierCreateRequest request) {
        return Result.success(supplierService.create(request));
    }

    @Operation(summary = "更新供应商")
    @PutMapping
    public Result<SupplierVO> update(@Valid @RequestBody SupplierUpdateRequest request) {
        return Result.success(supplierService.update(request));
    }

    @Operation(summary = "删除供应商")
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@Parameter(description = "供应商主键 ID") @PathVariable Long id) {
        return Result.success(supplierService.removeById(id));
    }
}
