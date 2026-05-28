package org.example.feign.fallback;

import lombok.extern.slf4j.Slf4j;
import org.example.common.model.supplier.vo.SupplierVO;
import org.example.common.result.Result;
import org.example.feign.client.SupplierFeignClient;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SupplierFeignFallback implements SupplierFeignClient {

    @Override
    public Result<SupplierVO> getById(Long id) {
        log.warn("SupplierFeignClient.getById fallback, id={}", id);
        return Result.failed("supplier service unavailable");
    }
}
