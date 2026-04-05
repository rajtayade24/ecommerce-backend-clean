package com.example.complaintManagementSystem.dto.request;

import com.example.complaintManagementSystem.enums.RoleType;
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