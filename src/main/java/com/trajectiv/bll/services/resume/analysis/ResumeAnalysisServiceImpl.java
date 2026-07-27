package com.trajectiv.bll.services.resume.analysis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.trajectiv.bll.dto.careerai.AiExecutionCommandBllDto;
import com.trajectiv.bll.dto.careerai.AiExecutionResultBllDto;
import com.trajectiv.bll.dto.resume.analysis.AnalyzeResumeBllCommand;
import com.trajectiv.bll.dto.resume.analysis.ResumeAnalysisBllDto;
import com.trajectiv.bll.dto.storage.ExtractedDocumentPageBllDto;
import com.trajectiv.bll.dto.storage.ExtractedDocumentTextBllDto;
import com.trajectiv.bll.exceptions.careerai.AiPromptRenderingException;
import com.trajectiv.bll.exceptions.resume.ResumeExtractionException;
import com.trajectiv.bll.models.careerai.AiUseCase;
import com.trajectiv.bll.services.careerai.AiExecutionService;
import com.trajectiv.bll.services.storage.pdf.PdfTextExtractionService;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
public class ResumeAnalysisServiceImpl
        implements ResumeAnalysisService {

    private static final int MAX_RESUME_BYTES =
            10 * 1024 * 1024;

    private static final byte[] PDF_SIGNATURE =
            "%PDF-".getBytes(StandardCharsets.US_ASCII);

    private final PdfTextExtractionService pdfTextExtractionService;
    private final AiExecutionService aiExecutionService;
    private final ObjectMapper objectMapper;

    public ResumeAnalysisServiceImpl(
            PdfTextExtractionService pdfTextExtractionService,
            AiExecutionService aiExecutionService,
            ObjectMapper objectMapper
    ) {
        this.pdfTextExtractionService = pdfTextExtractionService;
        this.aiExecutionService = aiExecutionService;
        this.objectMapper = objectMapper;
    }

    @Override
    public ResumeAnalysisBllDto analyze(
            AnalyzeResumeBllCommand command
    ) {
        validatePdf(command);

        ExtractedDocumentTextBllDto extraction =
                pdfTextExtractionService.extract(command.content());

        if (!extraction.textExtractable()) {
            throw ResumeExtractionException.textNotExtractable();
        }

        AiExecutionResultBllDto execution =
                aiExecutionService.execute(
                        new AiExecutionCommandBllDto(
                                AiUseCase.RESUME_INTELLIGENCE,
                                Map.of(
                                        "RESUME_TEXT_BY_PAGE",
                                        toPagesJson(extraction),
                                        "ATS_DETERMINISTIC_DIAGNOSTICS_JSON",
                                        toDiagnosticsJson(
                                                command,
                                                extraction
                                        )
                                )
                        )
                );

        return new ResumeAnalysisBllDto(
                execution.executionId(),
                execution.schemaVersion(),
                execution.promptVersion(),
                execution.provider(),
                execution.model(),
                execution.durationMillis(),
                execution.artifact()
        );
    }

    private void validatePdf(
            AnalyzeResumeBllCommand command
    ) {
        byte[] content = command.content();

        if (content.length == 0) {
            throw ResumeExtractionException.emptyFile();
        }

        if (content.length > MAX_RESUME_BYTES) {
            throw ResumeExtractionException.fileTooLarge(
                    MAX_RESUME_BYTES
            );
        }

        if (!hasPdfSignature(content)) {
            throw ResumeExtractionException.unsupportedFileType();
        }
    }

    private boolean hasPdfSignature(byte[] content) {
        if (content.length < PDF_SIGNATURE.length) {
            return false;
        }

        for (int index = 0; index < PDF_SIGNATURE.length; index++) {
            if (content[index] != PDF_SIGNATURE[index]) {
                return false;
            }
        }
        return true;
    }

    private String toPagesJson(
            ExtractedDocumentTextBllDto extraction
    ) {
        ArrayNode pages = objectMapper.createArrayNode();
        for (ExtractedDocumentPageBllDto page : extraction.pages()) {
            ObjectNode value = pages.addObject();
            value.put("page", page.pageNumber());
            value.put("text", page.text());
        }
        return writeJson(pages);
    }

    private String toDiagnosticsJson(
            AnalyzeResumeBllCommand command,
            ExtractedDocumentTextBllDto extraction
    ) {
        ObjectNode diagnostics = objectMapper.createObjectNode();
        diagnostics.put("fileName", command.fileName());
        diagnostics.put("contentType", command.contentType());
        diagnostics.put("pageCount", extraction.pageCount());
        diagnostics.put(
                "textExtractable",
                extraction.textExtractable()
        );
        diagnostics.set(
                "warnings",
                objectMapper.valueToTree(extraction.warnings())
        );
        return writeJson(diagnostics);
    }

    private String writeJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException exception) {
            throw new AiPromptRenderingException(
                    "Unable to serialize extracted resume data for the AI prompt.",
                    exception
            );
        }
    }
}