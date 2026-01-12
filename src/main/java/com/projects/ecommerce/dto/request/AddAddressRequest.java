package com.projects.ecommerce.dto.request;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddAddressRequest {
    private String name;
    private String line1;
    private String line2;

    private String city;
    private String state;
    private String pincode;

    private String country = "INDIA";
    private String phone;
    private String label; // home/work

}
