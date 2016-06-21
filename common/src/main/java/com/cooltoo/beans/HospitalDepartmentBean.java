package com.cooltoo.beans;

import java.util.List;

/**
 * Created by lg380357 on 2016/3/5.
 */
public class HospitalDepartmentBean {

    private int id;
    private String name;
    private String description;
    private int enable;
    private long imageId;
    private String imageUrl;
    private long disableImageId;
    private String disableImageUrl;
    private int parentId;
    private boolean parentValid;
    private String uniqueId;
    private List<HospitalDepartmentBean> subDepartment;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getEnable() {
        return this.enable;
    }

    public void setEnable(int enable) {
        this.enable = enable;
    }

    public long getImageId() {
        return this.imageId;
    }

    public void setImageId(long imageId) {
        this.imageId = imageId;
    }

    public String getImageUrl() {
        return this.imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public long getDisableImageId() {
        return this.disableImageId;
    }

    public void setDisableImageId(long disableImageId) {
        this.disableImageId = disableImageId;
    }

    public String getDisableImageUrl() {
        return this.disableImageUrl;
    }

    public void setDisableImageUrl(String disableImageUrl) {
        this.disableImageUrl = disableImageUrl;
    }

    public int getParentId() {
        return this.parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public boolean getParentValid() {
        return this.parentValid;
    }

    public void setParentValid(boolean parentValid) {
        this.parentValid = parentValid;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public List<HospitalDepartmentBean> getSubDepartment() {
        return this.subDepartment;
    }

    public void setSubDepartment(List<HospitalDepartmentBean> subDepartment) {
        this.subDepartment = subDepartment;
    }

    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(this.getClass()).append("@").append(hashCode()).append("[");
        msg.append("id=").append(id);
        msg.append(", name=").append(name);
        msg.append(", description=").append(description);
        msg.append(", enable=").append(enable);
        msg.append(", imageId=").append(imageId);
        msg.append(", imageUrl=").append(imageUrl);
        msg.append(", disableImageId=").append(disableImageId);
        msg.append(", disableImageUrl=").append(disableImageUrl);
        msg.append(", parentId=").append(parentId);
        msg.append(", uniqueId=").append(uniqueId);
        msg.append("]");
        return msg.toString();
    }
}
