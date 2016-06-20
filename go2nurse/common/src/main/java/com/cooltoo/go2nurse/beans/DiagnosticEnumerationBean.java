package com.cooltoo.go2nurse.beans;

/**
 * Created by hp on 2016/6/13.
 */
public class DiagnosticEnumerationBean {

    private long id;
    private String name;

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean equals(Object obj) {
        if (obj instanceof DiagnosticEnumerationBean) {
            DiagnosticEnumerationBean bean = (DiagnosticEnumerationBean) obj;
            return bean.getId()==id && name.equals(bean.getName());
        }
        return false;
    }

    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(getClass()).append("@").append(hashCode()).append("[");
        msg.append("id=").append(id);
        msg.append(", name=").append(name);
        msg.append("]");
        return msg.toString();
    }
}
