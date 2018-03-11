package com.otaconisme.maydeena;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.otaconisme.maydeena.db.AppDatabase;
import com.otaconisme.maydeena.dto.Task;
import com.otaconisme.maydeena.manager.Impl.TaskManagerImpl;
import com.otaconisme.maydeena.manager.TaskManager;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    TaskManager taskManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void init() {

        AppDatabase db = AppDatabase.getInstance(getApplicationContext());
        taskManager = TaskManagerImpl.getInstance(db);

        Task task = taskManager.createTask("buat kerja baik");
//        db.getTaskDao().insertTask(task);
        class ReadDatabase extends AsyncTask<Task, Integer, List<Task>> {

            @Override
            protected List<Task> doInBackground(Task ... taskss) {
                return db.getTaskDao().getAllTasks();
            }
        }
        try {
            List<Task> tasks = new ReadDatabase().execute().get();
            System.out.println();
        }catch (Exception e){
            System.out.print("Failed to read database");
        }
//        db.getTaskDao().insertTask(taskManager.createTask("test"));
    }
}
