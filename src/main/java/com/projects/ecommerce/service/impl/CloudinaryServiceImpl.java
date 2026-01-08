//package com.projects.ecommerce.service.impl;
//
//import com.cloudinary.Cloudinary;
//import com.cloudinary.utils.ObjectUtils;
//import com.projects.ecommerce.dto.response.CloudinaryUploadResult;
//import com.projects.ecommerce.service.CloudService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.IOException;
//import java.util.Map;
//
//@Service
//@RequiredArgsConstructor
//public class CloudinaryServiceImpl implements CloudService {
//
//    private final Cloudinary cloudinary;
//
//    @Override
//    public CloudinaryUploadResult upload(MultipartFile file) {
//        try {
//            Map uploadResult = cloudinary.uploader().upload(
//                    file.getBytes(),
//                    ObjectUtils.asMap(
//                            "folder", "products",
//                            "resource_type", "image"
//                    )
//            );
//
//            String url = uploadResult.get("secure_url").toString();
//            String publicId = uploadResult.get("public_id").toString();
//
//            return new CloudinaryUploadResult(url, publicId);
//
//        } catch (IOException e) {
//            throw new RuntimeException("Image upload failed", e);
//        }
//    }
//
//    @Override
//    public void deleteFile(String publicId) {
//        try {
//            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
//        } catch (IOException e) {
//            throw new RuntimeException("Failed to delete image from Cloudinary", e);
//        }
//    }
//}
