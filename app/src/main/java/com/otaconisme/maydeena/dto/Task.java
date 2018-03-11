package com.otaconisme.maydeena.dto;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.support.annotation.NonNull;

import com.otaconisme.maydeena.DataConverter;
import com.otaconisme.maydeena.enums.TaskWeightEnum;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
public class Task {

    @PrimaryKey
    @NonNull
    private UUID id;
    @TypeConverters({DataConverter.class})
    private UUID parent;
    private int weight;
    private String title;
    private double progress;
    @Ignore
    private Set<UUID> children;

    public static final Task rootTask = new Task();

    public Task() {
        this.children = new HashSet<>();
    }

    public Task(String title) {
        this(title, null);
    }

    public Task(String title, UUID parent) {
        this(UUID.randomUUID(), title, parent, new HashSet<>(), 0.0, TaskWeightEnum.DEFAULT.getValue());
    }

    public Task(@NonNull UUID id, String title, UUID parent, Set<UUID> children, double progress, int weight) {
        this.id = id;
        this.title = title;
        this.parent = parent;
        this.children = children;
        this.progress = progress;
        this.weight = (weight == 0) ? TaskWeightEnum.DEFAULT.getValue() : weight;
    }

    public boolean hasChildren() {
        return (children != null && children.size() > 0);
    }

    public boolean isProgressDone() {
        return (this.progress == 1.0);
    }

    public void setId(@NonNull UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }

    public UUID getParent() {
        return parent;
    }

    public void setParent(UUID parent) {
        this.parent = parent;
    }

    public Set<UUID> getChildren() {
        return children;
    }

    public void setChildren(Set<UUID> children) {
        this.children = children;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getProgress() {
        return progress;
    }

    public void setProgress(double progress) {
        this.progress = progress;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

}
