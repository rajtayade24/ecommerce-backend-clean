package com.projects.ecommerce.service;

public interface OtpService {

    String normalize(String identifier);

    void sendOtp(String identifier);

    boolean verifyOtp(String identifier, String submitted);

    boolean isVerified(String identifier);

    void consumeVerified(String identifier);
}
