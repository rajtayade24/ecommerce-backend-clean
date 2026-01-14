package com.projects.ecommerce.service;

import com.projects.ecommerce.dto.request.FeedbackRequest;
import com.projects.ecommerce.dto.response.FeedbackResponse;
import com.projects.ecommerce.enums.FeedbackStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FeedbackService {

    FeedbackResponse createFeedback(FeedbackRequest req);

    Page<FeedbackResponse> listFeedbacks(Integer rating, Long productId, Pageable pageable);

    FeedbackResponse getById(Long id);

    FeedbackResponse updateFeedback(Long id, FeedbackRequest req);

    FeedbackResponse updateStatus(Long id, FeedbackStatus status);

    void deleteFeedback(Long id);

}
