package com.cooltoo.nurse360.entities;

import com.cooltoo.constants.AdminUserType;
import com.cooltoo.constants.CommonStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by zhaolisong on 2016/11/9.
 */
@Entity
@Table(name = "nurse360_hospital_administrator")
public class HospitalAdminEntity implements UserDetails{

    private long id;
    private Date time;
    private CommonStatus status;
    private AdminUserType adminType;
    private String name;
    private String password;
    private String telephone ="";
    private String email="";
    private int hospitalId;
    private int departmentId;


    @Id
    @GeneratedValue
    @Column(name = "id")
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Column(name = "time_created")
    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    @Column(name = "status")
    @Enumerated
    public CommonStatus getStatus() {
        return status;
    }

    public void setStatus(CommonStatus status) {
        this.status = status;
    }

    @Column(name = "admin_type")
    @Enumerated
    public AdminUserType getAdminType() {
        return adminType;
    }

    public void setAdminType(AdminUserType adminType) {
        this.adminType = adminType;
    }

    @Column(name = "name")
    public String getName() {
        return name;
    }

    @JsonProperty
    public void setName(String name) {
        this.name = name;
    }



    @Column(name = "password")
    public String getPassword() {
        return password;
    }

    @JsonProperty
    public void setPassword(String password) {
        this.password = password;
    }

    @Column(name = "telephone")
    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    @Column(name = "email")
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Column(name = "hospital_id")
    public int getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(int hospitalId) {
        this.hospitalId = hospitalId;
    }

    @Column(name = "department_id")
    public int getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(int departmentId) {
        this.departmentId = departmentId;
    }

    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(getClass()).append("@").append(hashCode()).append("[");
        msg.append("id=").append(id);
        msg.append(", time=").append(time);
        msg.append(", status=").append(status);
        msg.append(", adminType=").append(adminType);
        msg.append(", name=").append(name);
        msg.append(", password=").append(password);
        msg.append(", telephone=").append(telephone);
        msg.append(", email=").append(email);
        msg.append(", hospitalId=").append(hospitalId);
        msg.append(", departmentId=").append(departmentId);
        msg.append("]");
        return msg.toString();
    }

    @Override
    @Transient
    public String getUsername() {
        return name;
    }

    @Override
    @Transient
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    @Transient
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    @Transient
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    @Transient
    public boolean isEnabled() {
        return CommonStatus.ENABLED.equals(status);
    }

    @Override
    @Transient
    public Collection<? extends GrantedAuthority> getAuthorities() {
        //TODO: read role from database
        Set<GrantedAuthority> authorites = new HashSet<>();
        authorites.add(new SimpleGrantedAuthority("ADMIN"));
        return authorites;
    }
}
