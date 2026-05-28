package org.example.common.model.user.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.common.entity.BaseEntity;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_user")
@Schema(name = "User", description = "用户实体")
public class User extends BaseEntity {

    @Schema(description = "登录用户名", example = "admin")
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

    @Schema(description = "角色，逗号分隔，例：admin,inspector")
    private String roles;

    @Schema(description = "状态：1=正常 0=禁用", example = "1")
    private Integer status;
}
