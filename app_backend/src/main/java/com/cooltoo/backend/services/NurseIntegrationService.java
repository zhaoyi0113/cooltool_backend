package com.cooltoo.backend.services;

import com.cooltoo.backend.beans.NurseBean;
import com.cooltoo.backend.beans.NurseSpeakBean;
import com.cooltoo.backend.beans.SpeakTypeBean;
import com.cooltoo.backend.entities.NurseIntegrationEntity;
import com.cooltoo.backend.repository.NurseIntegrationRepository;
import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.SocialAbilityType;
import com.cooltoo.constants.SpeakType;
import com.cooltoo.constants.UserType;
import com.cooltoo.exception.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * Created by yzzhao on 5/21/16.
 */
@Service
public class NurseIntegrationService {

    private static final Logger logger = LoggerFactory.getLogger(NurseIntegrationService.class);

    @Autowired
    private NurseService nurseService;

    private Map<SpeakType, SpeakTypeBean> speakTypePoints = new Hashtable<>();

    @Autowired
    private SpeakTypeService speakTypeService;

    @Autowired
    private NurseIntegrationRepository repository;

    public void nurseSpeakIntegration(long userId, SpeakType speakType, NurseSpeakBean nurseSpeakBean) {
        try {
            NurseBean nurse = nurseService.getNurse(userId);
            if (nurse != null) {
                NurseIntegrationEntity entity = new NurseIntegrationEntity();
                entity.setStatus(CommonStatus.ENABLED);
                entity.setUserId(userId);
                entity.setUserType(UserType.NURSE);
                entity.setAbilityType(SocialAbilityType.COMMUNITY);
                entity.setTimeCreated(Calendar.getInstance().getTime());
                entity.setReasonId(nurseSpeakBean.getId());
                SpeakTypeBean speak = getSpeakTypePoint(speakType);
                if (speak != null) {
                    entity.setPoint(speak.getFactor());
                    entity.setAbilityId(speak.getId());
                    repository.save(entity);
                }
            } else {
                logger.error("can't find nurse " + userId);
            }
        } catch (BadRequestException e) {
            logger.error(e.getMessage());
        }
    }

    public int getNurseCommunityIntegration(long userId, SpeakType speakType) {
        SpeakTypeBean speak = speakTypeService.getSpeakTypeByType(speakType);
        if (speak == null) {
            return 0;
        }
        List<NurseIntegrationEntity> entities = repository.findByUserIdAndAbilityTypeAndAbilityIdAndStatus(userId, SocialAbilityType.COMMUNITY, speak.getId(), CommonStatus.ENABLED);
        int integration = 0;
        if (entities != null) {
            for (NurseIntegrationEntity entity : entities) {
                integration += entity.getPoint();
                break;
            }
        }
        return integration;
    }

    public SpeakTypeBean getSpeakTypePoint(SpeakType type) {
        if (!speakTypePoints.containsKey(type)) {
            SpeakTypeBean speakType = speakTypeService.getSpeakTypeByType(type);
            if (speakType != null) {
                speakTypePoints.put(type, speakType);
            }
        }
        return speakTypePoints.get(type);
    }

    public void deleteNurseSpeak(long speakId) {
        List<NurseIntegrationEntity> entities = repository.findByAbilityTypeAndReasonIdAndStatus(SocialAbilityType.COMMUNITY, speakId, CommonStatus.ENABLED);
        if (entities != null && entities.size() > 0) {
            entities.get(0).setStatus(CommonStatus.DISABLED);
            repository.save(entities.get(0));
        }

    }

}
