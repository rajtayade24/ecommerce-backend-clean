package com.projects.ecommerce.controller.publicapi;

import com.projects.ecommerce.dto.request.FeedbackRequest;
import com.projects.ecommerce.dto.response.FeedbackResponse;
import com.projects.ecommerce.enums.FeedbackStatus;
import com.projects.ecommerce.service.FeedbackService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = {
        "http://localhost:5500",
        "http://localhost:8080",
        "http://127.0.0.1:5500",
        "http://localhost:5173",
        "http://10.91.2.29:5173",
        "http://10.91.2.29:5173/instagram-clone",
        "https://social-media-frontend-nbdo.vercel.app",
        "*"
})
@RestController
@RequestMapping("/feedbacks")
@RequiredArgsConstructor
public class FeedbackController {
    private final FeedbackService feedbackService;

    @PostMapping
    public ResponseEntity<FeedbackResponse> create(@Valid @RequestBody FeedbackRequest req) {
        FeedbackResponse resp = feedbackService.createFeedback(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }

    @GetMapping
    public ResponseEntity<Page<FeedbackResponse>> list(
            @RequestParam(value = "rating", required = false) Integer rating,
            @RequestParam(value = "productId", required = false) Long productId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size,
            @RequestParam(value = "sort", defaultValue = "createdAt,desc") String sort) {

        Sort sortObj = Sort.by(Sort.Order.desc("createdAt"));
        // try to parse sort param like "field,asc"
        try {
            String[] parts = sort.split(",");
            if (parts.length == 2) sortObj = Sort.by(Sort.Order.by(parts[0]).with(Sort.Direction.fromString(parts[1])));
            else sortObj = Sort.by(sort);
        } catch (Exception ignored) { /* fallback used */ }

        Pageable pageable = PageRequest.of(page, size, sortObj);
        Page<FeedbackResponse> pageResp = feedbackService.listFeedbacks(rating, productId, pageable);
        return ResponseEntity.ok(pageResp);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FeedbackResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(feedbackService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FeedbackResponse> update(@PathVariable Long id, @Valid @RequestBody FeedbackRequest req) {
        return ResponseEntity.ok(feedbackService.updateFeedback(id, req));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<FeedbackResponse> updateStatus(@PathVariable Long id,
                                                         @RequestParam("status") String  status) {

        return ResponseEntity.ok(feedbackService.updateStatus(id, FeedbackStatus.valueOf(status)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        feedbackService.deleteFeedback(id);
        return ResponseEntity.noContent().build();
    }
}
