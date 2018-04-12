package com.otaconisme.maydeena;

import android.arch.persistence.room.Room;
import android.support.test.InstrumentationRegistry;

import com.otaconisme.maydeena.db.AppDatabase;
import com.otaconisme.maydeena.dto.Task;
import com.otaconisme.maydeena.manager.Impl.TaskManagerImpl;
import com.otaconisme.maydeena.manager.TaskManager;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * Created by Zakwan on 12/12/2017.
 * Test Case
 * To test basic taskManager singleton
 */

public class SingletonTest {

    private AppDatabase db;

    @Before
    public void initialize() {
        db = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getTargetContext(), AppDatabase.class).build();
    }

    @Test
    public void taskManagerSingleton() {
        TaskManager taskManager_01 = TaskManagerImpl.getInstance(db);
        TaskManager taskManager_02 = TaskManagerImpl.getInstance(db);

        Task task_01 = taskManager_01.createTask("task_01");
        Task task_02 = taskManager_02.getTask(task_01.getId());

        assertEquals(task_01, task_02);
        assertEquals(taskManager_01, taskManager_02);

    }
}
