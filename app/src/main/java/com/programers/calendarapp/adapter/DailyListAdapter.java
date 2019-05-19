package com.programers.calendarapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.programers.calendarapp.R;
import com.programers.calendarapp.db.MyCalendar;

import java.util.ArrayList;

public class DailyListAdapter extends BaseAdapter {

    Context context;
    ArrayList<MyCalendar> myCalendars;
    LayoutInflater inflater;

    public DailyListAdapter(Context context, ArrayList<MyCalendar> list) {
        this.context = context;
        this.myCalendars = list;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
            convertView = inflater.inflate(R.layout.item_calendar_listview, null);
        }

        ImageView icon = (ImageView) convertView.findViewById(R.id.item_icon);
        TextView title = (TextView) convertView.findViewById(R.id.item_title);
        TextView context = (TextView) convertView.findViewById(R.id.item_context);

        // 제목이 없는 경우 -> 일정 정보가 없는 경우
        if (myCalendars.get(position).getTitle() == null) {
            icon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_dot_not));

        } else {
            icon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_dot));
            title.setText(myCalendars.get(position).getTitle());

            // 내용이 없는 경우
            if (myCalendars.get(position).getContext() == null || myCalendars.get(position).getContext().trim().equals("")) {
                context.setHint("내용이 없습니다.");
            } else {
                context.setText(myCalendars.get(position).getContext());
            }
        }

        return convertView;
    }

}
