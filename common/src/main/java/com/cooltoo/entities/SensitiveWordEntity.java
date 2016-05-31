package com.cooltoo.entities;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.SensitiveWordType;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by hp on 2016/5/31.
 */
@Entity
@Table(name = "sensitive_words")
public class SensitiveWordEntity {

    private int id;
    private String word;
    private SensitiveWordType type;
    private Date time;
    private CommonStatus status;

    @Id
    @GeneratedValue
    @Column(name = "id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Column(name = "word")
    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    @Column(name = "type")
    public SensitiveWordType getType() {
        return type;
    }

    public void setType(SensitiveWordType type) {
        this.type = type;
    }

    @Column(name = "time_created")
    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    @Column(name = "status")
    public CommonStatus getStatus() {
        return status;
    }

    public void setStatus(CommonStatus status) {
        this.status = status;
    }

    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(getClass()).append("@").append(hashCode()).append("[");
        msg.append("id=").append(id);
        msg.append(", word=").append(word);
        msg.append(", type=").append(type);
        msg.append(", time=").append(time);
        msg.append(", status=").append(status);
        msg.append("]");
        return msg.toString();
    }
}
