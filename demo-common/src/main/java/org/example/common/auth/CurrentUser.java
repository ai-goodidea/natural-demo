package org.example.common.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CurrentUser {

    private Long userId;
    private String username;
    /** 逗号分隔 */
    private String roles;

    public static CurrentUser anonymous() {
        return CurrentUser.builder().userId(0L).username("anonymous").roles("").build();
    }
}
