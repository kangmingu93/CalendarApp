package com.programers.calendarapp.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.programers.calendarapp.R;
import com.programers.calendarapp.activity.ShowActivity;
import com.programers.calendarapp.activity.MainActivity;
import com.programers.calendarapp.adapter.WeeklyListAdapter;
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
import java.util.HashSet;
import java.util.Locale;

public class WeeklyFragment extends Fragment implements OnDateSelectedListener, OnMonthChangedListener, AdapterView.OnItemClickListener {

    // 날짜 가져오기
    Calendar cal;
    // 툴바 텍스트뷰
    TextView tv_calendar;
    // 달력 화면
    MaterialCalendarView materialCalendarView;
    // 리스트뷰
    ListView listView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weekly, container, false);
        setHasOptionsMenu(false);

        // 메인의 날짜 정보 가져오기
        cal = ((MainActivity) getActivity()).calendar;
        tv_calendar = ((MainActivity)getActivity()).findViewById(R.id.tv_calendar);
        materialCalendarView = (MaterialCalendarView) view.findViewById(R.id.calendarView);
        listView = (ListView) view.findViewById(R.id.listView);

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
        // 리스트뷰 아이템 클릭 리스너
        listView.setOnItemClickListener(this);

        // 달력 초기화
        init();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("WeeklyFragment", "onResume()");
        init();
    }

    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
        // 선택 날짜로 값 변경
        cal.set(date.getYear(), date.getMonth()-1, date.getDay());
    }

    @Override
    public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
        // 툴바에 넣을 문자열
        StringBuffer sb = new StringBuffer();

        Calendar cal = Calendar.getInstance(Locale.KOREA);
        cal.set(date.getYear(), date.getMonth()-1, date.getDay());
        String start = String.format("%d-%02d-%02d", cal.get(Calendar.YEAR), cal.get(Calendar.MONTH)+1, cal.get(Calendar.DAY_OF_MONTH));
        sb.append((cal.get(Calendar.MONTH)+1) + "월 " + cal.get(Calendar.DAY_OF_MONTH) + "일 ~ ");

        cal.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
        String end = String.format("%d-%02d-%02d", cal.get(Calendar.YEAR), cal.get(Calendar.MONTH)+1, cal.get(Calendar.DAY_OF_MONTH));
        sb.append((cal.get(Calendar.MONTH)+1) + "월 " + cal.get(Calendar.DAY_OF_MONTH) + "일");

        // 툴바 내용 변경
        tv_calendar.setText(sb.toString());

        // 일정 정보 가져오기
        getData(start, end);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        MyCalendar myCalendar = ((MyCalendar)parent.getAdapter().getItem(position));

        // 실제 정보가 있으면 이동
        if (myCalendar.getTitle() != null && !myCalendar.getTitle().trim().equals("")) {
            Intent intent = new Intent(getContext(), ShowActivity.class);
            intent.putExtra("id", myCalendar.getId());
            startActivity(intent);
        }
    }

    // 일정 정보 가져오기
    private void getData(String start, String end) {
        try {
            DatabaseOpenHelper db = new DatabaseOpenHelper(getContext());
            ArrayList<MyCalendar> myCalendarList = db.getAll(start, end);

            // 일정 정보가 있다면
            if (myCalendarList != null && myCalendarList.size() > 0) {
                initDecorators(myCalendarList);
            }

            // 일정이 없을경우 빈 값을 넣음
            if (myCalendarList == null || myCalendarList.size() == 0) {
                MyCalendar myCalendar = new MyCalendar();
                myCalendarList.add(myCalendar);
            }

            // 일정 리스트 출력
            WeeklyListAdapter adapter = new WeeklyListAdapter(getContext(), myCalendarList);
            listView.setAdapter(adapter);

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    // 일정을 달력에 표시
    private void initDecorators(ArrayList<MyCalendar> list) {
        // 전체 표시 제거
        materialCalendarView.removeDecorators();

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
