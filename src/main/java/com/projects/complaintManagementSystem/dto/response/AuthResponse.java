package com.projects.complaintManagementSystem.dto.response;

import com.projects.complaintManagementSystem.enums.RoleType;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AuthResponse {
    private String token;
    private Long userId;
    private String fullName;
    private String email;
    private List<RoleType> role;
}
