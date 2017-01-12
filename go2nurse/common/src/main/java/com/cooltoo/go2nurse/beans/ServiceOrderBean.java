package com.cooltoo.go2nurse.beans;

import com.cooltoo.beans.HospitalBean;
import com.cooltoo.beans.HospitalDepartmentBean;
import com.cooltoo.constants.CommonStatus;
import com.cooltoo.constants.ManagedBy;
import com.cooltoo.constants.YesNoEnum;
import com.cooltoo.go2nurse.constants.OrderStatus;
import com.cooltoo.go2nurse.constants.ServiceVendorType;
import com.cooltoo.go2nurse.constants.TimeUnit;
import com.cooltoo.util.VerifyUtil;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hp on 2016/7/13.
 */
public class ServiceOrderBean {
    private static final long ONE_HOUR_MILLISECOND = 60*60*1000;
    private static final long ONE_DAY_MILLISECOND = 24*ONE_HOUR_MILLISECOND;

    public static final String FLAG = "FLAG";
    public static final String WAIT_STAFF = "WaitStaff";
    public static final String CANNOT_FETCH_REASON = "cannot_fetch_reason";
    public static final String CANNOT_FETCH_REASON_FORBIDDEN_BY_ADMIN = "FORBIDDEN_BY_ADMIN";
    public static final String CANNOT_FETCH_REASON_FORBIDDEN_BY_HEAD_NURSE = "FORBIDDEN_BY_HEAD_NURSE";

    private long id;
    private Date time;
    private CommonStatus status;
    private ManagedBy serviceItemManagedBy;
    private long serviceItemId;
    private ServiceItemBean serviceItem;
    private ServiceVendorType vendorType;
    private long vendorId;
    private ServiceVendorBean vendor;
    private HospitalBean vendorHospital;
    private long vendorDepartId;
    private HospitalDepartmentBean vendorHospitalDepart;
    private long categoryId;
    private ServiceCategoryBean category;
    private long topCategoryId;
    private ServiceCategoryBean topCategory;
    private long userId;
    private long patientId;
    private PatientBean patient;
    private long addressId;
    private String address;
    private Date serviceStartTime;
    private int serviceTimeDuration;
    private TimeUnit serviceTimeUnit;
    private int itemCount;
    private String totalConsumption;
    private int totalConsumptionCent;
    private String preferential;
    private int preferentialCent;
    private String totalServerIncome;
    private int totalServerIncomeCent;
    private YesNoEnum needVisitPatientRecord;
    private String orderNo;
    private String orderStatus;
    private Date payTime;
    private String paymentAmount;
    private int paymentAmountCent;
    private String leaveAMessage;
    private float score;
    private Date fetchTime;
    private Date completedTime;
    private YesNoEnum isNurseFetched;
    private List<ServiceOrderChargePingPPBean> pingPP;
    private Map<String, Object> properties = new HashMap<>();

    public long getId() {
        return id;
    }

    public Date getTime() {
        return time;
    }

    public CommonStatus getStatus() {
        return status;
    }

    public ManagedBy getServiceItemManagedBy() {
        return serviceItemManagedBy;
    }

    public long getServiceItemId() {
        return serviceItemId;
    }

    public ServiceItemBean getServiceItem() {
        return serviceItem;
    }

    public ServiceVendorType getVendorType() {
        return vendorType;
    }

    public long getVendorId() {
        return vendorId;
    }

    public ServiceVendorBean getVendor() {
        return vendor;
    }

    public HospitalBean getVendorHospital() {
        return vendorHospital;
    }

    public long getVendorDepartId() {
        return vendorDepartId;
    }

    public HospitalDepartmentBean getVendorHospitalDepart() {
        return vendorHospitalDepart;
    }

    public long getCategoryId() {
        return categoryId;
    }

    public ServiceCategoryBean getCategory() {
        return category;
    }

    public long getTopCategoryId() {
        return topCategoryId;
    }

    public ServiceCategoryBean getTopCategory() {
        return topCategory;
    }

    public long getUserId() {
        return userId;
    }

    public long getPatientId() {
        return patientId;
    }

    public PatientBean getPatient() {
        return patient;
    }

    public long getAddressId() {
        return addressId;
    }

    public String getAddress() {
        return address;
    }

    public Date getServiceStartTime() {
        return serviceStartTime;
    }

    public int getServiceTimeDuration() {
        return serviceTimeDuration;
    }

    public TimeUnit getServiceTimeUnit() {
        return serviceTimeUnit;
    }

    public long calculateServiceEndTime() {
        if (null==serviceStartTime) {
            return 0;
        }
        if (null==serviceTimeUnit) {
            return serviceStartTime.getTime();
        }
        long timeDuration = 0;
        if (TimeUnit.HOUR.equals(serviceTimeUnit)) {
            timeDuration = serviceTimeDuration * ONE_HOUR_MILLISECOND;
        }
        else if (TimeUnit.DAY.equals(serviceTimeUnit)) {
            timeDuration = serviceTimeDuration * ONE_DAY_MILLISECOND;
        }
        return serviceStartTime.getTime()+timeDuration;
    }

    public int getItemCount() {
        return itemCount;
    }

    public String getTotalConsumption() {
        return totalConsumption;
    }

    public int getTotalConsumptionCent() {
        return totalConsumptionCent;
    }

    public String getPreferential() {
        return preferential;
    }

    public int getPreferentialCent() {
        return preferentialCent;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public Date getPayTime() {
        return payTime;
    }

    public String getPaymentAmount() {
        return paymentAmount;
    }

    public int getPaymentAmountCent() {
        return paymentAmountCent;
    }

    public String getLeaveAMessage() {
        return leaveAMessage;
    }

    public float getScore() {
        return score;
    }

    public Date getCompletedTime() {
        return completedTime;
    }

    public Date getFetchTime() {
        return fetchTime;
    }

    public String getTotalServerIncome() {
        return totalServerIncome;
    }
    public int getTotalServerIncomeCent() {
        return totalServerIncomeCent;
    }

    public YesNoEnum getNeedVisitPatientRecord() {
        return needVisitPatientRecord;
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

    public void setServiceItemManagedBy(ManagedBy serviceItemManagedBy) {
        this.serviceItemManagedBy = serviceItemManagedBy;
    }

    public void setServiceItemId(long serviceItemId) {
        this.serviceItemId = serviceItemId;
    }

    public void setServiceItem(ServiceItemBean serviceItem) {
        this.serviceItem = serviceItem;
    }

    public void setVendorType(ServiceVendorType vendorType) {
        this.vendorType = vendorType;
    }

    public void setVendorId(long vendorId) {
        this.vendorId = vendorId;
    }

    public void setVendor(ServiceVendorBean vendor) {
        this.vendor = vendor;
    }

    public void setVendorHospital(HospitalBean vendorHospital) {
        this.vendorHospital = vendorHospital;
    }

    public void setVendorDepartId(long vendorDepartId) {
        this.vendorDepartId = vendorDepartId;
    }

    public void setVendorHospitalDepart(HospitalDepartmentBean department) {
        this.vendorHospitalDepart = department;
    }

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }

    public void setCategory(ServiceCategoryBean category) {
        this.category = category;
    }

    public void setTopCategoryId(long topCategoryId) {
        this.topCategoryId = topCategoryId;
    }

    public void setTopCategory(ServiceCategoryBean topCategory) {
        this.topCategory = topCategory;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public void setPatientId(long patientId) {
        this.patientId = patientId;
    }

    public void setPatient(PatientBean patient) {
        this.patient = patient;
    }

    public void setAddressId(long addressId) {
        this.addressId = addressId;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setServiceStartTime(Date serviceStartTime) {
        this.serviceStartTime = serviceStartTime;
    }

    public void setServiceTimeDuration(int serviceTimeDuration) {
        this.serviceTimeDuration = serviceTimeDuration;
    }

    public void setServiceTimeUnit(TimeUnit serviceTimeUnit) {
        this.serviceTimeUnit = serviceTimeUnit;
    }

    public void setItemCount(int itemCount) {
        this.itemCount = itemCount;
    }

    public void setTotalConsumptionCent(int totalConsumptionCent) {
        this.totalConsumptionCent = totalConsumptionCent;
        this.totalConsumption = VerifyUtil.parsePrice(totalConsumptionCent);
    }

    public void setPreferentialCent(int preferentialCent) {
        this.preferentialCent = preferentialCent;
        this.preferential = VerifyUtil.parsePrice(preferentialCent);
    }

    public void setTotalServerIncomeCent(int totalServerIncomeCent) {
        this.totalServerIncomeCent = totalServerIncomeCent;
        this.totalServerIncome = VerifyUtil.parsePrice(totalServerIncomeCent);
    }

    public void setNeedVisitPatientRecord(YesNoEnum needVisitPatientRecord) {
        this.needVisitPatientRecord = needVisitPatientRecord;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public void setPayTime(Date payTime) {
        this.payTime = payTime;
    }

    public void setPaymentAmountCent(int paymentAmountCent) {
        this.paymentAmountCent = paymentAmountCent;
        this.paymentAmount = VerifyUtil.parsePrice(paymentAmountCent);
    }

    public void setLeaveAMessage(String leaveAMessage) {
        this.leaveAMessage = leaveAMessage;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public void setCompletedTime(Date completedTime) {
        this.completedTime = completedTime;
    }

    public void setFetchTime(Date fetchTime) {
        this.fetchTime = fetchTime;
    }

    public List<ServiceOrderChargePingPPBean> getPingPP() {
        return pingPP;
    }

    public void setPingPP(List<ServiceOrderChargePingPPBean> pingPP) {
        this.pingPP = pingPP;
    }

    public Object getProperty(String key){
        return this.properties.get(key);
    }

    public void setProperty(String key, Object value){
        this.properties.put(key, value);
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    public YesNoEnum getIsNurseFetched() {
        return isNurseFetched;
    }

    public void setIsNurseFetched(YesNoEnum isNurseFetched) {
        this.isNurseFetched = isNurseFetched;
    }

    @Override
    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(getClass().getName()).append("@").append(hashCode()).append("[");
        msg.append("id=").append(id);
        msg.append(", time=").append(time);
        msg.append(", status=").append(status);
        msg.append(", serviceItemManagedBy=").append(serviceItemManagedBy);
        msg.append(", serviceItem=").append(serviceItem);
        msg.append(", userId=").append(userId);
        msg.append(", patient=").append(patient);
        msg.append(", address=").append(address);
        msg.append(", serviceStartTime=").append(serviceStartTime);
        msg.append(", serviceTimeDuration=").append(serviceTimeDuration);
        msg.append(", serviceTimeUnit=").append(serviceTimeUnit);
        msg.append(", totalConsumption=").append(totalConsumption);
        msg.append(", orderStatus=").append(orderStatus);
        msg.append(", payTime=").append(payTime);
        msg.append(", paymentAmount=").append(paymentAmount);
        msg.append(", score=").append(score);
        msg.append(", completedTime=").append(completedTime);
        msg.append("]");
        return msg.toString();
    }
}
