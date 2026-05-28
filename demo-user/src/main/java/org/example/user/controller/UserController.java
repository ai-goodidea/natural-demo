package org.example.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.common.dto.PageResult;
import org.example.common.entity.PageQuery;
import org.example.common.result.Result;
import org.example.user.dto.UserCreateRequest;
import org.example.user.dto.UserUpdateRequest;
import org.example.user.dto.UserVO;
import org.example.user.service.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}")
    public Result<UserVO> getById(@PathVariable Long id) {
        return Result.success(userService.getVoById(id));
    }

    @GetMapping("/page")
    public Result<PageResult<UserVO>> page(PageQuery q) {
        return Result.success(userService.pageVo(q));
    }

    @PostMapping
    public Result<UserVO> create(@Valid @RequestBody UserCreateRequest request) {
        return Result.success(userService.create(request));
    }

    @PutMapping
    public Result<UserVO> update(@Valid @RequestBody UserUpdateRequest request) {
        return Result.success(userService.update(request));
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.success(userService.removeById(id));
    }
}
