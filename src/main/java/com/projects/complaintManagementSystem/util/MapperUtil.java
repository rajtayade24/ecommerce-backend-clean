package com.projects.complaintManagementSystem.util;

import com.projects.complaintManagementSystem.dto.AddressDto;
import com.projects.complaintManagementSystem.entity.*;
import com.projects.complaintManagementSystem.enums.PaymentMethodType;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Data
public class MapperUtil {

    private final ModelMapper modelMapper;

    public Address copyAddressFromDto(AddressDto dto) {
        return Address.builder()
                .line1(dto.getLine1())
                .line2(dto.getLine2())
                .city(dto.getCity())
                .state(dto.getState())
                .pincode(dto.getPincode())
                .country(dto.getCountry() == null ? "INDIA" : dto.getCountry())
                .build();
    }

    public AddressDto copyAddressFromEntity(Address src) {
        return AddressDto.builder()
                .line1(src.getLine1())
                .line2(src.getLine2())
                .city(src.getCity())
                .state(src.getState())
                .pincode(src.getPincode())
                .country(src.getCountry())
                .build();
    }

    public BigDecimal nvl(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v;
    }

    public String generateOrderNumber() {
        // Simple readable order number: ORD-YYYYMMDD-HHMMSS-<random4>
        String ts = OffsetDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int rnd = new Random().nextInt(9000) + 1000;
        return "ORD-" + ts + "-" + rnd;
    }

    public String normalizeMobile(String mobile) {
        if (mobile == null) return null;

        mobile = mobile.trim().replaceAll("\\s+", "");

        // remove leading +
        if (mobile.startsWith("+")) {
            mobile = mobile.substring(1);
        }

        // remove leading 0
        if (mobile.startsWith("0")) {
            mobile = mobile.substring(1);
        }

        // if already starts with 91
        if (mobile.startsWith("91")) {
            return "+" + mobile;
        }

        // otherwise assume Indian number
        return "+91" + mobile;
    }
}
