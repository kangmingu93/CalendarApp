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

import com.programers.calendarapp.activity.MainActivity;
import com.programers.calendarapp.R;
import com.programers.calendarapp.db.DatabaseOpenHelper;
import com.programers.calendarapp.db.MyCalendar;
import com.programers.calendarapp.decorators.EventDecorator;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import org.threeten.bp.LocalDate;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class MonthlyFragment extends Fragment implements OnDateSelectedListener, OnMonthChangedListener {

    // 날짜 가져오기
    Calendar cal;
    // 툴바 텍스트뷰
    TextView tv_calendar;
    // 달력 화면
    MaterialCalendarView materialCalendarView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_monthly, container, false);
        setHasOptionsMenu(false);

        // 메인의 날짜 정보 가져오기
        cal = ((MainActivity) getActivity()).calendar;
        tv_calendar = ((MainActivity) getActivity()).findViewById(R.id.tv_calendar);
        materialCalendarView = (MaterialCalendarView) view.findViewById(R.id.calendarView);

        // 날짜 라벨 설정
        String[] weekLabels = {"월", "화", "수", "목", "금", "토", "일"};
        materialCalendarView.setWeekDayLabels(weekLabels);
        // 날짜 선택 이벤트
        materialCalendarView.setOnDateChangedListener(this);
        // 달력 이동 이벤트
        materialCalendarView.setOnMonthChangedListener(this);
        // 상단 날짜바 안보이기
        materialCalendarView.setTopbarVisible(false);
        // 자동 높이 조절
        materialCalendarView.setDynamicHeightEnabled(false);
        // 안쪽 여백
        materialCalendarView.setPadding(0, -20, 0, 30);

        // 달력 초기화
        init();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("MonthlyFragment", "onResume()");
        init();
    }

    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
        // 선택 날짜로 값 변경
        cal.set(date.getYear(), date.getMonth()-1, date.getDay());
    }

    @Override
    public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
        tv_calendar.setText(date.getYear() + "년 " + date.getMonth() + "월");

        Calendar cal = Calendar.getInstance(Locale.KOREA);
        cal.set(date.getYear(), date.getMonth(), date.getDay());

        String start = String.format("%d-%02d-%02d", cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.getActualMinimum(Calendar.DATE));
        String end = String.format("%d-%02d-%02d", cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.getActualMaximum(Calendar.DATE));

        getData(start, end);
    }

    // 일정 정보 가져오기
    private void getData(String start, String end) {
        try {
            DatabaseOpenHelper db = new DatabaseOpenHelper(getContext());
            ArrayList<MyCalendar> myCalendarList = db.getAll(start, end);

            // 전체 표시 제거
            materialCalendarView.removeDecorators();

            // 일정 정보가 있다면 표시
            if (myCalendarList != null && myCalendarList.size() > 0) {
                initDecorators(myCalendarList);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    // 일정을 달력에 표시
    private void initDecorators(ArrayList<MyCalendar> list) {
        // 데이터베이스에서 가져온 리스트가 비어있지 않다면
        HashSet<CalendarDay> days = new HashSet<CalendarDay>();

        for (MyCalendar myCalendar : list) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(myCalendar.getDate());

            LocalDate temp = LocalDate.of(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH));
            days.add(CalendarDay.from(temp));
        }
        // 일정이 있으면 빨간 점 표시
        materialCalendarView.addDecorator(new EventDecorator(Color.RED, days));
    }

    // 달력 초기화
    private void init() {
        // 툴바 텍스트 변경
        tv_calendar.setText((cal.get(Calendar.MONTH)+1) + "월 " + cal.get(Calendar.YEAR));

        materialCalendarView.setSelectedDate(LocalDate.of(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH)+1, cal.get(Calendar.DAY_OF_MONTH)));
        materialCalendarView.state().edit()
                .isCacheCalendarPositionEnabled(false)
                .commit();
    }

    // 오늘 날짜로 이동
    public void getNow() {
        cal = Calendar.getInstance();
        init();
    }

}
