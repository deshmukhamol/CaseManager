package com.example.casemanager.service;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageService {

    private final Path storageLocation;

    public FileStorageService(@Value("${case-manager.storage.upload-dir:uploads}") String uploadDir) {
        this.storageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
    }

    @PostConstruct
    public void init() throws IOException {
        Files.createDirectories(storageLocation);
    }

    public String store(Long caseId, MultipartFile file) throws IOException {
        String originalFileName = StringUtils.hasText(file.getOriginalFilename())
            ? file.getOriginalFilename()
            : "document";
        String cleanedFileName = originalFileName.replaceAll("[^a-zA-Z0-9\\.\\-_]", "_");
        Path caseDirectory = storageLocation.resolve("case-" + caseId);
        Files.createDirectories(caseDirectory);
        Path targetFile = caseDirectory.resolve(cleanedFileName);
        Files.copy(file.getInputStream(), targetFile, StandardCopyOption.REPLACE_EXISTING);
        return targetFile.toString();
    }
}
