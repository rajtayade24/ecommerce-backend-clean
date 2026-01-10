package com.projects.ecommerce.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {

    @PostConstruct
    public void init() throws Exception {

        FirebaseOptions options;

        // Check if environment variable exists (production)
        String credentialsPath = System.getenv("GOOGLE_APPLICATION_CREDENTIALS");

        if (credentialsPath != null && !credentialsPath.isEmpty()) {
            // Use the JSON file from environment variable
            try (FileInputStream serviceAccount = new FileInputStream(credentialsPath)) {
                options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .build();
            }
        } else {
            // Fallback: use classpath (local dev)
            InputStream serviceAccount = getClass().getClassLoader()
                    .getResourceAsStream("firebase/serviceAccount.json");

            if (serviceAccount == null) {
                throw new IllegalStateException(
                        "Firebase service account file not found. " +
                                "Set GOOGLE_APPLICATION_CREDENTIALS in production.");
            }

            options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();
        }

        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseApp.initializeApp(options);
        }
    }
}
