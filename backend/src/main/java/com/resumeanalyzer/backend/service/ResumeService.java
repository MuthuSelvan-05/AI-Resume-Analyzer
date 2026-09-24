package com.resumeanalyzer.backend.service;

import com.resumeanalyzer.backend.dto.ResumeRequest;
import com.resumeanalyzer.backend.dto.ResumeResponse;
import com.resumeanalyzer.backend.dto.ResumeVersionResponse;
import com.resumeanalyzer.backend.entity.Resume;
import com.resumeanalyzer.backend.entity.ResumeVersion;
import com.resumeanalyzer.backend.entity.User;
import com.resumeanalyzer.backend.exception.ResourceNotFoundException;
import com.resumeanalyzer.backend.repository.ResumeRepository;
import com.resumeanalyzer.backend.repository.ResumeVersionRepository;
import com.resumeanalyzer.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ResumeService {

    private final ResumeRepository resumeRepository;
    private final ResumeVersionRepository resumeVersionRepository;
    private final UserRepository userRepository;

    public ResumeResponse createResume(
            String email,
            ResumeRequest request) {

        User user = getUserByEmail(email);

        Resume resume = new Resume();
        resume.setUser(user);
        resume.setTitle(request.getTitle());

        Resume savedResume = resumeRepository.save(resume);

        return toResponse(savedResume);
    }

    @Transactional(readOnly = true)
    public List<ResumeResponse> getMyResumes(String email) {

        User user = getUserByEmail(email);

        return resumeRepository
                .findByUserIdOrderByUpdatedAtDesc(user.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public Resume getResumeEntity(
            String email,
            Long resumeId) {

        User user = getUserByEmail(email);

        return resumeRepository.findById(resumeId)
                .filter(resume ->
                        resume.getUser().getId().equals(user.getId()))
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Resume not found"
                        ));
    }

    @Transactional(readOnly = true)
    public ResumeResponse getResume(
            String email,
            Long resumeId) {

        return toResponse(
                getResumeEntity(email, resumeId)
        );
    }

    public ResumeResponse updateResume(
            String email,
            Long resumeId,
            ResumeRequest request) {

        Resume resume = getResumeEntity(email, resumeId);

        resume.setTitle(request.getTitle());

        Resume updatedResume = resumeRepository.save(resume);

        return toResponse(updatedResume);
    }

    public void deleteResume(
            String email,
            Long resumeId) {

        Resume resume = getResumeEntity(email, resumeId);

        resumeRepository.delete(resume);
    }

    private User getUserByEmail(String email) {

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found"
                        ));
    }

    private ResumeResponse toResponse(Resume resume) {

        ResumeVersionResponse latestVersion = null;

        ResumeVersion version =
                resumeVersionRepository
                        .findTopByResumeIdOrderByVersionNumberDesc(
                                resume.getId()
                        )
                        .orElse(null);

        if (version != null) {
            latestVersion = new ResumeVersionResponse(
                    version.getId(),
                    version.getVersionNumber(),
                    version.getFileName(),
                    version.getFileType(),
                    version.getUploadedAt()
            );
        }

        return new ResumeResponse(
                resume.getId(),
                resume.getTitle(),
                resume.getCreatedAt(),
                resume.getUpdatedAt(),
                latestVersion
        );
    }
}