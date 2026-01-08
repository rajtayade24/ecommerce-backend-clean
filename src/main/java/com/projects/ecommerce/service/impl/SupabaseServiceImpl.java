package com.projects.ecommerce.service.impl;

import com.projects.ecommerce.dto.response.CloudinaryUploadResult;
import com.projects.ecommerce.service.CloudService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import okhttp3.*;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SupabaseServiceImpl implements CloudService {

    private final OkHttpClient httpClient = new OkHttpClient();

    // e.g. https://abcd1234.supabase.co
    @Value("${supabase.url}")
    private String supabaseUrl;

    // service_role key (KEEP SECRET; server only)
    @Value("${supabase.service-role-key}")
    private String serviceRoleKey;

    // name of your bucket e.g. "products"
    @Value("${supabase.bucket:products}")
    private String bucket;

    @Override
    public CloudinaryUploadResult upload(MultipartFile file) {
        String originalFileName = file.getOriginalFilename() == null ? "file" : file.getOriginalFilename();
        String ext = "";
        int idx = originalFileName.lastIndexOf('.');
        if (idx >= 0) ext = originalFileName.substring(idx); // include dot
        String uuid = UUID.randomUUID().toString();
        // store under folder "products" root (or change as needed)
        String path = "products/" + uuid + ext; // this is the "publicId" equivalent

        String endpoint = String.format("%s/storage/v1/object/%s/%s", supabaseUrl, bucket, path);

        try {
            // create request body as binary
            MediaType mediaType = file.getContentType() == null ?
                    MediaType.parse("application/octet-stream") :
                    MediaType.parse(file.getContentType());

            RequestBody body = RequestBody.create(file.getBytes(), mediaType);

            Request request = new Request.Builder()
                    .url(endpoint)
                    .post(body)
                    .addHeader("Authorization", "Bearer " + serviceRoleKey)
                    .addHeader("apikey", serviceRoleKey) // some examples include apikey header. harmless with service role
                    .addHeader("Content-Type", mediaType.toString())
                    .build();

            try (Response response = httpClient.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    String respBody = response.body() != null ? response.body().string() : "empty";
                    throw new RuntimeException("Supabase upload failed: " + response.code() + " - " + respBody);
                }

                // build public URL (works for public buckets)
                String publicUrl = String.format("%s/storage/v1/object/public/%s/%s", supabaseUrl, bucket, path);

                // return path as publicId so you can delete later using the same path
                return new CloudinaryUploadResult(publicUrl, path);
            }
        } catch (IOException e) {
            throw new RuntimeException("Image upload failed", e);
        }
    }

    @Override
    public void deleteFile(String publicId) {
        // publicId is the path returned earlier: e.g. "products/<uuid>.png"
        String endpoint = String.format("%s/storage/v1/object/%s/%s", supabaseUrl, bucket, publicId);

        Request request = new Request.Builder()
                .url(endpoint)
                .delete()
                .addHeader("Authorization", "Bearer " + serviceRoleKey)
                .addHeader("apikey", serviceRoleKey)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String respBody = response.body() != null ? response.body().string() : "empty";
                throw new RuntimeException("Failed to delete image from Supabase: " + response.code() + " - " + respBody);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete image from Supabase", e);
        }
    }
}
