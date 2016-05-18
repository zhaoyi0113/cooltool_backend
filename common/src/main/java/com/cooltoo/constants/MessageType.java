package com.cooltoo.constants;

/**
 * Created by zhaolisong on 16/5/18.
 */
public enum MessageType {
    ThumbsUp, Comment, SkillApproved;

    public static MessageType parseString(String msgType) {
        if (ThumbsUp.name().equalsIgnoreCase(msgType)) {
            return ThumbsUp;
        }
        else if (Comment.name().equalsIgnoreCase(msgType)) {
            return Comment;
        }
        else if (SkillApproved.name().equalsIgnoreCase(msgType)) {
            return SkillApproved;
        }
        return null;
    }

}
