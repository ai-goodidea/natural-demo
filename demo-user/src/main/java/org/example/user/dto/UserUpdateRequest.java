package org.example.user.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserUpdateRequest {

    @NotNull
    private Long id;

    private String nickname;
    private String email;
    private String phone;
    private String avatar;
    private String roles;
    private Integer status;
}
