package com.cooltoo.entities;

import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.PlatformType;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by yzzhao on 5/22/16.
 */
@Entity
@Table(name = "platform_version")
public class PlatformVersionEntity {

    private int id;

    private PlatformType platformType;

    private String version;

    private Date timeCreated;

    private CommonStatus status;

    private String link;

    private int required;

    @Id
    @GeneratedValue
    @Column(name = "id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Column(name = "platform_type")
    @Enumerated
    public PlatformType getPlatformType() {
        return platformType;
    }

    public void setPlatformType(PlatformType platformType) {
        this.platformType = platformType;
    }

    @Column(name = "version")
    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Column(name = "time_created")
    public Date getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(Date timeCreated) {
        this.timeCreated = timeCreated;
    }

    @Column(name = "status")
    @Enumerated
    public CommonStatus getStatus() {
        return status;
    }

    public void setStatus(CommonStatus status) {
        this.status = status;
    }

    @Column(name = "link")
    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    @Column(name = "required")
    public int getRequired() {
        return required;
    }

    public void setRequired(int required) {
        this.required = required;
    }
}
