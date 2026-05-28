package org.example.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.common.dto.PageResult;
import org.example.common.entity.PageQuery;
import org.example.common.model.user.dto.UserCreateRequest;
import org.example.common.model.user.dto.UserUpdateRequest;
import org.example.common.model.user.vo.UserVO;
import org.example.common.result.Result;
import org.example.user.service.UserService;
import org.springframework.web.bind.annotation.*;

@Tag(name = "用户管理", description = "用户 CRUD 接口")
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "根据 ID 查询用户")
    @GetMapping("/{id}")
    public Result<UserVO> getById(@Parameter(description = "用户主键 ID") @PathVariable Long id) {
        return Result.success(userService.getVoById(id));
    }

    @Operation(summary = "分页查询用户")
    @GetMapping("/page")
    public Result<PageResult<UserVO>> page(PageQuery q) {
        return Result.success(userService.pageVo(q));
    }

    @Operation(summary = "新建用户")
    @PostMapping
    public Result<UserVO> create(@Valid @RequestBody UserCreateRequest request) {
        return Result.success(userService.create(request));
    }

    @Operation(summary = "更新用户")
    @PutMapping
    public Result<UserVO> update(@Valid @RequestBody UserUpdateRequest request) {
        return Result.success(userService.update(request));
    }

    @Operation(summary = "删除用户")
    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@Parameter(description = "用户主键 ID") @PathVariable Long id) {
        return Result.success(userService.removeById(id));
    }
}
