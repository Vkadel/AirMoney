package data;

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
    int mledgerid;
    String mchilid;//for the selected child user.

    public ledgeritem(int ledgeritemid,int status,String approvedby,
                    int value,String description,int direction,String creatorid,int ledgerid,String chilid){
        mledgeritemid=ledgeritemid;
        mstatus=status;//0:not approved, 1:approved
        mapprovedby=approvedby;//this is the authenticated parent user
        mvalue=value;
        mdescription=description;
        mdirection=direction;//0: deduction,1:addition
        mcreatorid=creatorid;
        mledgerid=ledgerid;
        mchilid=chilid;//for the selected child user.
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

    public void setMledgerid(int mledgerid) {
        this.mledgerid = mledgerid;
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
}
