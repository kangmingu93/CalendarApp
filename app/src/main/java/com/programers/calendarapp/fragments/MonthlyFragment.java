package com.programers.calendarapp.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.programers.calendarapp.MainActivity;
import com.programers.calendarapp.R;
import com.programers.calendarapp.db.DatabaseOpenHelper;
import com.programers.calendarapp.db.MyCalendar;
import com.programers.calendarapp.decorators.EventDecorator;
import com.programers.calendarapp.decorators.RangeDayDecorator;
import com.programers.calendarapp.decorators.SaturdayDecorator;
import com.programers.calendarapp.decorators.SundayDecorator;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import org.threeten.bp.DayOfWeek;
import org.threeten.bp.LocalDate;
import org.threeten.bp.format.DateTimeFormatter;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class MonthlyFragment extends Fragment implements OnDateSelectedListener, OnMonthChangedListener {

    TextView tv_calendar;
    MaterialCalendarView materialCalendarView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_monthly, container, false);
        setHasOptionsMenu(false);

        Log.d("MonthlyFragment", "onCreateView()");

        tv_calendar = ((MainActivity)getActivity()).findViewById(R.id.tv_calendar);
        CalendarDay date = CalendarDay.from(LocalDate.now());

        tv_calendar.setText(date.getMonth() + "월 " + date.getYear());

        materialCalendarView = (MaterialCalendarView) view.findViewById(R.id.calendarView);
        materialCalendarView.setOnDateChangedListener(this);
        materialCalendarView.setOnMonthChangedListener(this);
        materialCalendarView.setTopbarVisible(false);   // 상단 날짜바 안보이기

        materialCalendarView.setSelectedDate(LocalDate.now());

        materialCalendarView.state().edit()
                .isCacheCalendarPositionEnabled(false)
                .commit();

        materialCalendarView.setDynamicHeightEnabled(false);
        materialCalendarView.setPadding(0, -20, 0, 30);

        return view;
    }

    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {

    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("onStart()", "Start");

        materialCalendarView.state().edit()
                .isCacheCalendarPositionEnabled(false)
                .commit();
    }

    @Override
    public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
        tv_calendar.setText(date.getMonth() + "월 " + date.getYear());

        TimeZone tz = TimeZone.getTimeZone("Asia/Seoul");
        Calendar cal = Calendar.getInstance(tz);
        cal.set(date.getYear(), date.getMonth(), date.getDay());

        String start = String.format("%d-%02d-%02d", cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.getActualMinimum(Calendar.DATE));
        String end = String.format("%d-%02d-%02d", cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.getActualMaximum(Calendar.DATE));

        DatabaseOpenHelper db = new DatabaseOpenHelper(getContext());
        List<MyCalendar> myCalendarList = null;
        HashSet<CalendarDay> days = new HashSet<CalendarDay>();

        try {
            myCalendarList = db.getAll(start, end);
            if (myCalendarList != null) {
                for (MyCalendar myCalendar : myCalendarList) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(myCalendar.getDate());

                    LocalDate temp = LocalDate.of(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH));
                    days.add(CalendarDay.from(temp));
                }
                materialCalendarView.addDecorator(new EventDecorator(Color.RED, days));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    public void moveNow() {
        materialCalendarView.setSelectedDate(LocalDate.now());
        materialCalendarView.state().edit()
                .isCacheCalendarPositionEnabled(false)
                .commit();
    }

    public Date getSelectedDate() {
        CalendarDay day = materialCalendarView.getSelectedDate();
        Calendar calendar = Calendar.getInstance();
        calendar.set(day.getYear(), day.getMonth()-1, day.getDay());

        return calendar.getTime();
    }
}
