package com.otaconisme.maydeena.manager.Impl;

import android.os.AsyncTask;
import android.util.Log;

import com.otaconisme.maydeena.db.AppDatabase;
import com.otaconisme.maydeena.dto.Task;
import com.otaconisme.maydeena.enums.TaskProgressEnum;
import com.otaconisme.maydeena.enums.TaskWeightEnum;
import com.otaconisme.maydeena.manager.TaskManager;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static com.otaconisme.maydeena.dto.Task.rootTask;

/**
 * Created by Zakwan on 11/12/2017.
 * Implementation of TaskManager
 */

public class TaskManagerImpl implements TaskManager {
    private final String TAG = "TaskManager";

    //TODO think of a better way of putting this data
    private final Map<UUID, Task> allTasks = new HashMap<>();

    private static AppDatabase db = null;

    private static TaskManagerImpl instance = null;

    private TaskManagerImpl(AppDatabase db) {
        this.db = db;
    }

    public static TaskManagerImpl getInstance(AppDatabase db) {
        if (instance == null) {
            instance = new TaskManagerImpl(db);
        }
        return instance;
    }

    @Override
    public Task getRootTask() {
        return getTask(null);
    }

    @Override
    public Task getTask(UUID task) {
        return (task != null) ? allTasks.get(task) : rootTask;
    }

    @Override
    public Set<Task> getTasks(Set<UUID> tasks) {
        Set<Task> result = new HashSet<>();
        for (UUID id : tasks) {
            Task task = getTask(id);
            if (task != null) {
                result.add(getTask(id));
            } else {
                Log.e(TAG, "Missing task for UUID " + id);
            }
        }
        return result;
    }

    @Override
    public void switchParentTask(Task task, Task newParent) {
        if (!isRecursiveChildren(task, newParent)) {
            //remove from old parent
            Task oldParent = getTask(task.getParent());
            oldParent.getChildren().remove(task.getId());
            //reset progress if theres no more children
            if (!setProgressUndone(oldParent)) {
                updateAllParentProgress(oldParent);
            }
            //attach to new parent
            newParent.getChildren().add(task.getId());
            task.setParent(newParent.getId());
            updateAllParentProgress(task);
        } else {
            Log.e(TAG, "Failed to switch parent. Target parent is a child of the task");
        }

    }

    @Override
    public void calculateProgress(Task task) {
        calculateProgress(task, false);
    }


    //This will also update child tasks
    private void calculateProgress(Task task, boolean forceAll) {
        double totalWeight = 0.0;
        double totalScore = 0.0;

        if (task.hasChildren()) {
            for (Task child : getTasks(task.getChildren())) {
                if (forceAll) {
                    calculateProgress(child, forceAll);
                }
                totalScore += child.getProgress() * child.getWeight();
                totalWeight += child.getWeight();
            }
        } else {
            if (task.getProgress() < TaskProgressEnum.DONE.getValue()) {
                task.setProgress(TaskProgressEnum.UNDONE.getValue());
            }
            totalScore = task.getProgress() * task.getWeight();
            totalWeight = task.getWeight();
        }

        task.setProgress(totalScore / totalWeight);
    }

    private boolean addChildTask(Task task, Task newChildTask) {
        Set<UUID> children = task.getChildren();
        if (task != null && !children.contains(newChildTask.getId()) && task.getId() != newChildTask.getId()) {
            children.add(newChildTask.getId());
            task.setChildren(children);
            return true;
        }
        return false;
    }

    @Override
    public Task createTask(String title) {
        return createTask(title, null);
    }

    @Override
    public Task createTask(String title, Task parent) {
        Task task = (parent != null) ? new Task(title, parent.getId()) : new Task(title);
        //regenerate id if its already been used
        if (allTasks.get(task.getId()) == null) {
            allTasks.put(task.getId(), task);
            new InsertToDatabaseAsyncTask().execute(task);
        } else {
            createTask(title, parent);
        }

        //update parent
        Task parentTask = getTask(task.getParent());
        if (parentTask != null) {
            if (addChildTask(parentTask, task)) {
                updateAllParentProgress(parentTask);
            } else {
                Log.i(TAG, "Failed to add child[" + task.getId() + "] to task[" + parentTask.getId() + "]");
            }
        } else {
            //root task
            if (!addChildTask(rootTask, task)) {
                Log.i(TAG, "Failed to add child[" + task.getId() + "] to rootTask");
            }
        }
        return task;
    }

    private boolean removeChildTask(Task task, Task childTask) {
        Set<UUID> children = task.getChildren();
        if (task != null && children.contains(childTask.getId()) && childTask.getId() != null) {
            if(children.remove(childTask.getId())){
                task.setChildren(children);
                return true;
            }
        }
        return false;
    }

    @Override
    public void deleteTask(Task task) {
        deleteTask(task, 0);
    }

    private void deleteTask(Task task, int level) {
        if (task.getId() != null) {
            allTasks.remove(task.getId());
            new DeleteToDatabaseAsyncTask().execute(task);
            //This is a recursive process
            if (task.hasChildren()) {
                for (Task child : getTasks(task.getChildren())) {
                    deleteTask(child, level + 1);
                }
            }
            //only run this at the target task level (not the child)
            if (level == 0) {
                Task parentTask = getTask(task.getParent());
                if (parentTask != null) {
                    if (removeChildTask(parentTask, task)) {
                        updateAllParentProgress(getTask(task.getParent()));
                    } else {
                        Log.e(TAG, "Failed to remove child[" + task.getId() + "] to task[" + parentTask.getId() + "]");
                    }
                } else {
                    if (!removeChildTask(rootTask, task)) {
                        Log.e(TAG, "Failed to remove child[" + task.getId() + "] to rootTask");
                    }
                }
            }
        }
    }

    @Override
    public boolean setProgressDone(Task task) {
        boolean result = false;
        if (!task.hasChildren()) {
            if (!task.isProgressDone()) {
                task.setProgress(TaskProgressEnum.DONE.getValue());
                result = true;
            }
        }
        updateAllParentProgress(task);
        return result;
    }

    @Override
    public boolean setProgressUndone(Task task) {
        boolean result = false;
        if (!task.hasChildren()) {
            if (task.isProgressDone()) {
                task.setProgress(TaskProgressEnum.UNDONE.getValue());
                result = true;
            }
        }
        updateAllParentProgress(task);
        return result;
    }

    @Override
    public void setWeight(Task task, int weight) {
        if (weight >= TaskWeightEnum.MIN.getValue() && weight <= TaskWeightEnum.MAX.getValue()) {
            task.setWeight(weight);
            updateAllParentProgress(task);
        }
    }

    @Override
    public void setTitle(Task task, String title) {
        //TODO introduce limit/constraint
        task.setTitle(title);
    }

    @Override
    public void setChildren(Task parent, Set<UUID> children) {
        parent.setChildren(children);
        allTasks.put(parent.getId(), parent);
    }

    private void updateAllParentProgress(Task task) {
        calculateProgress(task);
        if (task.getParent() != null) {
            updateAllParentProgress(getTask(task.getParent()));
        }
    }

    private boolean isRecursiveChildren(Task source, Task target) {
        boolean result = false;

        if (source.getId() == target.getId()) {
            result = true;
        } else {
            if (source.hasChildren()) {
                for (UUID id : source.getChildren()) {
                    result = isRecursiveChildren(getTask(id), target);
                    if (result) break;
                }
            }
        }
        return result;
    }

    public void refreshParentChildRelation() {
        Map<UUID, Set<UUID>> parentChildRelation = new HashMap<>();
        for (Map.Entry<UUID, Task> entry : allTasks.entrySet()) {
            UUID taskId = entry.getKey();
            Task task = entry.getValue();
            //do a checker
            if (taskId != task.getId()) {
                Log.e(TAG, "id mismatch");
            } else {
                UUID parent = task.getParent();
                Set<UUID> children;
                if (parentChildRelation.get(parent) == null) {
                    children = new HashSet<>();
                } else {
                    children = parentChildRelation.get(parent);
                }
                children.add(task.getId());
                parentChildRelation.put(parent, children);
            }
        }

        for (Map.Entry<UUID, Set<UUID>> entry : parentChildRelation.entrySet()) {
            UUID parent = entry.getKey();
            Set<UUID> children = entry.getValue();
            Task task = allTasks.get(parent);
            setChildren(task, children);
        }
        //update progress values
        calculateProgress(getRootTask());
    }

    class InsertToDatabaseAsyncTask extends AsyncTask<Task, Integer, Long> {
        @Override
        protected Long doInBackground(Task ... tasks) {
            db.getTaskDao().insertTask(tasks[0]);
            return null;
        }
    }

    class DeleteToDatabaseAsyncTask extends AsyncTask<Task, Integer, Long> {
        @Override
        protected Long doInBackground(Task ... tasks) {
            db.getTaskDao().deleteTask(tasks[0]);
            return null;
        }
    }
}
