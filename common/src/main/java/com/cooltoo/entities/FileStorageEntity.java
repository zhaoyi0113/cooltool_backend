package com.cooltoo.entities;

import javax.persistence.*;

/**
 * Created by yzzhao on 2/26/16.
 */
@Entity
@Table(name = "nursego_file_storage")
public class FileStorageEntity {

    private long id;

    private String fileRealname;

    private String filePath;

    @Id
    @GeneratedValue
    @Column(name = "id")
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Column(name = "file_path")
    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    @Column(name = "file_real_name")
    public String getFileRealname() {
        return fileRealname;
    }

    public void setFileRealname(String fileRealname) {
        this.fileRealname = fileRealname;
    }

    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(this.getClass()).append("@").append(hashCode()).append("[");
        msg.append("id=").append(id).append(" , ");
        msg.append("fileRealname=").append(fileRealname).append(" , ");
        msg.append("filePath=").append(filePath);
        msg.append("]");
        return msg.toString();
    }
}
