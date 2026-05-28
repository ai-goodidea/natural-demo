package org.example.common.model.user.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.common.dto.BaseVO;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(name = "UserVO", description = "用户视图对象")
public class UserVO extends BaseVO {

    @Schema(description = "登录用户名")
    private String username;

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
