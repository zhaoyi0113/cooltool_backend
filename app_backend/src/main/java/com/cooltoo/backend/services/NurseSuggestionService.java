package com.cooltoo.backend.services;

import com.cooltoo.entities.NurseEntity;
import com.cooltoo.repository.NurseRepository;
import com.cooltoo.beans.SuggestionBean;
import com.cooltoo.constants.UserType;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.services.SuggestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by zhaolisong on 16/4/6.
 */
@Service("NurseSuggestionService")
public class NurseSuggestionService {

    @Autowired private SuggestionService suggestionService;
    @Autowired private NurseRepository nurseRepository;

    //=========================================================
    //           add suggestion
    //=========================================================
    public SuggestionBean nurseAddSuggestion(long nurseId, String strPlatformType, String version, String suggestion) {
        NurseEntity nurse = nurseRepository.findOne(nurseId);
        if (null==nurse) {
            throw new BadRequestException(ErrorCode.USER_NOT_EXISTED);
        }
        return suggestionService.addSuggestion(nurseId, UserType.NURSE.name(), nurse.getName(), strPlatformType, version, suggestion);
    }
}
