package com.cooltoo.go2nurse.beans;

import com.cooltoo.beans.HospitalBean;
import com.cooltoo.beans.HospitalDepartmentBean;
import com.cooltoo.go2nurse.constants.ServiceVendorType;

/**
 * Created by zhaolisong on 31/03/2017.
 */
public class ServiceItemVendorBean {

    private ServiceVendorType vendorType;
    private long vendorId;
    private long departId;
    private ServiceVendorBean      vendor;
    private HospitalBean           hospital;
    private HospitalDepartmentBean depart;

    public ServiceVendorType getVendorType() {
        return vendorType;
    }
    public void setVendorType(ServiceVendorType vendorType) {
        this.vendorType = vendorType;
    }

    public long getVendorId() {
        return vendorId;
    }
    public void setVendorId(long vendorId) {
        this.vendorId = vendorId;
    }

    public long getDepartId() {
        return departId;
    }
    public void setDepartId(long departId) {
        this.departId = departId;
    }

    public ServiceVendorBean getVendor() {
        return vendor;
    }
    public void setVendor(ServiceVendorBean vendor) {
        this.vendor = vendor;
    }

    public HospitalBean getHospital() {
        return hospital;
    }
    public void setHospital(HospitalBean hospital) {
        this.hospital = hospital;
    }

    public HospitalDepartmentBean getDepart() {
        return depart;
    }
    public void setDepart(HospitalDepartmentBean depart) {
        this.depart = depart;
    }

    public boolean equals(Object obj) {
        if (obj instanceof ServiceItemVendorBean) {
            ServiceItemVendorBean one = (ServiceItemVendorBean) obj;
            return vendorType.equals(one.getVendorType()) && vendorId==one.getVendorId() && departId==one.getDepartId();
        }
        return false;
    }
}
