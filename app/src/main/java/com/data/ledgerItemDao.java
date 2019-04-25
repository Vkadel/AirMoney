package com.data;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface ledgerItemDao {
    @Query("SELECT * FROM ledgeritem_table")
    public abstract android.arch.paging.DataSource.Factory<Integer,ledgeritem> getAll();


    @Query("SELECT * FROM ledgeritem_table WHERE mledgeritemid IN (:id)")
    List<user> loadAllbyID(int[] id);

    @Query("SELECT * FROM ledgeritem_table WHERE mledgeritemid IN (:id)")
    user loadOnebyID(int id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOne(user... users);

    @Delete
    void delete(user user);
}

