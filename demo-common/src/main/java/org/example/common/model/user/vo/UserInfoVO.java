package org.example.common.model.user.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "UserInfoVO", description = "登录后返回的用户信息（对齐 vben web-antd 的 UserInfo 结构）")
public class UserInfoVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "用户 ID（字符串形式，避免前端 Long 溢出）")
    private String userId;

    @Schema(description = "登录用户名")
    private String username;

    @Schema(description = "真实姓名 / 昵称")
    private String realName;

    @Schema(description = "头像")
    private String avatar;

    @Schema(description = "描述")
    private String desc;

    @Schema(description = "首页路径", example = "/dashboard")
    private String homePath;

    @Schema(description = "角色列表")
    private List<String> roles;

    @Schema(description = "JWT 透传给前端")
    private String token;
}
