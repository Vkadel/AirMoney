package com.data;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

import com.converters.dateConverter;
import com.converters.stringArrayToString;

@Database(entities = user.class, version = 1, exportSchema = false)
@TypeConverters({dateConverter.class, stringArrayToString.class})

public abstract class UserAppDatabase extends RoomDatabase {
    public abstract userDao userDao();
    private static volatile UserAppDatabase INSTANCE;

    static UserAppDatabase getDatabase(Context context) {
        if (UserAppDatabase.INSTANCE == null) {
            synchronized (UserAppDatabase.class) {
                if (UserAppDatabase.INSTANCE == null) {
                    UserAppDatabase.INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            UserAppDatabase.class, "user_database").fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return UserAppDatabase.INSTANCE;
    }

}
