package com.cooltoo.beans;

import javax.validation.constraints.DecimalMax;
import javax.ws.rs.FormParam;
import java.math.BigDecimal;

/**
 * Created by lg380357 on 2016/2/29.
 */
public class OrderBean {

    @FormParam("id")
    private long id;

    @FormParam("name")
    private String name;

    @FormParam("count")
    private int count;

    @FormParam("cash")
    private BigDecimal cash;

    public long getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public int getCount() {
        return count;
    }
    public BigDecimal getCash() {
        return cash;
    }

    public void setId(long id) {
        this.id = id;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setCount(int count) {
        this.count = count;
    }
    public void setCash(BigDecimal cash) {
        this.cash = cash;
    }

    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(this.getClass()).append("@").append(hashCode()).append("[");
        msg.append("id=").append(id).append(" , ");
        msg.append("name=").append(name).append(" , ");
        msg.append("count=").append(count).append(" , ");
        msg.append("cash=").append(cash);
        msg.append("]");
        return msg.toString();
    }
}
