package data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "ledgers_table")
public class childledger {
    @PrimaryKey
    private int mledgerid;
    private String mparentowners;
    private String mchildid;
    private String mchildname;
    private int mledgetotal;//may exclude from data to post to server and my want to have only at app level

    public childledger(int id, String parentowners, String childid, String childname, int ledgetotal){
        this.mledgerid =id;
        mparentowners=parentowners;
        mchildid=childid;
        mchildname=childname;
        mledgetotal=ledgetotal;
    }

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

    public void setMparentowners(String mparentowners) {
        this.mparentowners = mparentowners;
    }
}
