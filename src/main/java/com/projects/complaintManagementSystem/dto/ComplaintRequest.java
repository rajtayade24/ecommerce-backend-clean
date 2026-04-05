package com.projects.complaintManagementSystem.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ComplaintRequest {
    @NotBlank
    private String complaintText;
    private String category;
    private MultipartFile image;
    private Double latitude;
    private Double longitude;
    private String locationHint;
}