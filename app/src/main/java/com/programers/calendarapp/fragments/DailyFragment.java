package com.programers.calendarapp.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.programers.calendarapp.activity.MainActivity;
import com.programers.calendarapp.R;
import com.programers.calendarapp.activity.ShowActivity;
import com.programers.calendarapp.adapter.DailyListAdapter;
import com.programers.calendarapp.db.DatabaseOpenHelper;
import com.programers.calendarapp.db.MyCalendar;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class DailyFragment extends Fragment implements AdapterView.OnItemClickListener {

    // 날짜 가져오기
    Calendar cal;
    // 제스쳐
    GestureDetector gestureDetector;
    // 툴바 텍스트뷰
    TextView tv_calendar;
    // 일정 리스트뷰
    ListView listView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_daily, container, false);
        setHasOptionsMenu(false);

        // 메인의 날짜 정보 가져오기
        cal = ((MainActivity) getActivity()).calendar;
        tv_calendar = ((MainActivity)getActivity()).findViewById(R.id.tv_calendar);
        listView = (ListView) view.findViewById(R.id.listView);

        // 리스트뷰 아이템 클릭 리스너
        listView.setOnItemClickListener(this);

        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        });

        // 제스쳐 이벤트 -> 드래그 기능
        gestureDetector = new GestureDetector(getActivity(), new GestureDetector.SimpleOnGestureListener() {

            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                final int SWIPE_MIN_DISTANCE = 120;
                final int SWIPE_MAX_OFF_PATH = 250;
                final int SWIPE_THRESHOLD_VELOCITY = 200;
                try {
                    if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
                        return false;
                    if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                        // 내일로 이동
                        cal.add(Calendar.DAY_OF_MONTH, 1);
                        init();
                    } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                        // 어제로 이동
                        cal.add(Calendar.DAY_OF_MONTH, -1);
                        init();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return super.onFling(e1, e2, velocityX, velocityY);
            }
        });

        // 달력 초기화
        init();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("DailyFragment", "onResume()");
        init();
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
    private void getData(Calendar calendar) {
        try {
            DatabaseOpenHelper db = new DatabaseOpenHelper(getContext());
            String day = String.format("%d-%02d-%02d", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH)+1, calendar.get(Calendar.DAY_OF_MONTH));
            ArrayList<MyCalendar> myCalendarList = db.getAll(day, day);

            // 일정이 없을경우 빈 값을 넣음
            if (myCalendarList == null || myCalendarList.size() == 0) {
                MyCalendar myCalendar = new MyCalendar();
                myCalendarList.add(myCalendar);
            }

            DailyListAdapter adapter = new DailyListAdapter(getContext(), myCalendarList);
            listView.setAdapter(adapter);

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    // 화면 구성
    private void init() {
        // 툴바 텍스트 변경
        tv_calendar.setText((cal.get(Calendar.MONTH)+1) + "월 " + cal.get(Calendar.DAY_OF_MONTH) + "일");
        // 일정 정보 가져오기
        getData(cal);
    }

    // 오늘 날짜로 이동
    public void getNow() {
        cal = Calendar.getInstance();
        init();
    }

}
