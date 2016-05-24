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
public class CommentAbilityTypeConverter implements SocialAbilityTypeConverter {

    public static final int COMMENT = 1;
    public static final int ANSWER = 2;

    public List<SpecificSocialAbility> getItems() {
        List<SpecificSocialAbility> items = new ArrayList<>();
        SpecificSocialAbility ability;

        ability = new SpecificSocialAbility();
        ability.setAbilityId(COMMENT);
        ability.setAbilityName("评论");
        ability.setFactor(1);
        ability.setAbilityType(SocialAbilityType.COMMENT);
        items.add(ability);
        ability = new SpecificSocialAbility();
        ability.setAbilityId(ANSWER);
        ability.setAbilityName("答题");
        ability.setFactor(1);
        ability.setAbilityType(SocialAbilityType.COMMENT);
        items.add(ability);

        return items;
    }

    public long itemSize() {
        return 2;
    }

    public boolean existItem(int itemId) {
        return (COMMENT ==itemId || ANSWER ==itemId);
    }

    @Override
    public SpecificSocialAbility getItem(int itemId) {
        SpecificSocialAbility ability = null;
        if (1==itemId) {
            ability = new SpecificSocialAbility();
            ability.setAbilityId(COMMENT);
            ability.setAbilityName("评论");
            ability.setFactor(1);
            ability.setAbilityType(SocialAbilityType.COMMENT);
        }
        else if (2==itemId) {
            ability = new SpecificSocialAbility();
            ability.setAbilityId(ANSWER);
            ability.setAbilityName("答题");
            ability.setFactor(1);
            ability.setAbilityType(SocialAbilityType.COMMENT);
        }
        return ability;
    }
}
