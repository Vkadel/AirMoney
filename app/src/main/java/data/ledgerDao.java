package data;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface ledgerDao {
    @Query("SELECT * FROM childledger")
    public abstract android.arch.paging.DataSource.Factory<Integer, childledger> getAll();


    @Query("SELECT * FROM childledger WHERE mledgerid IN (:id)")
    List<user> loadAllbyID(int[] id);

    @Query("SELECT * FROM childledger WHERE mledgerid IN (:id)")
    user loadOnebyID(int id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOne(user... ledgers);

    @Delete
    void delete(user ledger);
}

