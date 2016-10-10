package com.cooltoo.nurse360.entities;

import com.cooltoo.constants.CommonStatus;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by hp on 2016/10/9.
 */
@Entity
@Table(name = "nurse360_file_storage")
public class Nurse360FileStorageEntity {

    private long id;
    private Date time;
    private CommonStatus status;
    private String realName;
    private String relativePath;

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

    @Column(name = "file_real_name")
    public String getRealName() {
        return realName;
    }

    @Column(name = "file_path")
    public String getRelativePath() {
        return relativePath;
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

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public void setRelativePath(String relativePath) {
        this.relativePath = relativePath;
    }

    @Override
    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(getClass()).append("@").append(hashCode()).append("[");
        msg.append("id=").append(id);
        msg.append(", time=").append(time);
        msg.append(", status=").append(status);
        msg.append(", realName=").append(realName);
        msg.append(", relativePath=").append(relativePath);
        msg.append("]");
        return msg.toString();
    }
}
