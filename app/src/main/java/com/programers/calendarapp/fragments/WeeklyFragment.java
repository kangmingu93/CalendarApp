package com.programers.calendarapp.fragments;

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
import com.programers.calendarapp.decorators.SaturdayDecorator;
import com.programers.calendarapp.decorators.SundayDecorator;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import org.threeten.bp.DayOfWeek;
import org.threeten.bp.LocalDate;

public class WeeklyFragment extends Fragment implements OnDateSelectedListener, OnMonthChangedListener {

    TextView tv_calendar;
    MaterialCalendarView materialCalendarView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weekly, container, false);
        setHasOptionsMenu(false);

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
        materialCalendarView.setWeekDayTextAppearance(R.style.CustomTextAppearanceWeek);
        materialCalendarView.setDateTextAppearance(R.style.CustomTextAppearanceDay);

        return view;
    }

    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {

    }

    @Override
    public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
        tv_calendar.setText(date.getMonth() + "월 " + date.getYear());
    }
}
