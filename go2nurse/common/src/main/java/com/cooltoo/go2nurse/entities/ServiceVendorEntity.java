package com.cooltoo.go2nurse.entities;

import com.cooltoo.constants.CommonStatus;
import edu.umd.cs.findbugs.annotations.OverrideMustInvoke;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by hp on 2016/7/19.
 */
@Entity
@Table(name = "go2nurse_service_vendor")
public class ServiceVendorEntity {

    private long id;
    private Date time;
    private CommonStatus status;
    private String name;
    private String description;
    private long logoId;

    @Id
    @GeneratedValue
    @Column(name = "id")
    public long getId() {
        return id;
    }

    @Column(name = "time_created")
    public Date getTime() {
        return time;
    }

    @Column(name = "status")
    @Enumerated
    public CommonStatus getStatus() {
        return status;
    }

    @Column(name = "name")
    public String getName() {
        return name;
    }

    @Column(name = "description")
    public String getDescription() {
        return description;
    }

    @Column(name = "logo_id")
    public long getLogoId() {
        return logoId;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public void setStatus(CommonStatus status) {
        this.status = status;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setLogoId(long logoId) {
        this.logoId = logoId;
    }

    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(getClass().getName()).append("@").append(hashCode()).append("[");
        msg.append("id=").append(id);
        msg.append(", name=").append(name);
        msg.append(", description=").append(description);
        msg.append(", logoId=").append(logoId);
        msg.append(", status=").append(status);
        msg.append(", time=").append(time);
        msg.append("]");
        return msg.toString();
    }
}
