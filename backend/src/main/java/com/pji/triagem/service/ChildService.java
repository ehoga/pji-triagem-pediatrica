package com.pji.triagem.service;

import com.pji.triagem.base.service.BaseService;
import com.pji.triagem.dto.request.CreateChildRequest;
import com.pji.triagem.dto.response.ChildHomeResponse;
import com.pji.triagem.dto.response.ChildResponse;
import com.pji.triagem.model.Child;

import java.util.List;

public interface ChildService extends BaseService<Child> {

    List<ChildHomeResponse> findChildrenForHomeByUserId(Long userId);

    ChildResponse createChild(Long userId, CreateChildRequest request);

    ChildResponse findByIdAndUserId(Long childId, Long userId);

    Child findAccessibleEntity(Long id);
}
