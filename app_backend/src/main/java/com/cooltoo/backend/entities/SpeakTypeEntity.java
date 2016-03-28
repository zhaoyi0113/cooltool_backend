package com.cooltoo.backend.entities;

import com.cooltoo.constants.SpeakType;

import javax.persistence.*;

/**
 * Created by zhaolisong on 16/3/28.
 */
@Entity
@Table(name = "speak_type")
public class SpeakTypeEntity {
    private int id;
    private String name;
    private SpeakType type;
    /** 每一条该类型的 Speak 对应的分值,用于计算等级的 */
    private int factor;
    /** 该类型点亮后的图片 ID */
    private long imageId;
    /** 该类型未点亮的图片 ID */
    private long disableImageId;

    @Id
    @GeneratedValue
    @Column(name = "id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "type")
    @Enumerated
    public SpeakType getType() {
        return type;
    }

    public void setType(SpeakType type) {
        this.type = type;
    }

    @Column(name = "factor")
    public int getFactor() {
        return factor;
    }

    public void setFactor(int factor) {
        this.factor = factor;
    }

    @Column(name = "image_id")
    public long getImageId() {
        return imageId;
    }

    public void setImageId(long imageId) {
        this.imageId = imageId;
    }

    @Column(name = "disable_image_id")
    public long getDisableImageId() {
        return disableImageId;
    }

    public void setDisableImageId(long disableImageId) {
        this.disableImageId = disableImageId;
    }

    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(getClass()).append("@").append(hashCode()).append("[");
        msg.append("id=").append(id).append(", ");
        msg.append("name=").append(name).append(", ");
        msg.append("type=").append(type).append(", ");
        msg.append("factor=").append(factor).append(", ");
        msg.append("imageId=").append(imageId).append(", ");
        msg.append("disableImageId=").append(disableImageId).append("]");
        return msg.toString();
    }

}
