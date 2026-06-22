package com.pji.triagem.service;

import com.pji.triagem.base.service.BaseService;
import com.pji.triagem.dto.request.CreateAssessmentRequest;
import com.pji.triagem.dto.response.AssessmentHistoryResponse;
import com.pji.triagem.dto.response.AssessmentResultResponse;
import com.pji.triagem.model.Assessment;

public interface AssessmentService extends BaseService<Assessment> {

    AssessmentResultResponse createAssessment(CreateAssessmentRequest request);

    AssessmentHistoryResponse getHistory(Long userId, Long childId);
}
