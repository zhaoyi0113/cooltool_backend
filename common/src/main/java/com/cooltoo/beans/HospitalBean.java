package com.cooltoo.beans;

/**
 * Created by lg380357 on 2016/3/5.
 */
public class HospitalBean {
    private int id;
    private String name;
    private String aliasName;
    private int province;
    private RegionBean provinceB;
    private int city;
    private RegionBean cityB;
    private int district;
    private RegionBean districtB;
    private String address;
    private int enable;
    private int supportGo2nurse;

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

    public int getProvince() {
        return province;
    }

    public void setProvince(int province) {
        this.province = province;
    }

    public int getCity() {
        return city;
    }

    public void setCity(int city) {
        this.city = city;
    }

    public int getDistrict() {
        return district;
    }

    public void setDistrict(int district) {
        this.district = district;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getEnable() {
        return enable;
    }

    public void setEnable(int enable) {
        this.enable = enable;
    }

    public RegionBean getProvinceBean() {
        return this.provinceB;
    }

    public void setProvinceBean(RegionBean provinceB) {
        this.provinceB = provinceB;
    }

    public RegionBean getCityBean() {
        return this.cityB;
    }

    public void setCityBean(RegionBean cityB) {
        this.cityB = cityB;
    }

    public RegionBean getDistrictBean() {
        return this.districtB;
    }

    public void setDistrictBean(RegionBean districtB) {
        this.districtB = districtB;
    }

    public String getAliasName() {
        return aliasName;
    }

    public void setAliasName(String aliasName) {
        this.aliasName = aliasName;
    }

    public int getSupportGo2nurse() {
        return supportGo2nurse;
    }

    public void setSupportGo2nurse(int supportGo2nurse) {
        this.supportGo2nurse = supportGo2nurse;
    }

    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(this.getClass()).append("@").append(hashCode()).append("[");
        msg.append("id=").append(id);
        msg.append(", name=").append(name);
        msg.append(", aliasName=").append(aliasName);
        msg.append(", province=").append(province);
        msg.append(", city=").append(city);
        msg.append(", district=").append(district);
        msg.append(", address=").append(address);
        msg.append(", enable=").append(enable);
        msg.append(", supportGo2nurse=").append(supportGo2nurse);
        msg.append("]");
        return msg.toString();
    }
}
