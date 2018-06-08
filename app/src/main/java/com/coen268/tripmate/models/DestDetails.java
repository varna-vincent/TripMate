package com.coen268.tripmate.models;

import java.util.Date;

public class DestDetails {
    public DestDetails(String destName, Date date){
        this.destName=destName;
        this.date=date;
    }

    private String destName;

    public String getDestName() {
        return destName;
    }

    public Date getDate() {
        return date;
    }

    private Date date;
}
