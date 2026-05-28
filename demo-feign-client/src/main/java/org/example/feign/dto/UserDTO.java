package org.example.feign.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class UserDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private String username;
    private String nickname;
    private String email;
    private String phone;
    private Integer status;
    private LocalDateTime createTime;
}
