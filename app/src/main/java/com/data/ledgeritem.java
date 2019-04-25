package com.data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "ledgeritem_table")
public class ledgeritem {
    @PrimaryKey
    int mledgeritemid;
    int mstatus;//0:not approved, 1:approved
    String mapprovedby;//this is the authenticated parent user
    int mvalue;
    String mdescription;
    int mdirection;//0: deduction,1:addition
    String mcreatorid;
    String mchildrenledgerid;
    String mchilid;//for the selected child user.
    String mdate;

    public ledgeritem(int ledgeritemid, int status, String approvedby,
                      int value, String description, int direction, String creatorid, String ledgerid, String chilid, String date) {
        mledgeritemid = ledgeritemid;
        mstatus = status;//0:not approved, 1:approved
        mapprovedby = approvedby;//this is the authenticated parent user
        mvalue = value;
        mdescription = description;
        mdirection = direction;//0: deduction,1:addition
        mcreatorid = creatorid;
        mchildrenledgerid = ledgerid;
        mchilid = chilid;//for the selected child user.
        mdate = date;
    }

    public void setMapprovedby(String mapprovedby) {
        this.mapprovedby = mapprovedby;
    }

    public void setMchilid(String mchilid) {
        this.mchilid = mchilid;
    }

    public void setMcreatorid(String mcreatorid) {
        this.mcreatorid = mcreatorid;
    }

    public void setMdescription(String mdescription) {
        this.mdescription = mdescription;
    }

    public void setMdirection(int mdirection) {
        this.mdirection = mdirection;
    }

    public void setMchildrenledgerid(String mchildrenledgerid) {
        this.mchildrenledgerid = mchildrenledgerid;
    }

    public void setMledgeritemid(int mledgeritemid) {
        this.mledgeritemid = mledgeritemid;
    }

    public void setMstatus(int mstatus) {
        this.mstatus = mstatus;
    }

    public void setMvalue(int mvalue) {
        this.mvalue = mvalue;
    }

    public int getMledgeritemid() {
        return mledgeritemid;
    }

    public int getMdirection() {
        return mdirection;
    }

    public int getMstatus() {
        return mstatus;
    }

    public String getMchildrenledgerid() {
        return mchildrenledgerid;
    }

    public int getMvalue() {
        return mvalue;
    }

    public String getMapprovedby() {
        return mapprovedby;
    }

    public String getMchilid() {
        return mchilid;
    }

    public String getMcreatorid() {
        return mcreatorid;
    }

    public String getMdate() {
        return mdate;
    }

    public String getMdescription() {
        return mdescription;
    }

    public void setMdate(String mdate) {
        this.mdate = mdate;
    }
}
