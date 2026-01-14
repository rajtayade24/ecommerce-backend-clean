package com.projects.ecommerce.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeedbackResponse {
    private Long id;
    private Long userId;
    private String phone;
    private String title;
    private String message;
    private int rating;
    private String status;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}