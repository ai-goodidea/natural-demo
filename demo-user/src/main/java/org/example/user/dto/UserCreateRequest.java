package org.example.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserCreateRequest {

    @NotBlank
    private String username;

    /** 本 demo 不强制 */
    private String password;

    private String nickname;
    private String email;
    private String phone;
    private String avatar;
    private String roles;
    private Integer status;
}
