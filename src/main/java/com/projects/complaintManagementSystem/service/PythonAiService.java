package com.projects.complaintManagementSystem.service;

import com.projects.complaintManagementSystem.dto.ApiMessageResponse;
import com.projects.complaintManagementSystem.dto.ComplaintAnalysisResponse;
import com.projects.complaintManagementSystem.dto.ComplaintRequest;
import com.projects.complaintManagementSystem.dto.ComplaintStatusResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

@Service
public class PythonAiService {

    private final RestTemplate restTemplate;

    // RestTemplate is a Spring Boot class used to call external APIs (like your
    // Python AI service)
    // from your Java backend.
    // It contains fuctions like
    // exchannge(url, HttpMethod, request, response)
    // getForEntity(url, response)

    @Value("${python.ai.base-url}")
    private String pythonBaseUrl;

    public PythonAiService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public ComplaintAnalysisResponse analyzeComplaint(
            String complaintText,
            String category,
            MultipartFile image,
            Double latitude,
            Double longitude,
            String locationHint
    ) throws IOException {
        String url = pythonBaseUrl + "/api/complaints/analyze";

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

            body.add("complaint_text", complaintText);
            if (category != null) body.add("category", category);
            if (latitude != null) body.add("latitude", latitude);
            if (longitude != null) body.add("longitude", longitude);
            if (locationHint != null) body.add("location_hint", locationHint);

            if (image != null && !image.isEmpty()) {
                if (image.getSize() > 5 * 1024 * 1024) { // 5MB
                    throw new RuntimeException("File size must be less than 5MB");
                }

                String contentType = image.getContentType();

                if (contentType == null ||
                        !(contentType.equals("image/png") ||
                                contentType.equals("image/jpeg") ||
                                contentType.equals("image/webp"))) {

                    throw new RuntimeException("Only PNG, JPG, JPEG, WEBP allowed");
                }

                body.add("image", new MultipartInputStreamFileResource(
                        image.getInputStream(),
                        image.getOriginalFilename()
                ));
            }

            HttpEntity<MultiValueMap<String, Object>> requestEntity =
                    new HttpEntity<>(body, headers);

            ResponseEntity<ComplaintAnalysisResponse> response =
                    restTemplate.postForEntity(url, requestEntity, ComplaintAnalysisResponse.class);

            return response.getBody();
        } catch (ResourceAccessException e) {
            throw new RuntimeException("AI service unreachable (Python server is OFF)");
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new RuntimeException("Error from AI service: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Python AI service is down. Please try again later.", e);
        }
    }

    public ComplaintStatusResponse getComplaintById(String complaintId) {
        String url = pythonBaseUrl + "/api/complaints/" + complaintId;

        ResponseEntity<ComplaintStatusResponse> response = restTemplate.getForEntity(url,
                ComplaintStatusResponse.class);

        return response.getBody();
    }

    public List<Map<String, Object>> getAllComplaints() {
        String url = pythonBaseUrl + "/api/complaints";

        ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Map<String, Object>>>() {
                });

        return response.getBody();
    }

    public ApiMessageResponse retrainModels() {
        String url = pythonBaseUrl + "/api/train";

        ResponseEntity<Map> response =
                restTemplate.postForEntity(url, null, Map.class);

        Object message = response.getBody() != null ?
                response.getBody().get("message") : null;
        String msg = message != null ? message.toString() : "No response";
        return new ApiMessageResponse(msg);
    }


}