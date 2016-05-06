package com.cooltoo.constants;

import java.util.*;

/**
 * Created by yzzhao on 3/21/16.
 */
public enum SocialAbilityType {
    COMMUNITY,   //社区徽章,包括发图,点赞,评论等
    SKILL,       //技能徽章
    OCCUPATION,  //职业徽章,比如:医院,科室等关于护士简历方面
    THUMBS_UP_ME,//点赞
    THUMBS_UP_OTHERS, // 被点赞
    COMMENT_MADE;//发评论

    public static SocialAbilityType parseString(String skill) {
        if (COMMUNITY.name().equalsIgnoreCase(skill)) {
            return COMMUNITY;
        }
        else if (SKILL.name().equalsIgnoreCase(skill)) {
            return SKILL;
        }
        else if (OCCUPATION.name().equalsIgnoreCase(skill)) {
            return OCCUPATION;
        }
        else if (THUMBS_UP_ME.name().equalsIgnoreCase(skill)) {
            return THUMBS_UP_ME;
        }
        else if (THUMBS_UP_OTHERS.name().equalsIgnoreCase(skill)) {
            return THUMBS_UP_OTHERS;
        }
        else if (COMMENT_MADE.name().equalsIgnoreCase(skill)) {
            return COMMENT_MADE;
        }
        return null;
    }

    public static List<String> getAllValues() {
        List<String> allEnums = new ArrayList<String>();
        allEnums.add(COMMUNITY.name());
        allEnums.add(SKILL.name());
        allEnums.add(OCCUPATION.name());
        allEnums.add(THUMBS_UP_ME.name());
        allEnums.add(THUMBS_UP_OTHERS.name());
        allEnums.add(COMMENT_MADE.name());
        return allEnums;
    }
}
