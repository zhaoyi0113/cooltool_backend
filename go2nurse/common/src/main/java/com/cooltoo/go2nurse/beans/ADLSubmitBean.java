package com.cooltoo.go2nurse.beans;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhaolisong on 01/03/2017.
 */
public class ADLSubmitBean {

    private long   questionnaireId = 0;
    private String questionnaireTitle = "";
    private String conclusionItem = "";
    private String conclusionInterval= "";
    private int    conclusionScore= 0;
    // question and user selection
    private long questionId = 0;
    private String questionContent = "";
    private String item  = "";
    private int score = 0;

    public long getQuestionId() {
        return questionId;
    }
    public void setQuestionId(long questionId) {
        this.questionId = questionId;
    }

    public String getQuestionContent() {
        return questionContent;
    }
    public void setQuestionContent(String questionContent) {
        this.questionContent = questionContent;
    }

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

    public long getQuestionnaireId() {
        return questionnaireId;
    }
    public void setQuestionnaireId(long questionnaireId) {
        this.questionnaireId = questionnaireId;
    }

    public String getQuestionnaireTitle() {
        return questionnaireTitle;
    }
    public void setQuestionnaireTitle(String questionnaireTitle) {
        this.questionnaireTitle = questionnaireTitle;
    }

    public String getConclusionItem() {
        return conclusionItem;
    }
    public void setConclusionItem(String conclusionItem) {
        this.conclusionItem = conclusionItem;
    }

    public String getConclusionInterval() {
        return conclusionInterval;
    }
    public void setConclusionInterval(String conclusionInterval) {
        this.conclusionInterval = conclusionInterval;
    }

    public int getConclusionScore() {
        return conclusionScore;
    }
    public void setConclusionScore(int conclusionScore) {
        this.conclusionScore = conclusionScore;
    }
}
