package com.KixaluDesign.WhatToDoApp;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class TaskListActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final String KEY_TASKS = "tasks";
    private TasksAdapter tasksAdapter;
    private int selectedId = R.id.action_filter_all_tasks;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        TaskStorageHelper storageHelper = TaskStorageHelper.getInstance();

        if (savedInstanceState == null) {
            storageHelper.initStorage(this);
            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(view.getContext(), TaskDetailActivity.class);
                    startActivity(intent);
                }
            });
        }

        RecyclerView tasksList = (RecyclerView) findViewById(R.id.task_list);
        tasksAdapter = new TasksAdapter();
        tasksList.setAdapter(tasksAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        showFilteredTasks(selectedId);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.filters, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        showFilteredTasks(id);

        return true;
    }

    private void showFilteredTasks(int id) {
        selectedId = id;
        TaskStorageHelper.getInstance().getTasks(new TaskStorageHelper.Callback() {
            @Override
            public void onData(List<Task> tasks) {
                ArrayList<Task> filtered = new ArrayList<>();
                for (Task task : tasks) {

                    if (selectedId == R.id.action_filter_all_tasks) {

                        filtered.add(task);

                    } else if (selectedId == R.id.action_filter_ongoing_tasks) {
                        if (!task.isCompleted() && !task.isArchived()) {
                            filtered.add(task);

                        }

                    }else if (selectedId == R.id.action_filter_finished_tasks) {
                        if (task.isCompleted()) {
                            filtered.add(task);
                        }

                    }else if (selectedId == R.id.action_filter_archived_tasks) {
                        if (task.isArchived()) {
                            filtered.add(task);
                        }
                    }

                }
                tasksAdapter.setTasks(filtered);
            }
        });

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_statistics) {
            TaskStorageHelper.getInstance().getTasks(new TaskStorageHelper.Callback() {
                @Override
                public void onData(List<Task> tasks) {
                    Intent statisticsIntent = new Intent(TaskListActivity.this, StatisticsActivity.class);

                    statisticsIntent.putParcelableArrayListExtra(KEY_TASKS, new ArrayList<>(tasks));
                    startActivity(statisticsIntent);
                }
            });



        } else if (id == R.id.nav_info) {
            Intent infoIntent = new Intent(this, InfoActivity.class);
            startActivity(infoIntent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private class TasksAdapter extends RecyclerView.Adapter<TaskListActivity.TasksAdapter.TaskViewHolder> {
        private List<Task> tasks = new ArrayList<>();

        public void setTasks(List<Task> tasks) {
            this.tasks = tasks;
            notifyDataSetChanged();
        }

        @Override
        public TaskListActivity.TasksAdapter.TaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View itemView = inflater.inflate(R.layout.task_item, parent, false);
            return new TaskListActivity.TasksAdapter.TaskViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(TaskListActivity.TasksAdapter.TaskViewHolder holder, int position) {
            Task task = tasks.get(position);
            holder.title.setText(task.getTitle());
            holder.description.setText(task.getDescription());
            holder.completed.setChecked(task.isCompleted());
        }

        @Override
        public int getItemCount() {
            return tasks.size();
        }

        class TaskViewHolder extends RecyclerView.ViewHolder {
            final TextView title;
            final TextView description;
            final CheckBox completed;

            TaskViewHolder(View itemView) {
                super(itemView);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = getAdapterPosition();
                        Task task = tasks.get(position);
                        Intent intent = new Intent(v.getContext(), TaskDetailActivity.class);
                        intent.putExtra(TaskDetailActivity.KEY_TASK, task);
                        startActivity(intent);
                    }
                });

                title = (TextView) itemView.findViewById(R.id.title);
                description = (TextView) itemView.findViewById(R.id.description);
                completed = (CheckBox) itemView.findViewById(R.id.completed);

                completed.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
                        int position = getAdapterPosition();
                        final Task task = tasks.get(position);

                        if (task.isCompleted() != isChecked) {
                            Runnable runnable = new Runnable() {
                                public void run() {
                                    task.setCompleted(isChecked);
                                    TaskStorageHelper.getInstance().saveTask(task);
                                    showFilteredTasks(selectedId);
                                }
                            };
                            buttonView.post(runnable);
                        }
                    }
                });
            }
        }
    }

}
