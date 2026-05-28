package org.example.feign.fallback;

import lombok.extern.slf4j.Slf4j;
import org.example.common.result.Result;
import org.example.feign.client.SupplierFeignClient;
import org.example.feign.dto.SupplierDTO;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SupplierFeignFallback implements SupplierFeignClient {

    @Override
    public Result<SupplierDTO> getById(Long id) {
        log.warn("SupplierFeignClient.getById fallback, id={}", id);
        return Result.failed("supplier service unavailable");
    }
}
