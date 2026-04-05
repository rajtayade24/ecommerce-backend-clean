package com.projects.complaintManagementSystem.dto;

import com.projects.complaintManagementSystem.enums.RoleType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private String email;

    private Long id;

    private String mobile;

    private String firstName;
    private String middleName;
    private String lastName;

    private List<AddressDto> addresses = new ArrayList<>();

    private Set<RoleType> roles = new HashSet<>();

    private boolean active = true;

    private String token;

    private OffsetDateTime createdAt = OffsetDateTime.now();
}