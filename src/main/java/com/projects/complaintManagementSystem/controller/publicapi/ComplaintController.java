package com.projects.complaintManagementSystem.controller;

import com.projects.complaintManagementSystem.dto.ApiMessageResponse;
import com.projects.complaintManagementSystem.dto.ComplaintAnalysisResponse;
import com.projects.complaintManagementSystem.dto.ComplaintRequest;
import com.projects.complaintManagementSystem.dto.ComplaintStatusResponse;
import com.projects.complaintManagementSystem.service.PythonAiService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = {
        "http://localhost:5500",
        "http://localhost:8080",
        "http://127.0.0.1:5500",
        "http://localhost:5173",
        "http://10.91.2.29:5173",
        "*"
})
@RestController
@RequestMapping("/api/complaints")
public class ComplaintController {

    private final PythonAiService pythonAiService;

    public ComplaintController(PythonAiService pythonAiService) {
        this.pythonAiService = pythonAiService;
    }

    @PostMapping(value = "/analyze", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ComplaintAnalysisResponse> analyzeComplaint(
            @RequestParam("complaint_text") String complaintText,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "image", required = false) MultipartFile image,
            @RequestParam(value = "latitude", required = false) Double latitude,
            @RequestParam(value = "longitude", required = false) Double longitude,
            @RequestParam(value = "location_hint", required = false) String locationHint
    ) throws IOException {
        return ResponseEntity.ok(
                pythonAiService.analyzeComplaint(
                        complaintText, category, image, latitude, longitude, locationHint
                )
        );
    }

    @GetMapping("/{complaintId}")
    public ResponseEntity<ComplaintStatusResponse> getComplaint(@PathVariable String complaintId) {
        return ResponseEntity.ok(pythonAiService.getComplaintById(complaintId));
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllComplaints() {
        return ResponseEntity.ok(pythonAiService.getAllComplaints());
    }

     @PostMapping("/train")
     public ResponseEntity<ApiMessageResponse> retrainModels() {
     return ResponseEntity.ok(pythonAiService.retrainModels());
     }
}