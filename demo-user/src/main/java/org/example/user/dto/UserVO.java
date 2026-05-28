package org.example.user.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.common.dto.BaseVO;

@Data
@EqualsAndHashCode(callSuper = true)
public class UserVO extends BaseVO {

    private String username;
    private String nickname;
    private String email;
    private String phone;
    private String avatar;
    /** 角色，逗号分隔 */
    private String roles;
    /** 1=正常 0=禁用 */
    private Integer status;
}
