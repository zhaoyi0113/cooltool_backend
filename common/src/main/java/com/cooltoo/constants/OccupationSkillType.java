package com.cooltoo.constants;

import com.cooltoo.util.VerifyUtil;

import java.util.*;

/**
 * Created by yzzhao on 3/21/16.
 */
public enum OccupationSkillType {
    COMMUNITY, //社区徽章,包括发图,点赞,评论等
    SKILL, //技能徽章
    OCCUPATION, //职业徽章,比如:医院,科室等关于护士简历方面
    COMMUNITY_SPEAK_SMUG,        //社区徽章--臭美
    COMMUNITY_SPEAK_CATHART,     //社区徽章--吐槽
    COMMUNITY_SPEAK_ASK_QUESTION; //社区徽章--提问


    public static OccupationSkillType parseString(String skill) {
        if (COMMUNITY.name().equalsIgnoreCase(skill)) {
            return COMMUNITY;
        }
        else if (COMMUNITY_SPEAK_SMUG.name().equalsIgnoreCase(skill)) {
            return COMMUNITY_SPEAK_SMUG;
        }
        else if (COMMUNITY_SPEAK_CATHART.name().equalsIgnoreCase(skill)) {
            return COMMUNITY_SPEAK_CATHART;
        }
        else if (COMMUNITY_SPEAK_ASK_QUESTION.name().equalsIgnoreCase(skill)) {
            return COMMUNITY_SPEAK_ASK_QUESTION;
        }
        else if (SKILL.name().equalsIgnoreCase(skill)) {
            return SKILL;
        }
        else if (OCCUPATION.name().equalsIgnoreCase(skill)) {
            return OCCUPATION;
        }
        return null;
    }

    public static OccupationSkillType parseInt(int skill) {
        OccupationSkillType retVal = null;
        if (COMMUNITY.ordinal()==(skill)) {
            retVal = COMMUNITY;
        }
        else if (COMMUNITY_SPEAK_SMUG.ordinal()==(skill)) {
            return COMMUNITY_SPEAK_SMUG;
        }
        else if (COMMUNITY_SPEAK_CATHART.ordinal()==(skill)) {
            return COMMUNITY_SPEAK_CATHART;
        }
        else if (COMMUNITY_SPEAK_ASK_QUESTION.ordinal()==(skill)) {
            return COMMUNITY_SPEAK_ASK_QUESTION;
        }
        else if (SKILL.ordinal()==(skill)) {
            retVal = SKILL;
        }
        else if (OCCUPATION.ordinal()==(skill)) {
            retVal = OCCUPATION;
        }
        return retVal;
    }

    public static List<String> getAllValues() {
        List<String> allEnums = new ArrayList<String>();
        allEnums.add(COMMUNITY.name());
        allEnums.add(SKILL.name());
        allEnums.add(OCCUPATION.name());
        allEnums.add(COMMUNITY_SPEAK_SMUG.name());
        allEnums.add(COMMUNITY_SPEAK_CATHART.name());
        allEnums.add(COMMUNITY_SPEAK_ASK_QUESTION.name());
        return allEnums;
    }
}
