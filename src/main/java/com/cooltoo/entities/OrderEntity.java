package com.cooltoo.entities;

import javax.persistence.*;
import javax.ws.rs.FormParam;
import java.math.BigDecimal;

/**
 * Created by lg380357 on 2016/2/29.
 */
@Entity
@Table(name = "order_list")
public class OrderEntity {

    private long id;
    private String name;
    private int count;
    private BigDecimal cash;

    @Id
    @GeneratedValue
    @Column(name = "id")
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    @Column(name = "name")
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "count")
    public int getCount() {
        return count;
    }
    public void setCount(int count) {
        this.count = count;
    }

    @Column(name = "cash")
    public BigDecimal getCash() {
        return cash;
    }
    public void setCash(BigDecimal cash) {
        this.cash = cash;
    }

    @Override
    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append(this.getClass()).append("@").append(hashCode()).append("[");
        msg.append("id=").append(id).append(" ,");
        msg.append("name=").append(name).append(" ,");
        msg.append("count=").append(count).append(" ,");
        msg.append("cash=").append(cash);
        msg.append(" ]");
        return msg.toString();
    }
}
