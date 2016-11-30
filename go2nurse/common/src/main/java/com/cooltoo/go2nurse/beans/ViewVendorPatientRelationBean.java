package com.cooltoo.go2nurse.beans;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhaolisong on 2016/11/24.
 */
public class ViewVendorPatientRelationBean {
    private long userId;
    private long patientId;
    private UserBean user;
    private PatientBean patient;
    private Map<String, Object> properties = new HashMap<String, Object>();

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getPatientId() {
        return patientId;
    }

    public void setPatientId(long patientId) {
        this.patientId = patientId;
    }

    public UserBean getUser() {
        return user;
    }

    public void setUser(UserBean user) {
        this.user = user;
    }

    public PatientBean getPatient() {
        return patient;
    }

    public void setPatient(PatientBean patient) {
        this.patient = patient;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(String key, Object value) {
        if (null==key || key.trim().length()==0 || null==value) {
            return;
        }
        properties.put(key.trim(), value);
    }
}
