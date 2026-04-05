package com.projects.complaintManagementSystem.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressDto {
    private Long id;
    private Long userId;
    private String line1;
    private String line2;

    private String city;
    private String state;
    private String pincode;

    private String country = "INDIA";

}

