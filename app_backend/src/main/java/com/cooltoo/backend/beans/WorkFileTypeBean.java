package com.cooltoo.backend.beans;

import com.cooltoo.constants.WorkFileType;

/**
 * Created by zhaolisong on 16/3/29.
 */
public class WorkFileTypeBean {

    private int id;
    private String name;
    private WorkFileType type;
    private int factor;
    private int maxFileCount;
    private int minFileCount;
    private long imageId;
    private String imageUrl;
    private long disableImageId;
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

    public WorkFileType getType() {
        return type;
    }

    public void setType(WorkFileType type) {
        this.type = type;
    }

    public int getFactor() {
        return factor;
    }

    public void setFactor(int factor) {
        this.factor = factor;
    }

    public int getMaxFileCount() {
        return maxFileCount;
    }

    public void setMaxFileCount(int maxFileCount) {
        this.maxFileCount = maxFileCount;
    }

    public int getMinFileCount() {
        return minFileCount;
    }

    public void setMinFileCount(int minFileCount) {
        this.minFileCount = minFileCount;
    }

    public long getImageId() {
        return imageId;
    }

    public void setImageId(long imageId) {
        this.imageId = imageId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public long getDisableImageId() {
        return disableImageId;
    }

    public void setDisableImageId(long disableImageId) {
        this.disableImageId = disableImageId;
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
        msg.append("maxFileCount=").append(maxFileCount).append(", ");
        msg.append("minFileCount=").append(minFileCount).append(", ");
        msg.append("imageId=").append(imageId).append(", ");
        msg.append("disableImageId").append(disableImageId).append("]");
        return msg.toString();
    }
}
