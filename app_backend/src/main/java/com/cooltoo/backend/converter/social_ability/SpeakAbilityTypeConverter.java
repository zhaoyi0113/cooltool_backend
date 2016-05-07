package com.cooltoo.backend.converter.social_ability;

import com.cooltoo.backend.entities.SpeakTypeEntity;
import com.cooltoo.backend.repository.SpeakTypeRepository;
import com.cooltoo.beans.SpecificSocialAbility;
import com.cooltoo.constants.SocialAbilityType;
import com.cooltoo.constants.SpeakType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhaolisong on 16/5/6.
 */
@Component
public class SpeakAbilityTypeConverter implements SocialAbilityTypeConverter {

    private static final Logger logger = LoggerFactory.getLogger(SpeakAbilityTypeConverter.class.getName());

    @Autowired
    private SpeakTypeRepository speakTypeRepository;

    public List<SpecificSocialAbility> getItems() {
        List<SpecificSocialAbility> items = new ArrayList<>();
        SpecificSocialAbility ability;
        List<SpeakTypeEntity> speakTypes = speakTypeRepository.findAll(sorter);
        for (SpeakTypeEntity speakType : speakTypes) {
            ability = new SpecificSocialAbility();
            ability.setAbilityId(speakType.getId());
            ability.setAbilityName(speakType.getName());
            ability.setAbilityType(SocialAbilityType.COMMUNITY);
            items.add(ability);
        }
        return items;
    }

    public long itemSize() {
        return speakTypeRepository.count();
    }

    public boolean existItem(int itemId) {
        return speakTypeRepository.exists(itemId);
    }

}
