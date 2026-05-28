package org.example.user.auth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.common.auth.AuthConstant;
import org.example.common.auth.UserContextHolder;
import org.example.common.exception.BusinessException;
import org.example.common.model.user.dto.LoginRequest;
import org.example.common.model.user.vo.LoginResult;
import org.example.common.model.user.vo.UserInfoVO;
import org.example.common.result.Result;
import org.example.common.result.ResultCode;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "认证授权", description = "登录、登出、用户信息查询")
@RestController
@RequestMapping("/user/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "登录并签发 JWT")
    @PostMapping("/login")
    public Result<LoginResult> login(@Valid @RequestBody LoginRequest req) {
        return Result.success(authService.login(req));
    }

    @Operation(summary = "获取当前用户信息",
            description = "通过网关注入的 X-User-Id 查用户信息（来自 redis / DB）。token 失效后 redis 无数据 → 直接 401，前端走重新登录。")
    @GetMapping("/userinfo")
    public Result<UserInfoVO> userinfo(HttpServletRequest req) {
        String token = extractToken(req);
        UserInfoVO cached = authService.getUserInfo(token);
        if (cached != null) {
            cached.setToken(token);
            return Result.success(cached);
        }
        Long uid = UserContextHolder.get().getUserId();
        if (uid == null || uid == 0L) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }
        UserInfoVO fresh = authService.getUserInfoByUserId(uid);
        if (fresh == null) throw new BusinessException(ResultCode.UNAUTHORIZED);
        fresh.setToken(token);
        return Result.success(fresh);
    }

    @Operation(summary = "登出（清除 redis token）")
    @PostMapping("/logout")
    public Result<Void> logout(HttpServletRequest req) {
        authService.logout(extractToken(req));
        return Result.success();
    }

    @Operation(summary = "获取当前用户的权限码列表")
    @GetMapping("/codes")
    public Result<List<String>> codes() {
        return Result.success(authService.codes(UserContextHolder.get().getRoles()));
    }

    @Operation(summary = "获取后端动态菜单（demo：返回空，由前端走静态路由）")
    @GetMapping("/menu")
    public Result<List<Object>> menu() {
        return Result.success(List.of());
    }

    private String extractToken(HttpServletRequest req) {
        String h = req.getHeader(AuthConstant.TOKEN_HEADER);
        if (h == null) return null;
        return h.startsWith(AuthConstant.TOKEN_PREFIX) ? h.substring(AuthConstant.TOKEN_PREFIX.length()) : h;
    }
}
