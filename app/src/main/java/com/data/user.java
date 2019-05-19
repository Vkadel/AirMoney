package com.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.converters.dateConverter;
import com.converters.stringArrayToString;

@Entity(tableName = "user_table")
@TypeConverters({dateConverter.class, stringArrayToString.class})
public class user {
    static final public int PARENT=1;
    static final public int CHILD=2;
    static final public int ADMIN=0;

    @PrimaryKey
    String mid;
    @ColumnInfo(name = "musertype")
    int musertype;//0: Admin, 1:Parent, 2:child
    @ColumnInfo(name = "mauthid")
    String mauthid;
    @ColumnInfo(name = "mname")
    String mname;
    @ColumnInfo(name = "memail")
    String memail;
    @ColumnInfo(name = "mledger_id")
    String mledger_id;
    @ColumnInfo(name = "mchildren")
    public Map<String, String> mchildren;
    private Map<String, String> mshared;


    public user(String id,int usertype,String name,String authid,String email,String ledger_id,Map<String, String> children,Map<String, String> shared){
        this.mid=id;
        this.musertype=usertype;//it can be child,parent,admin
        this.mauthid=authid;
        this.mname=name;
        this.memail=email;
        this.mledger_id=ledger_id;
        this.mchildren=children;
        mshared=shared;
    }

    //Constructor for Login screen
    public user(String id,int usertype,String name,String authid,String email,String ledger_id,Map<String, String> children){
        this.mid=id;
        this.musertype=usertype;//it can be child,parent,admin
        this.mauthid=authid;
        this.mname=name;
        this.memail=email;
        this.mledger_id=ledger_id;
        this.mchildren=children;
        mshared=null;
    }
    public user(){}

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("mid", mid);
        result.put("musertype", musertype);
        result.put("mauthid", mauthid);
        result.put("mname", mname);
        result.put("memail", memail);
        result.put("mledger_id", mledger_id);
        result.put("mchildren", mchildren);
        result.put("mShared", mshared);
        return result;
    }

    public Map<String, String> getmShared() {
        return mshared;
    }

    public void setmShared(Map<String, String>mShared) {
        this.mshared = mShared;
    }

    public void setMauthid(String mauthid) {
        this.mauthid = mauthid;
    }

    public void setMchildren(Map<String, String> mchildren) {
        this.mchildren = mchildren;
    }

    public void setMemail(String memail) {
        this.memail = memail;
    }

    public void setMid(String mid) {
        this.mid = mid;
    }

    public String getMid() {
        return mid;
    }

    public int getMusertype() {
        return musertype;
    }

    public String getMauthid() {
        return mauthid;
    }

    public Map<String, String> getMchildren() {
        return mchildren;
    }

    public String getMemail() {
        return memail;
    }

    public String getMledger_id() {
        return mledger_id;
    }

    public String getMname() {
        return mname;
    }

    public void setMledger_id(String mledger_id) {
        this.mledger_id = mledger_id;
    }

    public void setMname(String mname) {
        this.mname = mname;
    }

    public void setMusertype(int musertype) {
        this.musertype = musertype;
    }
}
