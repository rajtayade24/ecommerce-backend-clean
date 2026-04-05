package com.example.complaintManagementSystem.service;


import com.example.complaintManagementSystem.dto.ApiMessageResponse;
import com.example.complaintManagementSystem.dto.ComplaintAnalysisResponse;
import com.example.complaintManagementSystem.dto.ComplaintRequest;
import com.example.complaintManagementSystem.dto.ComplaintStatusResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class PythonAiService {

    private final RestTemplate restTemplate;

// RestTemplate is a Spring Boot class used to call external APIs (like your Python AI service)
// from your Java backend.
// It contains fuctions like
// exchannge(url, HttpMethod, request, response)
// getForEntity(url, response)

    @Value("${python.ai.base-url}")
    private String pythonBaseUrl;

    public PythonAiService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public ComplaintAnalysisResponse analyzeComplaint(ComplaintRequest request) {
        String url = pythonBaseUrl + "/api/complaints/analyze";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        // Body, Headers
        HttpEntity<ComplaintRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<ComplaintAnalysisResponse> response =
                restTemplate.exchange(url, HttpMethod.POST, entity, ComplaintAnalysisResponse.class);
//               exchannge( url, httpmethod, request, response )

        return response.getBody();
    }

    public ComplaintStatusResponse getComplaintById(String complaintId) {
        String url = pythonBaseUrl + "/api/complaints/" + complaintId;

        ResponseEntity<ComplaintStatusResponse> response =
                restTemplate.getForEntity(url, ComplaintStatusResponse.class);

        return response.getBody();
    }

    public List<Map<String, Object>> getAllComplaints() {
        String url = pythonBaseUrl + "/api/complaints";

        ResponseEntity<List<Map<String, Object>>> response =
                restTemplate.exchange(
                        url,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<List<Map<String, Object>>>() {
                        }
                );

        return response.getBody();
    }

//    public ApiMessageResponse retrainModels() {
//        String url = pythonBaseUrl + "/api/train";
//
//        ResponseEntity<Map> response =
//                restTemplate.postForEntity(url, null, Map.class);
//
//        Object message = response.getBody() != null ? response.getBody().get("message") : null;
//        String msg = message != null ? message.toString() : "No response";
//        return new ApiMessageResponse(msg);
//    }
}