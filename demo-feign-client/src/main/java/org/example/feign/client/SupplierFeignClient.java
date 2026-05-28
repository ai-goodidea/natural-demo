package org.example.feign.client;

import org.example.common.result.Result;
import org.example.feign.dto.SupplierDTO;
import org.example.feign.fallback.SupplierFeignFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "boot-supplier", fallback = SupplierFeignFallback.class, path = "/supplier")
public interface SupplierFeignClient {

    @GetMapping("/{id}")
    Result<SupplierDTO> getById(@PathVariable("id") Long id);
}
