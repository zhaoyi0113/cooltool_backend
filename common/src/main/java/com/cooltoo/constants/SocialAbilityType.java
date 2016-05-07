package com.cooltoo.constants;

import java.util.*;

/**
 * Created by yzzhao on 3/21/16.
 */
public enum SocialAbilityType {
    COMMUNITY("发言"),   //社区徽章,包括发图,点赞,评论等
    SKILL("技能"),       //技能徽章
    OCCUPATION("职业"),  //职业徽章,比如:医院,科室等关于护士简历方面
    THUMBS_UP("点赞"),   //点赞/被点赞
    COMMENT("评论");     //发评论/答题"

    String typeName;

    SocialAbilityType(String typeName) {
        this.typeName = typeName;
    }

    public String getTypeName() {
        return typeName;
    }

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
        else if (THUMBS_UP.name().equalsIgnoreCase(skill)) {
            return THUMBS_UP;
        }
        else if (COMMENT.name().equalsIgnoreCase(skill)) {
            return COMMENT;
        }
        return null;
    }

    public static Map<String, String> getAllValues() {
        Map<String, String> allEnums = new HashMap<>();
        allEnums.put(COMMUNITY.name(), COMMUNITY.getTypeName());
        allEnums.put(SKILL.name(), SKILL.getTypeName());
        allEnums.put(OCCUPATION.name(), OCCUPATION.getTypeName());
        allEnums.put(THUMBS_UP.name(), THUMBS_UP.getTypeName());
        allEnums.put(COMMENT.name(), COMMENT.getTypeName());
        return allEnums;
    }

}
