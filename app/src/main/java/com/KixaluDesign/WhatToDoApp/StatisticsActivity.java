package com.KixaluDesign.WhatToDoApp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;

public class StatisticsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);


        ArrayList<Task> tasks = getIntent().getParcelableArrayListExtra(TaskListActivity.KEY_TASKS);

        Date youngest = new Date(0);
        Date oldest = new Date(Long.MAX_VALUE);

        int total = tasks.size();
        int completed = 0;
        int archived = 0;

        for (Task task : tasks) {
            Date started = task.getStarted();
            if (started.after(youngest)){
                youngest = started;
            }
            if (started.before(oldest)){
                oldest = started;
            }
            if (task.isCompleted()){
                completed++;
            }if (task.isArchived()){
                archived++;
            }
            
        }

        TextView tasksTotal = (TextView) findViewById(R.id.tasks_total);
        tasksTotal.setText(String.valueOf(total));
        TextView tasksCompletes = (TextView) findViewById(R.id.tasks_completed);
        tasksCompletes.setText(String.valueOf(completed));
        TextView tasksArchived = (TextView) findViewById(R.id.tasks_archived);
        tasksArchived.setText(String.valueOf(archived));

        double timeDiff = youngest.getTime() - oldest.getTime();
        double days = (timeDiff/(1000 * 60 * 60 * 24)) + 1;

        TextView avgPerDay = (TextView) findViewById(R.id.tasks_per_day);
        avgPerDay.setText(String.valueOf(total / days));
    }

}
