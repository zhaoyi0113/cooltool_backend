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

    public List<SpecificSocialAbility> getItems() {
        List<SpecificSocialAbility> items = new ArrayList<>();
        SpecificSocialAbility ability;

        ability = new SpecificSocialAbility();
        ability.setAbilityId(1);
        ability.setAbilityName("评论");
        ability.setAbilityType(SocialAbilityType.COMMENT);
        items.add(ability);
        ability = new SpecificSocialAbility();
        ability.setAbilityId(2);
        ability.setAbilityName("答题");
        ability.setAbilityType(SocialAbilityType.COMMENT);
        items.add(ability);

        return items;
    }

    public long itemSize() {
        return 2;
    }

    public boolean existItem(int itemId) {
        return (1==itemId || 2==itemId);
    }

}
