package com.projects.ecommerce.service.impl;


import com.projects.ecommerce.dto.request.FeedbackRequest;
import com.projects.ecommerce.dto.response.FeedbackResponse;
import com.projects.ecommerce.entity.Feedback;
import com.projects.ecommerce.entity.User;
import com.projects.ecommerce.enums.FeedbackStatus;
import com.projects.ecommerce.repository.FeedbackRepository;
import com.projects.ecommerce.service.FeedbackService;
import com.projects.ecommerce.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class FeedbackServiceImpl implements FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final UserService userService;

    private final Function<Feedback, FeedbackResponse> toDto = f -> FeedbackResponse.builder()
            .id(f.getId())
            .userId(f.getUserId())
            .phone(f.getPhone())
            .title(f.getTitle())
            .message(f.getMessage())
            .rating(f.getRating())
            .status(f.getStatus().name())
            .createdAt(f.getCreatedAt())
            .updatedAt(f.getUpdatedAt())
            .build();

    @Override
    @Transactional
    public FeedbackResponse createFeedback(FeedbackRequest req) {
        User user = userService.getCurrentUser();

        Feedback f = Feedback.builder()
                .userId(user.getId())
                .phone(user.getMobile())
                .title(req.getTitle())
                .message(req.getMessage())
                .rating(req.getRating())
                .status(FeedbackStatus.NEW)
                .build();
        Feedback saved = feedbackRepository.save(f);
        return toDto.apply(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FeedbackResponse> listFeedbacks(Integer rating, Long productId, Pageable pageable) {
        Page<Feedback> page;
        if (productId != null && rating != null) {
            page = feedbackRepository.findByProductIdAndRating(productId, rating, pageable);
        } else if (productId != null) {
            page = feedbackRepository.findByProductId(productId, pageable);
        } else if (rating != null) {
            page = feedbackRepository.findByRating(rating, pageable);
        } else {
            page = feedbackRepository.findAll(pageable);
        }
        return page.map(toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public FeedbackResponse getById(Long id) {
        Feedback f = feedbackRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Feedback not found: " + id));
        return toDto.apply(f);
    }

    @Override
    @Transactional
    public FeedbackResponse updateFeedback(Long id, FeedbackRequest req) {
        Feedback f = feedbackRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Feedback not found: " + id));
        // Update allowed fields (title, message, rating, author info). Status change via status endpoint.
        f.setTitle(req.getTitle());
        f.setMessage(req.getMessage());
        f.setRating(req.getRating());
        return toDto.apply(feedbackRepository.save(f));
    }

    @Override
    @Transactional
    public FeedbackResponse updateStatus(Long id, FeedbackStatus status) {
        Feedback f = feedbackRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Feedback not found: " + id));
        f.setStatus(status);
        return toDto.apply(feedbackRepository.save(f));
    }

    @Override
    @Transactional
    public void deleteFeedback(Long id) {
        if (!feedbackRepository.existsById(id)) throw new ResourceNotFoundException("Feedback not found: " + id);
        feedbackRepository.deleteById(id);
    }

    // Custom exception inside service package:
    public static class ResourceNotFoundException extends RuntimeException {
        public ResourceNotFoundException(String msg) {
            super(msg);
        }
    }
}
