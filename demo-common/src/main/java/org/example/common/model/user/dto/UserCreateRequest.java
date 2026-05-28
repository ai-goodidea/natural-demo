package org.example.common.model.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(name = "UserCreateRequest", description = "新建用户请求")
public class UserCreateRequest {

    @NotBlank
    @Schema(description = "登录用户名", requiredMode = Schema.RequiredMode.REQUIRED, example = "admin")
    private String username;

    @Schema(description = "登录密码（demo 不强制）")
    private String password;

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "头像 URL")
    private String avatar;

    @Schema(description = "角色，逗号分隔")
    private String roles;

    @Schema(description = "状态：1=正常 0=禁用", example = "1")
    private Integer status;
}
