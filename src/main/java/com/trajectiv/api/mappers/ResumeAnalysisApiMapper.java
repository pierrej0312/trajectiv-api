package com.trajectiv.api.mappers;

import com.trajectiv.api.dto.me.resume.analysis.ResumeAnalysisResponseApiDto;
import com.trajectiv.bll.dto.resume.analysis.AnalyzeResumeBllCommand;
import com.trajectiv.bll.dto.resume.analysis.ResumeAnalysisBllDto;
import com.trajectiv.bll.exceptions.resume.ResumeExtractionException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Component
public class ResumeAnalysisApiMapper {

    public AnalyzeResumeBllCommand toBllCommand(
            MultipartFile file
    ) {
        if (file == null || file.isEmpty()) {
            throw ResumeExtractionException.emptyFile();
        }

        String originalFilename = file.getOriginalFilename();
        String fileName = originalFilename == null
                || originalFilename.isBlank()
                ? "resume.pdf"
                : originalFilename.trim();

        try {
            return new AnalyzeResumeBllCommand(
                    fileName,
                    file.getContentType(),
                    file.getBytes()
            );
        } catch (IOException exception) {
            throw ResumeExtractionException.failed(exception);
        }
    }

    public ResumeAnalysisResponseApiDto toApiDto(
            ResumeAnalysisBllDto source
    ) {
        return new ResumeAnalysisResponseApiDto(
                source.analysisId(),
                source.schemaVersion(),
                source.promptVersion(),
                source.provider(),
                source.model(),
                source.durationMillis(),
                source.artifact()
        );
    }
}