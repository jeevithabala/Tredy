package com.marmeto.user.tredy.util;

import java.util.Date;

public  class MObject implements Comparable<MObject> {

    private Date dateTime;

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date datetime) {
        this.dateTime = datetime;
    }

    @Override
    public int compareTo(MObject o) {
        return getDateTime().compareTo(o.getDateTime());
    }
}
