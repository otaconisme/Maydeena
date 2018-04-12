package com.otaconisme.maydeena.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;

import com.otaconisme.maydeena.R;
import com.otaconisme.maydeena.dto.Task;
import com.otaconisme.maydeena.manager.Impl.TaskManagerImpl;
import com.otaconisme.maydeena.manager.TaskManager;

import java.util.List;

/**
 * Created by Zakwan on 3/11/2018.
 * Temporary adapter for list
 */

public class MainListAdapter extends BaseAdapter {
    private List<Task> list;
    private Context context;
    private TaskManager taskManager;

    public MainListAdapter(List<Task> list, Context context) {
        this.list = list;
        this.context = context;
        this.taskManager = TaskManagerImpl.getInstance(context);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //TODO use View Holder (research)
        view = inflater.inflate(R.layout.task_view_layout, null);

        EditText taskTitleText = view.findViewById(R.id.task_title_text);
        taskTitleText.setText(list.get(i).getTitle());

        taskTitleText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if(!taskTitleText.getText().toString().isEmpty()) {
                        taskManager.setTitle(list.get(i), taskTitleText.getText().toString());
                    }
                }
            }

        });

        Button taskDeleteButton = view.findViewById(R.id.task_delete_button);
        if (taskDeleteButton != null) {
            taskDeleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    taskManager.deleteTask(list.get(i));
                    list.remove(i);
                    v.clearFocus();
//                    ((MainActivity) context).notifyDataListChanged();
                    notifyDataSetChanged();
                }
            });
        }

        return view;
    }
}
