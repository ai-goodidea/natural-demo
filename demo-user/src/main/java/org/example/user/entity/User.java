package org.example.user.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.common.entity.BaseEntity;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_user")
public class User extends BaseEntity {

    private String username;
    private String password;
    private String nickname;
    private String email;
    private String phone;
    private String avatar;
    /** 角色，逗号分隔，例：admin,inspector */
    private String roles;
    /** 1=正常 0=禁用 */
    private Integer status;
}
