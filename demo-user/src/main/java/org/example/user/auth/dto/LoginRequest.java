package org.example.user.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {

    @NotBlank
    private String username;

    /** 本 demo 不校验密码，留字段是为了和前端表单兼容 */
    private String password;
}
