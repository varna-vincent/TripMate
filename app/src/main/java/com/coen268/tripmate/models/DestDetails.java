package com.coen268.tripmate.models;

import java.util.Date;

public class DestDetails {
    public DestDetails(String destName,String destAddr, Date date){
        this.destName=destName;
        this.destAddr=destAddr;
        this.date=date;
    }

    private String destName;

    public String getDestAddr() {
        return destAddr;
    }

    private String destAddr;

    public String getDestName() {
        return destName;
    }

    public Date getDate() {
        return date;
    }

    private Date date;
}
