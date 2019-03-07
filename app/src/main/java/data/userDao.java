package data;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface userDao {
    @Query("SELECT * FROM user_table")
    public abstract android.arch.paging.DataSource.Factory<Integer, user> getAll();


    @Query("SELECT * FROM user_table WHERE mid IN (:id)")
    List<user> loadAllbyID(int[] id);

    @Query("SELECT * FROM user_table WHERE mid IN (:id)")
    user loadOnebyID(int id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOne(user... users);

    @Delete
    void delete(user user);
}

