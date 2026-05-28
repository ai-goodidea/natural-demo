package org.example.common.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JwtPayload implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long userId;
    private String username;
    private String nickname;
    private String avatar;
    /** 逗号分隔 */
    private String roles;
}
