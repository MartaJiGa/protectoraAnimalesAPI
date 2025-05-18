package com.svalero.protectoraAnimales.domain.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserOutDTO {
    private String username;
    private String password;
    private boolean active;
}
