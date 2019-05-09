package com.data;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

import com.converters.dateConverter;
import com.converters.stringArrayToString;

@Database(entities = LedgerIdAtParent.class, version = 1, exportSchema = false)
@TypeConverters({dateConverter.class, stringArrayToString.class})

public abstract class LedgerIDsListAppDatabase extends RoomDatabase {
    public abstract LedgerIDsListDao ledgerIDsListDao();
    private static volatile LedgerIDsListAppDatabase INSTANCE;

    static LedgerIDsListAppDatabase getDatabase(Context context) {
        if (LedgerIDsListAppDatabase.INSTANCE == null) {
            synchronized (LedgerIDsListAppDatabase.class) {
                if (LedgerIDsListAppDatabase.INSTANCE == null) {
                    LedgerIDsListAppDatabase.INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            LedgerIDsListAppDatabase.class, "LedgerIDsList_database").fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return LedgerIDsListAppDatabase.INSTANCE;
    }

}
