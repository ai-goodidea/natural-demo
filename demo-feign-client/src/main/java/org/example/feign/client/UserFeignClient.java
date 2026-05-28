package org.example.feign.client;

import org.example.common.result.Result;
import org.example.feign.dto.UserDTO;
import org.example.feign.fallback.UserFeignFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "boot-user", fallback = UserFeignFallback.class, path = "/user")
public interface UserFeignClient {

    @GetMapping("/{id}")
    Result<UserDTO> getById(@PathVariable("id") Long id);
}
