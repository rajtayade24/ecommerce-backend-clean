package com.projects.ecommerce.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StripeResponse {
    private String status;
    private String message;
    private String sessionId;
    private String sessionUrl;
}
