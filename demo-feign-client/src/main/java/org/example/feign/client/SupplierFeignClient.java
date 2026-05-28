package org.example.feign.client;

import io.swagger.v3.oas.annotations.Hidden;
import org.example.common.model.supplier.vo.SupplierVO;
import org.example.common.result.Result;
import org.example.feign.fallback.SupplierFeignFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Hidden
@FeignClient(name = "boot-supplier", fallback = SupplierFeignFallback.class, path = "/supplier")
public interface SupplierFeignClient {

    @GetMapping("/{id}")
    Result<SupplierVO> getById(@PathVariable("id") Long id);
}
