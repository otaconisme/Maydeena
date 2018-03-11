package com.otaconisme.maydeena.persistence.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.otaconisme.maydeena.dto.Task;

import java.util.List;

/**
 * Created by Zakwan on 12/19/2017.
 * Dao for Task
 */

@Dao
public interface TaskDao {
    @Insert
    void insertTask(Task task);

    @Delete
    void deleteTask(Task task);

    @Query("SELECT * FROM task")
    List<Task> getAllTasks();

}
