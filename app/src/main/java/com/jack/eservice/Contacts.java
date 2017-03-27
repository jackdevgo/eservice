package com.jack.eservice;

/**
 * Created by jason on 2017/3/21.
 */

public class Contacts {
    public static final String REF_CONTACTS = "contacts";
    String addr;
    String name;
    String phone;
    public Contacts(){

    }

    public Contacts(String addr, String name, String phone) {
        this.addr = addr;
        this.name = name;
        this.phone = phone;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
