package com.resumeanalyzer.backend.service;

import com.resumeanalyzer.backend.dto.SuggestionRequest;
import com.resumeanalyzer.backend.dto.SuggestionResponse;
import com.resumeanalyzer.backend.entity.Analysis;
import com.resumeanalyzer.backend.entity.Suggestion;
import com.resumeanalyzer.backend.exception.ResourceNotFoundException;
import com.resumeanalyzer.backend.repository.SuggestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SuggestionService {

    private final SuggestionRepository suggestionRepository;
    private final AnalysisService analysisService;

    public SuggestionResponse createSuggestion(
            String email,
            Long analysisId,
            SuggestionRequest request) {

        Analysis analysis =
                analysisService.getAnalysisEntity(
                        email,
                        analysisId
                );

        Suggestion suggestion = new Suggestion();

        suggestion.setAnalysis(analysis);
        suggestion.setCategory(request.getCategory());
        suggestion.setTitle(request.getTitle());
        suggestion.setDescription(request.getDescription());
        suggestion.setPriority(request.getPriority());

        Suggestion savedSuggestion =
                suggestionRepository.save(suggestion);

        return toResponse(savedSuggestion);
    }

    @Transactional(readOnly = true)
    public List<SuggestionResponse> getSuggestions(
            String email,
            Long analysisId) {

        analysisService.getAnalysisEntity(
                email,
                analysisId
        );

        return suggestionRepository
                .findByAnalysisIdOrderByPriorityAsc(analysisId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public SuggestionResponse getSuggestion(
            String email,
            Long analysisId,
            Long suggestionId) {

        analysisService.getAnalysisEntity(
                email,
                analysisId
        );

        Suggestion suggestion =
                suggestionRepository.findById(suggestionId)
                        .filter(item ->
                                item.getAnalysis()
                                        .getId()
                                        .equals(analysisId))
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Suggestion not found"
                                ));

        return toResponse(suggestion);
    }

    public SuggestionResponse updateSuggestion(
            String email,
            Long analysisId,
            Long suggestionId,
            SuggestionRequest request) {

        analysisService.getAnalysisEntity(
                email,
                analysisId
        );

        Suggestion suggestion =
                suggestionRepository.findById(suggestionId)
                        .filter(item ->
                                item.getAnalysis()
                                        .getId()
                                        .equals(analysisId))
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Suggestion not found"
                                ));

        suggestion.setCategory(request.getCategory());
        suggestion.setTitle(request.getTitle());
        suggestion.setDescription(request.getDescription());
        suggestion.setPriority(request.getPriority());

        return toResponse(
                suggestionRepository.save(suggestion)
        );
    }

    public void deleteSuggestion(
            String email,
            Long analysisId,
            Long suggestionId) {

        analysisService.getAnalysisEntity(
                email,
                analysisId
        );

        Suggestion suggestion =
                suggestionRepository.findById(suggestionId)
                        .filter(item ->
                                item.getAnalysis()
                                        .getId()
                                        .equals(analysisId))
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Suggestion not found"
                                ));

        suggestionRepository.delete(suggestion);
    }

    private SuggestionResponse toResponse(
            Suggestion suggestion) {

        return new SuggestionResponse(
                suggestion.getId(),
                suggestion.getCategory(),
                suggestion.getTitle(),
                suggestion.getDescription(),
                suggestion.getPriority()
        );
    }
}