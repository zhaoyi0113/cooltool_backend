package com.cooltoo.go2nurse.beans;

import java.util.List;

/**
 * Created by zhaolisong on 2016/10/28.
 */
public class CategoryCoursesOrderGroup {

    private int hospitalId;
    private int departmentId;
    private long categoryId;


    /** Cache the hash code for the string */
    private int hash; // Default to 0

    public int getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(int hospitalId) {
        this.hospitalId = hospitalId;
    }

    public int getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(int departmentId) {
        this.departmentId = departmentId;
    }

    public long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }

    public void resetHashCode() {
        hash = 0;
        hash = hashCode();
    }

    public boolean equals(int hospitalId, int departmentId, long categoryId) {
        return this.hospitalId==hospitalId
                && this.departmentId==departmentId
                && this.categoryId==categoryId;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof CategoryCoursesOrderGroup) {
            CategoryCoursesOrderGroup group = (CategoryCoursesOrderGroup) obj;
            return group.getHospitalId()==hospitalId
                    && group.getDepartmentId()==departmentId
                    && group.getCategoryId()==categoryId;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hashCode = hash;
        if (hashCode==0 && (0!=hospitalId || 0!=departmentId || 0!=categoryId)) {
            String hashString = hospitalId + "_" + departmentId + "_" + categoryId;
            hash = hashString.hashCode();
            hashCode = hash;
        }
        return hashCode;
    }

    @Override
    public CategoryCoursesOrderGroup clone() {
        CategoryCoursesOrderGroup clone = new CategoryCoursesOrderGroup();
        clone.hospitalId = hospitalId;
        clone.departmentId = departmentId;
        clone.categoryId = categoryId;
        return clone;
    }
}
