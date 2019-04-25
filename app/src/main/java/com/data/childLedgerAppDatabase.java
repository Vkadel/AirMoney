package com.data;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

import com.converters.dateConverter;
import com.converters.stringArrayToString;

@Database(entities = childledger.class, version = 1, exportSchema = false)
@TypeConverters({dateConverter.class, stringArrayToString.class})
//TODO: need to make sure items dont get deleted when application its offline
public abstract class childLedgerAppDatabase extends RoomDatabase {
    public abstract ledgerDao childledgerDAO();
    private static volatile childLedgerAppDatabase INSTANCE;

    static childLedgerAppDatabase getDatabase(Context context) {
        if (childLedgerAppDatabase.INSTANCE == null) {
            synchronized (childLedgerAppDatabase.class) {
                if (childLedgerAppDatabase.INSTANCE == null) {
                    childLedgerAppDatabase.INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            childLedgerAppDatabase.class, "child_ledger_database").fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return childLedgerAppDatabase.INSTANCE;
    }

}
