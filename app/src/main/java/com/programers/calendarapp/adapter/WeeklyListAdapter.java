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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class WeeklyListAdapter extends BaseAdapter {

    Context context;
    ArrayList<MyCalendar> myCalendars;
    LayoutInflater inflater;

    public WeeklyListAdapter(Context context, ArrayList<MyCalendar> list) {
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
            context.setText(getFormatDateString(myCalendars.get(position).getDate()));
        }

        return convertView;
    }

    // 날짜 포맷 변경
    private String getFormatDateString(Date date) {
        // 요일 정보를 가져오기 위한 포맷
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.KOREA);

        Calendar calendar = Calendar.getInstance(Locale.KOREA);
        calendar.setTime(date);
        String dayOfWeek = dayFormat.format(calendar.getTime());
        String msg = String.format("%d 년 %02d 월 %02d 일 ( %s )", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH), dayOfWeek);

        return msg;
    }

}
