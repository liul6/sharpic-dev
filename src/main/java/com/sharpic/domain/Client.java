package com.sharpic.domain;

import java.util.Date;

/**
 * Created by joey on 2016-12-05.
 */
public class Client {
    private String name;
    private String email;
    private String phone;
    private Date updatedDatetime;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getUpdatedDatetime() {
        return updatedDatetime;
    }

    public void setUpdatedDatetime(Date updatedDatetime) {
        this.updatedDatetime = updatedDatetime;
    }
}
