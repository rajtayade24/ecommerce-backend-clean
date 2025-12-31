package com.projects.ecommerce.dto.request;

import com.projects.ecommerce.enums.RoleType;
import lombok.Data;

@Data

public class CreateUserDto {

    private String mobile;
    private String email;

    private String password;

    private String name;

    private RoleType role = RoleType.USER;

    private boolean active = true;
}