package com.cooltoo.go2nurse.beans;

/**
 * Created by hp on 2016/6/29.
 */
public class QuestionOptionBean {

    private String item;
    private int score;

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(getClass()).append("@").append(hashCode()).append("[");
        msg.append("item=").append(item);
        msg.append(", score=").append(score);
        msg.append("]");
        return msg.toString();
    }
}
