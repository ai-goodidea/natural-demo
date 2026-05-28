package org.example.user.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 对齐 vben web-antd 的 UserInfo 结构。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String userId;
    private String username;
    private String realName;
    private String avatar;
    private String desc;
    private String homePath;
    private List<String> roles;
    /** 透传给前端 */
    private String token;
}
