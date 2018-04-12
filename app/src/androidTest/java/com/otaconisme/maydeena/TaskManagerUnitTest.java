package com.otaconisme.maydeena;

import android.arch.persistence.db.SupportSQLiteOpenHelper;
import android.arch.persistence.room.DatabaseConfiguration;
import android.arch.persistence.room.InvalidationTracker;
import android.arch.persistence.room.Room;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.otaconisme.maydeena.db.AppDatabase;
import com.otaconisme.maydeena.dto.Task;
import com.otaconisme.maydeena.enums.TaskProgressEnum;
import com.otaconisme.maydeena.enums.TaskWeightEnum;
import com.otaconisme.maydeena.manager.Impl.TaskManagerImpl;
import com.otaconisme.maydeena.manager.TaskManager;
import com.otaconisme.maydeena.persistence.dao.TaskDao;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static com.otaconisme.maydeena.dto.Task.rootTask;
import static org.junit.Assert.assertEquals;

/**
 * Created by Zakwan on 11/15/
 * Test Case
 * Test basic functionality of TaskManager
 */

@RunWith(AndroidJUnit4.class)
public class TaskManagerUnitTest {
    private final double double_delta = 0.0000001;
    private TaskManager taskManager;




    @Before
    public void initialize() {
        AppDatabase db = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getTargetContext(), AppDatabase.class).build();

        taskManager = TaskManagerImpl.getInstance(db);
    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void createMainTask() {
        String taskTitle = "My Task";
        Task task = taskManager.createTask(taskTitle);
        double progress = 0.0;
        Set<UUID> children = new HashSet<>();

        assertEquals(taskTitle, task.getTitle());
        assertEquals(progress, task.getProgress(), double_delta);
        assertEquals(null, task.getParent());
        assertEquals(TaskWeightEnum.DEFAULT.getValue(), task.getWeight(), double_delta);
        assertEquals(children, task.getChildren());
    }

    @Test
    public void createChildTask() {
        Task parenTask = taskManager.createTask("parent task");
        int number_of_children = 3;
        for (int i = 0; i < number_of_children; i++) {
            Task childTask = taskManager.createTask("child task 0" + i, parenTask);
            assertEquals(parenTask.getId(), childTask.getParent());
            assertEquals(true, parenTask.getChildren().contains(childTask.getId()));
        }
        assertEquals(true, parenTask.getChildren().size() == number_of_children);
    }

    @Test
    public void deleteTask() {
        Task task = taskManager.createTask("toBeDeleted");
        Task checkTask = taskManager.getTask(task.getId());
        assertEquals(true, task.equals(checkTask));
        assertEquals(true, rootTask.getChildren().contains(task.getId()));

        taskManager.deleteTask(task);
        assertEquals(true, taskManager.getTask(task.getId()) == null);
        assertEquals(false, rootTask.getChildren().contains(task.getId()));
    }

    @Test
    public void setProgressDoneUndone() {
        Task parent = taskManager.createTask("parent");
        Task child = taskManager.createTask("child", parent);

        assertEquals(TaskProgressEnum.UNDONE.getValue(), child.getProgress(), double_delta);
        assertEquals(TaskProgressEnum.UNDONE.getValue(), parent.getProgress(), double_delta);

        taskManager.setProgressDone(child);

        assertEquals(TaskProgressEnum.DONE.getValue(), child.getProgress(), double_delta);
        assertEquals(TaskProgressEnum.DONE.getValue(), parent.getProgress(), double_delta);

        taskManager.setProgressUndone(child);

        assertEquals(TaskProgressEnum.UNDONE.getValue(), child.getProgress(), double_delta);
        assertEquals(TaskProgressEnum.UNDONE.getValue(), parent.getProgress(), double_delta);

        Task grandchild01 = taskManager.createTask("grandchild01", child);
        Task grandchild02 = taskManager.createTask("grandchild02", child);

        taskManager.setProgressDone(grandchild01);

        assertEquals(0.5, child.getProgress(), double_delta);
        assertEquals(0.5, parent.getProgress(), double_delta);

        taskManager.setProgressDone(grandchild02);

        assertEquals(TaskProgressEnum.DONE.getValue(), child.getProgress(), double_delta);
        assertEquals(TaskProgressEnum.DONE.getValue(), parent.getProgress(), double_delta);

        taskManager.setProgressUndone(grandchild01);
        taskManager.setProgressUndone(grandchild02);

        assertEquals(TaskProgressEnum.UNDONE.getValue(), child.getProgress(), double_delta);
        assertEquals(TaskProgressEnum.UNDONE.getValue(), parent.getProgress(), double_delta);

        //setting done top parent should change it progress
        taskManager.setProgressDone(child);
        assertEquals(TaskProgressEnum.UNDONE.getValue(), child.getProgress(), double_delta);
    }

    @Test
    public void switchParentTask() {
        Task parent01 = taskManager.createTask("parent01");
        Task parent02 = taskManager.createTask("parent02");
        Task child = taskManager.createTask("child", parent01);
        Task grandChildren01 = taskManager.createTask("grandchildren01", child);
        Task grandChildren02 = taskManager.createTask("grandchildren02", child);
        Task grandChildren03 = taskManager.createTask("grandchildren03", child);
        Task grandChildren04 = taskManager.createTask("grandchildren04", child);

        taskManager.setProgressDone(grandChildren01);
        assertEquals(0.25, child.getProgress(), double_delta);
        assertEquals(0.25, parent01.getProgress(), double_delta);
        assertEquals(TaskProgressEnum.UNDONE.getValue(), parent02.getProgress(), double_delta);

        taskManager.switchParentTask(child, parent02);
        assertEquals(0.25, child.getProgress(), double_delta);
        assertEquals(0.25, parent02.getProgress(), double_delta);
        assertEquals(TaskProgressEnum.UNDONE.getValue(), parent01.getProgress(), double_delta);

        taskManager.switchParentTask(grandChildren04, rootTask);
        assertEquals(null, grandChildren04.getParent());
        assertEquals(true, rootTask.getChildren().contains(grandChildren04.getId()));

    }

    @Test
    public void getRootTask() {
        Task rootTask = taskManager.getRootTask();
        assertEquals(null, rootTask.getId());
        assertEquals(null, rootTask.getParent());
        assertEquals(null, rootTask.getTitle());
        assertEquals(0, rootTask.getWeight(), double_delta);
        assertEquals(TaskProgressEnum.UNDONE.getValue(), (int) rootTask.getProgress(), double_delta);
        if (rootTask.hasChildren()) {
            Set<Task> children = taskManager.getTasks(rootTask.getChildren());
            for (Task child : children) {
                assertEquals(null, child.getParent());
            }
        }
    }
}
