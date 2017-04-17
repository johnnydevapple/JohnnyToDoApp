package com.KixaluDesign.WhatToDoApp;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class Task implements Parcelable {

    private long id;
    private String title;
    private String description;
    private Date started = new Date();
    private boolean completed;
    private boolean archived;



    public Task() {
    }

    public Task(long id, String title, String description, Date started, boolean completed, boolean archived) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.started = started;
        this.completed = completed;
        this.archived = archived;
    }

    protected Task(Parcel in) {
        id = in.readLong();
        title = in.readString();
        description = in.readString();
        completed = in.readByte() != 0;
        archived = in.readByte() != 0;
        long timestamp = in.readLong();
        if (timestamp != -1){
            started = new Date(timestamp);
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeByte((byte) (completed ? 1 : 0));
        dest.writeByte((byte) (archived ? 1 : 0));

        if (started != null) {
            dest.writeLong(started.getTime());
        }else {
            dest.writeLong(-1);
        }
    }

    public static final Creator<Task> CREATOR = new Creator<Task>() {
        @Override
        public Task createFromParcel(Parcel in) {
            return new Task(in);
        }

        @Override
        public Task[] newArray(int size) {
            return new Task[size];
        }
    };

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getStarted() {
        return started;
    }

    public void setStarted(Date started) {
        this.started = started;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public boolean isArchived() {
        return archived;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Task task = (Task) o;

        if (id != task.id) return false;
        if (completed != task.completed) return false;
        if (archived != task.archived) return false;
        if (title != null ? !title.equals(task.title) : task.title != null) return false;
        if (description != null ? !description.equals(task.description) : task.description != null)
            return false;
        return started != null ? started.equals(task.started) : task.started == null;

    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (started != null ? started.hashCode() : 0);
        result = 31 * result + (completed ? 1 : 0);
        result = 31 * result + (archived ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", started=" + started +
                ", completed=" + completed +
                ", archived=" + archived +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }


}
