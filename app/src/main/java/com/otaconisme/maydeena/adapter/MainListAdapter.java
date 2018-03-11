package com.otaconisme.maydeena.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.otaconisme.maydeena.R;
import com.otaconisme.maydeena.dto.Task;

import java.util.List;

/**
 * Created by Zakwan on 3/11/2018.
 * Temporary adapter for list
 */

public class MainListAdapter extends BaseAdapter {
    private List<Task> list;
    private Context context;
    public MainListAdapter(List<Task> list, Context context){
        this.list = list;
        this.context = context;
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
        TextView text = view.findViewById(R.id.textView);
        text.setText(list.get(i).getTitle());
        return view;
    }
}
