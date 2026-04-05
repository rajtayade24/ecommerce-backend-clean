package com.projects.complaintManagementSystem.dto;


import lombok.Data;

import java.util.Map;

@Data
public class ComplaintAnalysisResponse {
    private String complaintId;
    private String complaintText;
    private String imageUrl;
    private Double latitude;
    private Double longitude;
    private String locationHint;
    private Map<String, Object> analysis;
    private String status;
    private String createdAt;
}