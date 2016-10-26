package com.cooltoo.go2nurse.service;

import com.cooltoo.beans.SuggestionBean;
import com.cooltoo.constants.UserType;
import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import com.cooltoo.go2nurse.entities.UserEntity;
import com.cooltoo.go2nurse.repository.UserRepository;
import com.cooltoo.services.SuggestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by zhaolisong on 16/4/6.
 */
@Service("UserSuggestionService")
public class UserSuggestionService {

    @Autowired private SuggestionService suggestionService;
    @Autowired private UserRepository userRepository;

    //=========================================================
    //           add suggestion
    //=========================================================
    public SuggestionBean userAddSuggestion(long userId, String strPlatformType, String version, String suggestion) {
        UserEntity user = userRepository.findOne(userId);
        if (null==user) {
            throw new BadRequestException(ErrorCode.USER_NOT_EXISTED);
        }
        return suggestionService.addSuggestion(userId, UserType.NORMAL_USER.name(), user.getName(), strPlatformType, version, suggestion);
    }
}
