package com.cooltoo.go2nurse.beans;

import java.util.List;

/**
 * Created by hp on 2016/7/11.
 */
public class QuestionnaireStatisticsBean {
    private long answerTimes;
    private List<ConclusionBean> conclusions;

    public long getAnswerTimes() {
        return answerTimes;
    }

    public List<ConclusionBean> getConclusions() {
        return conclusions;
    }

    public void setAnswerTimes(long answerTimes) {
        this.answerTimes = answerTimes;
    }

    public void settConclusions(List<ConclusionBean> conclusions) {
        this.conclusions = conclusions;
    }

    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(getClass().getName()).append("@").append(hashCode()).append("[");
        msg.append(", score=").append(answerTimes);
        msg.append(", conclusion=").append(conclusions);
        msg.append("]");
        return msg.toString();
    }

    public static class ConclusionBean {
        private QuestionnaireConclusionBean conclusion;
        private long resultCount;

        public QuestionnaireConclusionBean getConclusion() {
            return conclusion;
        }

        public void setConclusion(QuestionnaireConclusionBean conclusion) {
            this.conclusion = conclusion;
        }

        public long getResultCount() {
            return resultCount;
        }

        public void setResultCount(long resultCount) {
            this.resultCount = resultCount;
        }

        public String toString() {
            StringBuilder msg = new StringBuilder();
            msg.append(getClass().getName()).append("@").append(hashCode()).append("[");
            msg.append(", conclusion=").append(conclusion);
            msg.append(", resultCount=").append(resultCount);
            msg.append("]");
            return msg.toString();
        }
    }
}
