package org.example.common.model.user.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "LoginResult", description = "登录结果")
public class LoginResult {

    @Schema(description = "JWT 访问令牌")
    private String accessToken;

    @Schema(description = "登录后用户信息")
    private UserInfoVO userInfo;
}
