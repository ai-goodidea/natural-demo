package org.example.user.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.common.auth.AuthConstant;
import org.example.common.auth.UserContextHolder;
import org.example.common.exception.BusinessException;
import org.example.common.result.Result;
import org.example.common.result.ResultCode;
import org.example.user.auth.dto.LoginRequest;
import org.example.user.auth.dto.LoginResult;
import org.example.user.auth.dto.UserInfoVO;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public Result<LoginResult> login(@Valid @RequestBody LoginRequest req) {
        return Result.success(authService.login(req));
    }

    /**
     * 通过网关注入的 X-User-Id 查用户信息（来自 redis / DB）。
     * 这样保证 token 失效后 redis 里没数据 → userinfo 直接 401，让前端走重新登录。
     */
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

    @PostMapping("/logout")
    public Result<Void> logout(HttpServletRequest req) {
        authService.logout(extractToken(req));
        return Result.success();
    }

    @GetMapping("/codes")
    public Result<List<String>> codes() {
        return Result.success(authService.codes(UserContextHolder.get().getRoles()));
    }

    /** 静态菜单 —— vben 期望 /menu/all，gateway 路由 /user/menu 给这里 */
    @GetMapping("/menu")
    public Result<List<Object>> menu() {
        // 菜单走前端静态路由，这里返回空数组；vben 在没远端菜单时会用静态路由
        return Result.success(List.of());
    }

    private String extractToken(HttpServletRequest req) {
        String h = req.getHeader(AuthConstant.TOKEN_HEADER);
        if (h == null) return null;
        return h.startsWith(AuthConstant.TOKEN_PREFIX) ? h.substring(AuthConstant.TOKEN_PREFIX.length()) : h;
    }
}
