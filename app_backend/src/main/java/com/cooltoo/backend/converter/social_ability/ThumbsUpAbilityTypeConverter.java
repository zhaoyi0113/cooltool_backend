package com.cooltoo.backend.converter.social_ability;

import com.cooltoo.beans.SpecificSocialAbility;
import com.cooltoo.constants.SocialAbilityType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhaolisong on 16/5/6.
 */
@Component
public class ThumbsUpAbilityTypeConverter implements SocialAbilityTypeConverter {

    public static final int BEEN_THUMBS_UP = 1;
    public static final int THUMBS_UP_OTHERS = 2;

    public List<SpecificSocialAbility> getItems() {
        List<SpecificSocialAbility> items = new ArrayList<>();
        SpecificSocialAbility ability;

        ability = new SpecificSocialAbility();
        ability.setAbilityId(BEEN_THUMBS_UP);
        ability.setAbilityName("被赞");
        ability.setFactor(1);
        ability.setAbilityType(SocialAbilityType.THUMBS_UP);
        items.add(ability);
        ability = new SpecificSocialAbility();
        ability.setAbilityId(THUMBS_UP_OTHERS);
        ability.setAbilityName("点赞");
        ability.setFactor(1);
        ability.setAbilityType(SocialAbilityType.THUMBS_UP);
        items.add(ability);

        return items;
    }

    public long itemSize() {
        return 2;
    }

    public boolean existItem(int itemId) {
        return (BEEN_THUMBS_UP ==itemId || THUMBS_UP_OTHERS ==itemId);
    }

    @Override
    public SpecificSocialAbility getItem(int itemId) {
        SpecificSocialAbility ability = null;
        if (1==itemId) {
            ability = new SpecificSocialAbility();
            ability.setAbilityId(BEEN_THUMBS_UP);
            ability.setAbilityName("被赞");
            ability.setFactor(1);
            ability.setAbilityType(SocialAbilityType.THUMBS_UP);
        }
        else if (2==itemId) {
            ability = new SpecificSocialAbility();
            ability.setAbilityId(THUMBS_UP_OTHERS);
            ability.setAbilityName("点赞");
            ability.setFactor(1);
            ability.setAbilityType(SocialAbilityType.THUMBS_UP);
        }
        return ability;
    }
}
