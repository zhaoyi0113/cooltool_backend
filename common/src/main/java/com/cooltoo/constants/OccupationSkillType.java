package com.cooltoo.constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by yzzhao on 3/21/16.
 */
public enum OccupationSkillType {
    COMMUNITY, //社区徽章,包括发图,点赞,评论等
    SKILL, //技能徽章
    OCCUPATION; //职业徽章,比如:医院,科室等关于护士简历方面

    public static OccupationSkillType parseString(String skill) {
        OccupationSkillType retVal = null;
        if (COMMUNITY.name().equalsIgnoreCase(skill)) {
            retVal = COMMUNITY;
        }
        else if (SKILL.name().equalsIgnoreCase(skill)) {
            retVal = SKILL;
        }
        else if (OCCUPATION.name().equalsIgnoreCase(skill)) {
            retVal = OCCUPATION;
        }
        return retVal;
    }

    public static OccupationSkillType parseInt(int skill) {
        OccupationSkillType retVal = null;
        if (COMMUNITY.ordinal()==(skill)) {
            retVal = COMMUNITY;
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
        return allEnums;
    }
}
