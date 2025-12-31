package com.projects.ecommerce.dto;

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
    private String name;
    private String address;

    private String city;
    private String state;
    private String pincode;

    private String country = "INDIA";
    private String phone;
    private String label; // home/work

    private boolean primaryAddress;
}

