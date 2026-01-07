package com.projects.ecommerce.service;

import com.projects.ecommerce.dto.response.CloudinaryUploadResult;
import org.springframework.web.multipart.MultipartFile;

public interface CloudinaryService {

    CloudinaryUploadResult upload(MultipartFile file);

    void deleteFile(String publicId);
}
