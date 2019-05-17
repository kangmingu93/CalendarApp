package com.programers.calendarapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.programers.calendarapp.db.MyCalendar;

import java.util.ArrayList;

public class GridAdapter extends BaseAdapter {
    Context context;
    ArrayList<MyCalendar> myCalendars = new ArrayList<MyCalendar>();
    LayoutInflater inf;

    public GridAdapter(Context context, ArrayList<MyCalendar> list) {
        this.context = context;
        this.myCalendars = list;
        inf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return myCalendars.size();
    }

    @Override
    public Object getItem(int position) {
        return myCalendars.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = inf.inflate(R.layout.item_calendar_gridview, null);
        }

        TextView tv = (TextView) convertView.findViewById(R.id.tv_item);
        tv.setText(myCalendars.get(position).getTitle());

        return convertView;
    }
}
