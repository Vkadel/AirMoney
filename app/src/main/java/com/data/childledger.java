package com.data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import java.util.Map;

import com.converters.dateConverter;
import com.converters.stringArrayToString;

@Entity(tableName = "ledgers_table")
@TypeConverters({dateConverter.class, stringArrayToString.class})
public class childledger {
    @PrimaryKey
    private int mledgerid;
    public Map<String, String> mparentowners;
    private String mchildid;
    private String mchildname;
    private int mledgetotal;//may exclude from data to post to server and my want to have only at app level
    private String munit;
    private Map<String, String> mSharedKeys;


    public childledger(int id, Map<String, String> parentowners, String childid, String childname, int ledgetotal,String unit,Map<String, String> sharedwith){
        this.mledgerid =id;
        mparentowners=parentowners;
        mchildid=childid;
        mchildname=childname;
        mledgetotal=ledgetotal;
        munit=unit;
        mSharedKeys =sharedwith;
    }
    public childledger(int id, String childid, String childname, int ledgetotal,String unit,Map<String, String> sharedwith){
        this.mledgerid =id;
        mchildid=childid;
        mchildname=childname;
        mledgetotal=ledgetotal;
        munit=unit;
        mSharedKeys =sharedwith;
    }

    public childledger(){}

    public void setMledgerid(int mledgerid) {
        this.mledgerid = mledgerid;
    }

    public void setMchildid(String mchildid) {
        this.mchildid = mchildid;
    }

    public void setMchildname(String mchildname) {
        this.mchildname = mchildname;
    }

    public void setMledgetotal(int mledgetotal) {
        this.mledgetotal = mledgetotal;
    }

    public void setMparentowners(Map<String, String> mparentowners) {
        this.mparentowners = mparentowners;
    }

    public int getMledgerid() {
        return mledgerid;
    }

    public int getMledgetotal() {
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

    public Map<String, String> getmSharedKeys() {
        return mSharedKeys;
    }

    public void setmSharedKeys(Map<String, String> mSharedKeys) {
        this.mSharedKeys = mSharedKeys;
    }

}
