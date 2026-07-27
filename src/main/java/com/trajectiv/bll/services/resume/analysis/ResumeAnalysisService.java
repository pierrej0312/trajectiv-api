package com.trajectiv.bll.services.resume.analysis;

import com.trajectiv.bll.dto.resume.analysis.AnalyzeResumeBllCommand;
import com.trajectiv.bll.dto.resume.analysis.ResumeAnalysisBllDto;

public interface ResumeAnalysisService {

    ResumeAnalysisBllDto analyze(AnalyzeResumeBllCommand command);
}