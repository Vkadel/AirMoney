package com.data;

import androidx.room.Entity;

@Entity(tableName = "ledgers_owned_table")
public class LedgerIdAtParent {
    String mledgerId;
    String misOwned;

    LedgerIdAtParent(String ledgerId, String isOwned){
        mledgerId =ledgerId;
        misOwned=isOwned;
    }

    public String getMisOwned() {
        return misOwned;
    }

    public String getMledgerId() {
        return mledgerId;
    }

    public void setMisOwned(String misOwned) {
        this.misOwned = misOwned;
    }

    public void setMledgerId(String mledgerId) {
        this.mledgerId = mledgerId;
    }

}
