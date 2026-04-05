package com.example.complaintManagementSystem.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ComplaintRequest {

    @NotBlank
    private String complaintText;

    private String imageUrl;
    private Double latitude;
    private Double longitude;
    private String locationHint;
}