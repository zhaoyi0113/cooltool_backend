package com.cooltoo.backend.converter.social_ability;

import com.cooltoo.backend.entities.SkillEntity;
import com.cooltoo.backend.repository.SkillRepository;
import com.cooltoo.beans.SpecificSocialAbility;
import com.cooltoo.constants.SocialAbilityType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhaolisong on 16/5/6.
 */
@Component
public class SkillAbilityTypeConverter implements SocialAbilityTypeConverter {

    @Autowired
    private SkillRepository skillRepository;

    public List<SpecificSocialAbility> getItems() {
        List<SpecificSocialAbility> items = new ArrayList<>();
        SpecificSocialAbility ability;
        List<SkillEntity> skills = skillRepository.findAll(sorter);
        for (SkillEntity skill : skills) {
            ability = new SpecificSocialAbility();
            ability.setAbilityId(skill.getId());
            ability.setAbilityName(skill.getName());
            ability.setFactor(skill.getFactor());
            ability.setAbilityType(SocialAbilityType.SKILL);
            items.add(ability);
        }
        return items;
    }

    public long itemSize() {
        return skillRepository.count();
    }

    public boolean existItem(int itemId) {
        return skillRepository.exists(itemId);
    }

    public SpecificSocialAbility getItem(int itemId) {
        SkillEntity skill = skillRepository.findOne(itemId);
        SpecificSocialAbility ability = null;
        if (null!=skill) {
            ability = new SpecificSocialAbility();
            ability.setAbilityId(skill.getId());
            ability.setAbilityName(skill.getName());
            ability.setFactor(skill.getFactor());
            ability.setAbilityType(SocialAbilityType.SKILL);
        }
        return ability;
    }
}
