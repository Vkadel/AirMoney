package data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "user_table")
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
    String mchildren;


    public user(String id,int usertype,String name,String authid,String email,String ledger_id,String children){
        this.mid=id;
        this.musertype=usertype;//it can be child,parent,admin
        this.mauthid=authid;
        this.mname=name;
        this.memail=email;
        this.mledger_id=ledger_id;
        this.mchildren=children;
    }
    public user(){}

    public void setMauthid(String mauthid) {
        this.mauthid = mauthid;
    }

    public void setMchildren(String mchildren) {
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

    public String getMchildren() {
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
