package com.cooltoo.backend.converter.social_ability;

import com.cooltoo.beans.SpecificSocialAbility;
import org.springframework.data.domain.Sort;
import java.util.List;

/**
 * Created by zhaolisong on 16/5/6.
 */
public interface SocialAbilityTypeConverter {
    Sort sorter = new Sort(new Sort.Order(Sort.Direction.ASC, "id"));
    List<SpecificSocialAbility> getItems();
    long itemSize();
    boolean existItem(int itemId);
    SpecificSocialAbility getItem(int itemId);
}
