package com.resumeanalyzer.backend.service;

import com.resumeanalyzer.backend.dto.ResumeVersionResponse;
import com.resumeanalyzer.backend.entity.Resume;
import com.resumeanalyzer.backend.entity.ResumeVersion;
import com.resumeanalyzer.backend.exception.BadRequestException;
import com.resumeanalyzer.backend.exception.ResourceNotFoundException;
import com.resumeanalyzer.backend.repository.ResumeVersionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ResumeVersionService {

    private final ResumeVersionRepository resumeVersionRepository;
    private final ResumeService resumeService;

    private static final String UPLOAD_DIRECTORY = "uploads/resumes";

    public ResumeVersionResponse uploadResumeVersion(
            String email,
            Long resumeId,
            MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Resume file is required");
        }

        String originalFileName = file.getOriginalFilename();

        if (originalFileName == null || originalFileName.isBlank()) {
            throw new BadRequestException("Invalid file name");
        }

        String fileType = getFileType(originalFileName);

        if (!fileType.equals("PDF") && !fileType.equals("DOCX")) {
            throw new BadRequestException(
                    "Only PDF and DOCX files are supported"
            );
        }

        if (file.getSize() > 10 * 1024 * 1024) {
            throw new BadRequestException(
                    "File size must not exceed 10MB"
            );
        }

        Resume resume =
                resumeService.getResumeEntity(email, resumeId);

        int nextVersionNumber =
                resumeVersionRepository
                        .findTopByResumeIdOrderByVersionNumberDesc(resumeId)
                        .map(version -> version.getVersionNumber() + 1)
                        .orElse(1);

        Path uploadPath =
                Paths.get(UPLOAD_DIRECTORY)
                        .toAbsolutePath()
                        .normalize();

        try {
            Files.createDirectories(uploadPath);

            String storedFileName =
                    UUID.randomUUID()
                            + "_"
                            + sanitizeFileName(originalFileName);

            Path targetPath =
                    uploadPath.resolve(storedFileName).normalize();

            if (!targetPath.startsWith(uploadPath)) {
                throw new BadRequestException("Invalid file path");
            }

            Files.copy(
                    file.getInputStream(),
                    targetPath,
                    StandardCopyOption.REPLACE_EXISTING
            );

            ResumeVersion version = new ResumeVersion();

            version.setResume(resume);
            version.setVersionNumber(nextVersionNumber);
            version.setFileName(originalFileName);
            version.setFileType(fileType);
            version.setFilePath(targetPath.toString());

            ResumeVersion savedVersion =
                    resumeVersionRepository.save(version);

            return toResponse(savedVersion);

        } catch (IOException exception) {
            throw new BadRequestException(
                    "Failed to store resume file"
            );
        }
    }

    @Transactional(readOnly = true)
    public List<ResumeVersionResponse> getResumeVersions(
            String email,
            Long resumeId) {

        resumeService.getResumeEntity(email, resumeId);

        return resumeVersionRepository
                .findByResumeIdOrderByVersionNumberDesc(resumeId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ResumeVersion getResumeVersionEntity(
            String email,
            Long resumeId,
            Integer versionNumber) {

        resumeService.getResumeEntity(email, resumeId);

        return resumeVersionRepository
                .findByResumeIdAndVersionNumber(
                        resumeId,
                        versionNumber
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Resume version not found"
                        ));
    }

    @Transactional(readOnly = true)
    public Resource getResumeFile(
            String email,
            Long resumeId,
            Integer versionNumber) {

        ResumeVersion version =
                getResumeVersionEntity(
                        email,
                        resumeId,
                        versionNumber
                );

        try {
            Path filePath =
                    Paths.get(version.getFilePath())
                            .toAbsolutePath()
                            .normalize();

            if (!Files.exists(filePath)
                    || !Files.isRegularFile(filePath)) {

                throw new ResourceNotFoundException(
                        "Resume file not found"
                );
            }

            Resource resource =
                    new UrlResource(filePath.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                throw new ResourceNotFoundException(
                        "Resume file cannot be read"
                );
            }

            return resource;

        } catch (IOException exception) {

            throw new ResourceNotFoundException(
                    "Unable to read resume file"
            );
        }
    }

    private String getFileType(String fileName) {

        String lowerCaseName =
                fileName.toLowerCase();

        if (lowerCaseName.endsWith(".pdf")) {
            return "PDF";
        }

        if (lowerCaseName.endsWith(".docx")) {
            return "DOCX";
        }

        return "";
    }

    private String sanitizeFileName(String fileName) {

        return fileName
                .replaceAll("[^a-zA-Z0-9._-]", "_");
    }

    private ResumeVersionResponse toResponse(
            ResumeVersion version) {

        return new ResumeVersionResponse(
                version.getId(),
                version.getVersionNumber(),
                version.getFileName(),
                version.getFileType(),
                version.getUploadedAt()
        );
    }
}