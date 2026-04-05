package com.example.complaintManagementSystem.dto;


import lombok.Data;

import java.util.Map;

@Data
public class ComplaintStatusResponse {
    private String complaintId;
    private String complaintText;
    private String department;
    private Map<String, Object> sentiment;
    private String priority;
    private String status;
    private String imageUrl;
    private Double latitude;
    private Double longitude;
    private String locationHint;
    private String createdAt;
}