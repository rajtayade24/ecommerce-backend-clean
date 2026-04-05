package com.projects.complaintManagementSystem.service;

import com.projects.complaintManagementSystem.enums.AccountStatusType;
import com.projects.complaintManagementSystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserCleanupService {

    private final UserRepository userRepository;

    @Scheduled(cron = "0 0 * * * *") // every hour
    @Transactional
    public void cleanupUnverifiedUsers() {

        LocalDateTime cutoff = LocalDateTime.now().minusHours(24);

        userRepository.deleteUnverifiedUsersOlderThan(
                AccountStatusType.PENDING,
                cutoff
        );

        System.out.println("Cleaned unverified users older than 24 hours");
    }
}