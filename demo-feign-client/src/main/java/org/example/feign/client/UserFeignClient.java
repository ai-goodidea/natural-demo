package org.example.feign.client;

import io.swagger.v3.oas.annotations.Hidden;
import org.example.common.model.user.vo.UserVO;
import org.example.common.result.Result;
import org.example.feign.fallback.UserFeignFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Hidden
@FeignClient(name = "boot-user", fallback = UserFeignFallback.class, path = "/user")
public interface UserFeignClient {

    @GetMapping("/{id}")
    Result<UserVO> getById(@PathVariable("id") Long id);
}
