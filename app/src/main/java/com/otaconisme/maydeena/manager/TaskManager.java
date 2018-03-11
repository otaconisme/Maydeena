package com.otaconisme.maydeena.manager;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.otaconisme.maydeena.dto.Task;

import java.util.Set;
import java.util.UUID;

/**
 * Created by Zakwan on 11/12/2017.
 * Only use this to manipulate task
 */

@Dao
public interface TaskManager {

    Task getRootTask();

    Task getTask(UUID task);

    Set<Task> getTasks(Set<UUID> tasks);

    void switchParentTask(Task sourceTask, Task targetParentTask);

    void calculateProgress(Task task);

    Task createTask(String title);

    Task createTask(String title, Task parent);

    void deleteTask(Task task);

    boolean setProgressDone(Task task);

    boolean setProgressUndone(Task task);

    void setWeight(Task task, int weight);

    void setTitle(Task task, String title);

    void setChildren(Task task, Set<UUID> children);

    void refreshParentChildRelation();
}
