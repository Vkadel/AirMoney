package com.data;


import android.arch.paging.DataSource;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

@Dao
public interface LedgerIDsListDao {
    @Query("SELECT * FROM ledgers_owned_table")
    public abstract DataSource.Factory<Integer, LedgerIdAtParent> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void addOneItem(LedgerIdAtParent... ledgerIdAtParents);

}
