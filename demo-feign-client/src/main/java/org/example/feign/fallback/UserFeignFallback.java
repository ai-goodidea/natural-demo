package org.example.feign.fallback;

import lombok.extern.slf4j.Slf4j;
import org.example.common.result.Result;
import org.example.feign.client.UserFeignClient;
import org.example.feign.dto.UserDTO;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UserFeignFallback implements UserFeignClient {

    @Override
    public Result<UserDTO> getById(Long id) {
        log.warn("UserFeignClient.getById fallback, id={}", id);
        return Result.failed("user service unavailable");
    }
}
