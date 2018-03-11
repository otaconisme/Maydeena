package com.otaconisme.maydeena.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

import com.otaconisme.maydeena.DataConverter;
import com.otaconisme.maydeena.dto.Task;
import com.otaconisme.maydeena.persistence.dao.TaskDao;

/**
 * Created by Zakwan on 12/19/2017.
 * Main Database class
 */

//TODO find out exportSchmea
@Database(entities = {Task.class}, version = 1, exportSchema = false)
@TypeConverters({DataConverter.class})
public abstract class AppDatabase extends RoomDatabase{

    public abstract TaskDao getTaskDao();

    private static AppDatabase instance;

    public static AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "main-database").build();
        }
        return instance;
    }
}
