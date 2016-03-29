package com.cooltoo.backend.beans;

import com.cooltoo.constants.SpeakType;

/**
 * Created by zhaolisong on 16/3/28.
 */
public class SpeakTypeBean {

    private int id;
    private String name;
    private SpeakType type;
    /** 每一条该类型的 Speak 对应的分值,用于计算等级的 */
    private int factor;
    /** 该类型点亮后的图片 ID */
    private long imageId;
    /** 点亮后的图片 URL */
    private String imageUrl;
    /** 该类型未点亮的图片 ID */
    private long disableImageId;
    /** 该类型未点亮的图片 ID */
    private String disableImageUrl;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SpeakType getType() {
        return type;
    }

    public void setType(SpeakType type) {
        this.type = type;
    }

    public int getFactor() {
        return factor;
    }

    public void setFactor(int factor) {
        this.factor = factor;
    }

    public long getImageId() {
        return imageId;
    }

    public void setImageId(long imageId) {
        this.imageId = imageId;
    }

    public long getDisableImageId() {
        return disableImageId;
    }

    public void setDisableImageId(long disableImageId) {
        this.disableImageId = disableImageId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDisableImageUrl() {
        return disableImageUrl;
    }

    public void setDisableImageUrl(String disableImageUrl) {
        this.disableImageUrl = disableImageUrl;
    }

    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(getClass()).append("@").append(hashCode()).append("[");
        msg.append("id=").append(id).append(", ");
        msg.append("name=").append(name).append(", ");
        msg.append("type=").append(type).append(", ");
        msg.append("factor=").append(factor).append(", ");
        msg.append("imageId=").append(imageId).append(", ");
        msg.append("imageUrl=").append(imageUrl).append(", ");
        msg.append("disableImageId=").append(disableImageId).append(", ");
        msg.append("disableImageUrl=").append(disableImageUrl).append("]");
        return msg.toString();
    }
}
