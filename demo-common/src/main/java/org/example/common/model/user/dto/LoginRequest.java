package org.example.common.model.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(name = "LoginRequest", description = "登录请求")
public class LoginRequest {

    @NotBlank
    @Schema(description = "登录用户名", requiredMode = Schema.RequiredMode.REQUIRED, example = "admin")
    private String username;

    @Schema(description = "登录密码（demo 不校验）")
    private String password;
}
