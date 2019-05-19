package com.data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.support.annotation.Nullable;

import java.util.Map;

import com.converters.dateConverter;
import com.converters.stringArrayToString;

@Entity(tableName = "ledgers_table")
@TypeConverters({dateConverter.class, stringArrayToString.class})
public class childledger {
    @PrimaryKey
    private String mledgerid;
    public Map<String, String> mparentowners;
    private String mchildid;
    private String mchildname;
    private String munit; //1:Money  2:Time  3:Other
    private Map<String, String> mshared;
    private double mledgetotal;//may exclude from data to post to server and my want to have only at app level
    //if Time this will be the total of minutes. To later be converted to hours.


    public childledger(String id, Map<String, String> parentowners, String childid,
                       String childname, double ledgetotal, String unit,
                       Map<String, String> sharedwith ){
        this.mledgerid =id;
        mparentowners=parentowners;
        mchildid=childid;
        mchildname=childname;
        mledgetotal=ledgetotal;
        munit=unit;
        mshared =sharedwith;

    }
    public childledger(String id, String childid, String childname, double ledgetotal,String unit,Map<String, String> sharedwith){
        this.mledgerid =id;
        mchildid=childid;
        mchildname=childname;
        mledgetotal=ledgetotal;
        munit=unit;
        mshared =sharedwith;
    }


    @Override
    public boolean equals(@Nullable Object obj) {
        return super.equals(obj);
    }

    public childledger(){}

    public void setMledgerid(String mledgerid) {
        this.mledgerid = mledgerid;
    }

    public void setMchildid(String mchildid) {
        this.mchildid = mchildid;
    }

    public void setMchildname(String mchildname) {
        this.mchildname = mchildname;
    }

    public void setMledgetotal(double mledgetotal) {
        this.mledgetotal = mledgetotal;
    }

    public void setMparentowners(Map<String, String> mparentowners) {
        this.mparentowners = mparentowners;
    }

    public String getMledgerid() {
        return mledgerid;
    }

    public double getMledgetotal() {
        return mledgetotal;
    }

    public String getMchildid() {
        return mchildid;
    }

    public String getMchildname() {
        return mchildname;
    }

    public Map<String, String> getMparentowners() {
        return mparentowners;
    }

    public String getMunit() {
        return munit;
    }

    public void setMunit(String munit) {
        this.munit = munit;
    }

    public Map<String, String> getMshared() {
        return mshared;
    }

    public void setMshared(Map<String, String> mshared) {
        this.mshared = mshared;
    }

}
