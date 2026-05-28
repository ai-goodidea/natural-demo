package org.example.common.model.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(name = "UserUpdateRequest", description = "更新用户请求")
public class UserUpdateRequest {

    @NotNull
    @Schema(description = "用户主键 ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;

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

    @Schema(description = "状态：1=正常 0=禁用")
    private Integer status;
}
