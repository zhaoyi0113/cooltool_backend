package com.cooltoo.backend.converter.social_ability;

import com.cooltoo.entities.HospitalDepartmentEntity;
import com.cooltoo.repository.HospitalDepartmentRepository;
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
public class OccupationAbilityTypeConverter implements SocialAbilityTypeConverter {

    @Autowired
    private HospitalDepartmentRepository departmentRepository;

    public List<SpecificSocialAbility> getItems() {
        List<SpecificSocialAbility> items = new ArrayList<>();
        SpecificSocialAbility ability;

        List<HospitalDepartmentEntity> departments = departmentRepository.findAll(sorter);
        for (HospitalDepartmentEntity department : departments) {
            ability = new SpecificSocialAbility();
            ability.setAbilityId(department.getId());
            ability.setAbilityName(department.getName());
            ability.setFactor(1);
            ability.setAbilityType(SocialAbilityType.OCCUPATION);
            items.add(ability);
        }

        return items;
    }

    public long itemSize() {
        return departmentRepository.count();
    }

    public boolean existItem(int itemId) {
        return departmentRepository.exists(itemId);
    }

    public SpecificSocialAbility getItem(int itemId) {
        HospitalDepartmentEntity entity = departmentRepository.findOne(itemId);
        SpecificSocialAbility ability = null;
        if (null!=entity) {
            ability = new SpecificSocialAbility();
            ability.setAbilityId(entity.getId());
            ability.setAbilityName(entity.getName());
            ability.setFactor(1);
            ability.setAbilityType(SocialAbilityType.OCCUPATION);
        }
        return ability;
    }
}
