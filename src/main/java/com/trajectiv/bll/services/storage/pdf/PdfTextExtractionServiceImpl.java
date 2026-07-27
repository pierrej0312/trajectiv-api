package com.trajectiv.bll.services.storage.pdf;

import com.trajectiv.bll.dto.storage.ExtractedDocumentPageBllDto;
import com.trajectiv.bll.dto.storage.ExtractedDocumentTextBllDto;
import com.trajectiv.bll.exceptions.resume.ResumeExtractionException;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class PdfTextExtractionServiceImpl
        implements PdfTextExtractionService {

    private static final int MIN_EXTRACTABLE_CHARACTERS = 40;

    @Override
    public ExtractedDocumentTextBllDto extract(byte[] content) {
        if (content == null || content.length == 0) {
            throw ResumeExtractionException.emptyFile();
        }

        try (PDDocument document = Loader.loadPDF(content)) {
            if (!document
                    .getCurrentAccessPermission()
                    .canExtractContent()) {
                throw ResumeExtractionException
                        .extractionNotAllowed();
            }

            int pageCount = document.getNumberOfPages();
            List<ExtractedDocumentPageBllDto> pages =
                    new ArrayList<>(pageCount);
            List<String> warnings = new ArrayList<>();
            int extractedCharacters = 0;

            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true);

            for (int pageNumber = 1;
                 pageNumber <= pageCount;
                 pageNumber++) {
                stripper.setStartPage(pageNumber);
                stripper.setEndPage(pageNumber);

                String text = normalize(
                        stripper.getText(document)
                );
                extractedCharacters += text
                        .replaceAll("\\s+", "")
                        .length();

                pages.add(
                        new ExtractedDocumentPageBllDto(
                                pageNumber,
                                text
                        )
                );

                if (text.isBlank()) {
                    warnings.add(
                            "PAGE_%d_EMPTY_OR_IMAGE_ONLY"
                                    .formatted(pageNumber)
                    );
                }
            }

            boolean textExtractable =
                    extractedCharacters
                            >= MIN_EXTRACTABLE_CHARACTERS;

            if (!textExtractable) {
                warnings.add(
                        "OCR_REQUIRED_OR_UNSUPPORTED_TEXT_ENCODING"
                );
            }

            if (pageCount == 0) {
                warnings.add("PDF_HAS_NO_PAGE");
            }

            return new ExtractedDocumentTextBllDto(
                    pageCount,
                    textExtractable,
                    pages,
                    warnings
            );
        } catch (IOException exception) {
            throw ResumeExtractionException.failed(exception);
        }
    }

    private String normalize(String value) {
        if (value == null) {
            return "";
        }

        return value
                .replace("\u0000", "")
                .replace("\r\n", "\n")
                .replace('\r', '\n')
                .strip();
    }
}