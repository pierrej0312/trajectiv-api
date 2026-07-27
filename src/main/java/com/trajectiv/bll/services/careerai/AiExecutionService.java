package com.trajectiv.bll.services.careerai;

import com.trajectiv.bll.dto.careerai.AiExecutionCommandBllDto;
import com.trajectiv.bll.dto.careerai.AiExecutionResultBllDto;

public interface AiExecutionService {

    AiExecutionResultBllDto execute(AiExecutionCommandBllDto command);
}