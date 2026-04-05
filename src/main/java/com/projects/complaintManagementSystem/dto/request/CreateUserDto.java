package com.projects.complaintManagementSystem.dto.request;

import com.projects.complaintManagementSystem.dto.AddressDto;
import com.projects.complaintManagementSystem.enums.RoleType;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data

public class CreateUserDto {

    private String firstName;
    private String middleName;
    private String lastName;

    private String mobile;
    private String email;

    private String password;

    private List<AddressDto> addresses = new ArrayList<>();
}