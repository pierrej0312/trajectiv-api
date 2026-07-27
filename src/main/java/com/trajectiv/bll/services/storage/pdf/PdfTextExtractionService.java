package com.trajectiv.bll.services.storage.pdf;

import com.trajectiv.bll.dto.storage.ExtractedDocumentTextBllDto;

public interface PdfTextExtractionService {

    ExtractedDocumentTextBllDto extract(byte[] content);
}