package com.trajectiv.bll.exceptions.resume;

import com.trajectiv.bll.exceptions.BusinessErrorCode;
import com.trajectiv.bll.exceptions.BusinessException;

public final class ResumeExtractionException extends BusinessException {

    private ResumeExtractionException(
            BusinessErrorCode errorCode,
            String message
    ) {
        super(errorCode, message, true);
    }

    private ResumeExtractionException(
            BusinessErrorCode errorCode,
            String message,
            Throwable cause
    ) {
        super(errorCode, message, cause, true);
    }

    public static ResumeExtractionException emptyFile() {
        return new ResumeExtractionException(
                BusinessErrorCode.EMPTY_FILE,
                "A non-empty PDF file is required."
        );
    }

    public static ResumeExtractionException fileTooLarge(
            long maximumBytes
    ) {
        long maximumMegabytes = maximumBytes / 1024 / 1024;
        return new ResumeExtractionException(
                BusinessErrorCode.FILE_TOO_LARGE,
                "The PDF must not exceed %d MB."
                        .formatted(maximumMegabytes)
        );
    }

    public static ResumeExtractionException unsupportedFileType() {
        return new ResumeExtractionException(
                BusinessErrorCode.UNSUPPORTED_FILE_TYPE,
                "Only valid PDF resumes are supported for now."
        );
    }

    public static ResumeExtractionException textNotExtractable() {
        return new ResumeExtractionException(
                BusinessErrorCode.RESUME_TEXT_NOT_EXTRACTABLE,
                "The PDF does not contain enough extractable text. OCR is not enabled yet."
        );
    }

    public static ResumeExtractionException extractionNotAllowed() {
        return new ResumeExtractionException(
                BusinessErrorCode.RESUME_TEXT_NOT_EXTRACTABLE,
                "The PDF security settings do not allow text extraction."
        );
    }

    public static ResumeExtractionException failed(
            Throwable cause
    ) {
        return new ResumeExtractionException(
                BusinessErrorCode.RESUME_EXTRACTION_FAILED,
                "The PDF could not be read. Verify that it is a valid, non-encrypted PDF.",
                cause
        );
    }
}