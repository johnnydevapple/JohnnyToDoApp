package com.KixaluDesign.WhatToDoApp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public final class TaskStorageHelper implements Handler.Callback {
    private static final TaskStorageHelper INSTANCE = new TaskStorageHelper();
    public static final String TAG = "TaskStorageHelper";
    private static final int MSG_SAVE_TASK = 10;
    private static  final int MSG_LOAD_TASKS = 20;

    private final Handler bgHandler;
    private final Handler mainHandler;
    private DbHelper dbHelper;

    private TaskStorageHelper() {
        HandlerThread handlerThread = new HandlerThread(TAG);
        handlerThread.start();
        bgHandler = new Handler(handlerThread.getLooper(), this);
        mainHandler = new Handler(Looper.getMainLooper(), this);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)

    public void initStorage(Context context) {dbHelper = new DbHelper(context);}

    public static TaskStorageHelper getInstance() {
        return INSTANCE;
    }

    public void saveTask(Task task) {

        bgHandler.obtainMessage(MSG_SAVE_TASK, task).sendToTarget();
    }

    public void deleteTask(Task task)
    {

        task.setArchived(true);

        saveTask(task);
    }

    public void getTasks(Callback callback) {
      Message message = bgHandler.obtainMessage(MSG_LOAD_TASKS, callback);
        message.sendToTarget();

    }

    public interface Callback {
        void onData(List<Task> tasks);

    }

    @Override
    public boolean handleMessage(Message msg) {

        if (msg.what == MSG_SAVE_TASK) {
            internalSaveTask((Task) msg.obj);
        } else if (msg.what == MSG_LOAD_TASKS){
            internalLoadTasks((Callback) msg.obj);
        }
        return true;
    }

    private void internalLoadTasks(final Callback callback) {
        SQLiteDatabase database = dbHelper.getReadableDatabase();

        final List<Task> tasks = new ArrayList<>();

        Cursor cursor = database.query("task", DbSchema.COLUMNS, null, null, null, null, null);
        while (cursor.moveToNext()){
            Task task = new Task();
            task.setId(cursor.getLong(cursor.getColumnIndex("_id")));
            task.setTitle(cursor.getString(cursor.getColumnIndex("title")));
            task.setDescription(cursor.getString(cursor.getColumnIndex("description")));
            long timestamp = cursor.getLong(cursor.getColumnIndex("started"));
            task.setStarted(new Date(timestamp));
            int completed = cursor.getInt(cursor.getColumnIndex("completed"));
            task.setCompleted(completed == 1);
            int archived = cursor.getInt(cursor.getColumnIndex("archived"));
            task.setArchived(archived == 1);
            tasks.add(task);
        }
        Runnable runnable = new Runnable() {
            public void run() {
                callback.onData(tasks);
            }
        };
        mainHandler.post(runnable);
    }

    private void internalSaveTask(Task task) {

        SQLiteDatabase database = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title", task.getTitle());
        values.put("description", task.getDescription());
        values.put("started", task.getStarted().getTime());
        values.put("completed", task.isCompleted());
        values.put("archived", task.isArchived());




        if (task.getId() == 0) {
            database.insert("task", "", values);
        } else {
            database.update("task", values,"_id = ?", new String[]{String.valueOf(task.getId())});
        }

    }

    private static class DbSchema {
      public static final String CREATE_TASK_TABLE =
              "CREATE TABLE task (" +
                      "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                      "title TEXT," +
                      "description TEXT," +
                      "started INTEGER," +
                      "completed INTEGER," +
                      "archived INTEGER" +
                      ")";

       public static final String[] COLUMNS = {"_id", "title", "description", "started", "completed", "archived"};

    }

    private class DbHelper extends SQLiteOpenHelper {
        private static final String DB_NAME = "tasks.db";
        private static final int DB_VERSION = 1;


        public DbHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DbSchema.CREATE_TASK_TABLE);

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }
}
