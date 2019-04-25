package com.data;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

import com.converters.dateConverter;
import com.converters.stringArrayToString;

@Database(entities = ledgeritem.class, version = 1, exportSchema = false)
@TypeConverters({dateConverter.class, stringArrayToString.class})
//TODO: need to make sure items dont get deleted when application its offline
public abstract class ledgerItemAppDatabase extends RoomDatabase {
    public abstract ledgerDao childledgerDAO();
    private static volatile ledgerItemAppDatabase INSTANCE;

    static ledgerItemAppDatabase getDatabase(Context context) {
        if (ledgerItemAppDatabase.INSTANCE == null) {
            synchronized (ledgerItemAppDatabase.class) {
                if (ledgerItemAppDatabase.INSTANCE == null) {
                    ledgerItemAppDatabase.INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            ledgerItemAppDatabase.class, "ledger_item_database").fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return ledgerItemAppDatabase.INSTANCE;
    }

}
