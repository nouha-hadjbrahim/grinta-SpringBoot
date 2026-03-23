package com.esprit.backendsport.Services.File;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileUploadService {

    private static final String UPLOAD_DIR = "uploads";
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5 MB
    private static final String[] ALLOWED_EXTENSIONS = {"jpg", "jpeg", "png", "gif"};

    /**
     * Save uploaded file and return the file name
     * Generates unique filename with timestamp
     * 
     * @param file MultipartFile to upload
     * @return File name saved in uploads folder
     * @throws IOException if file upload fails
     * @throws IllegalArgumentException if file is invalid
     */
    public String saveFile(MultipartFile file) throws IOException {
        // Validate file
        validateFile(file);

        // Create uploads directory if it doesn't exist
        Path uploadPath = Paths.get(UPLOAD_DIR);
        Files.createDirectories(uploadPath);

        // Generate unique filename
        String originalName = file.getOriginalFilename();
        String fileExtension = getFileExtension(originalName);
        String uniqueFileName = UUID.randomUUID() + "_" + System.currentTimeMillis() + "." + fileExtension;

        // Save file
        Path filePath = uploadPath.resolve(uniqueFileName);
        Files.write(filePath, file.getBytes());

        return uniqueFileName;
    }

    /**
     * Delete file from uploads folder
     * 
     * @param fileName File name to delete
     * @return true if deleted successfully, false if file doesn't exist
     */
    public boolean deleteFile(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return false;
        }

        try {
            Path filePath = Paths.get(UPLOAD_DIR, fileName);
            return Files.deleteIfExists(filePath);
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Validate uploaded file
     * Checks file size and extension
     * 
     * @param file MultipartFile to validate
     * @throws IllegalArgumentException if file is invalid
     */
    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be empty");
        }

        // Check file size
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("File size exceeds maximum allowed size of 5 MB");
        }

        // Check file extension
        String fileName = file.getOriginalFilename();
        if (fileName == null) {
            throw new IllegalArgumentException("File name cannot be null");
        }

        String extension = getFileExtension(fileName).toLowerCase();
        boolean isAllowed = false;
        for (String allowed : ALLOWED_EXTENSIONS) {
            if (allowed.equalsIgnoreCase(extension)) {
                isAllowed = true;
                break;
            }
        }

        if (!isAllowed) {
            throw new IllegalArgumentException("File type not allowed. Allowed types: jpg, jpeg, png, gif");
        }
    }

    /**
     * Extract file extension from file name
     * 
     * @param fileName File name
     * @return File extension without dot
     */
    private String getFileExtension(String fileName) {
        int lastDot = fileName.lastIndexOf('.');
        if (lastDot > 0) {
            return fileName.substring(lastDot + 1);
        }
        return "";
    }
}
