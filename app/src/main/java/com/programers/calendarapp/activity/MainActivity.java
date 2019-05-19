package com.programers.calendarapp.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.programers.calendarapp.R;
import com.programers.calendarapp.fragments.DailyFragment;
import com.programers.calendarapp.fragments.MonthlyFragment;
import com.programers.calendarapp.fragments.WeeklyFragment;

import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener, View.OnClickListener {
    // 날짜
    public static Calendar calendar = Calendar.getInstance(Locale.KOREA); // 기본 오늘
    // 액션바
    ActionBar actionBar;
    // 프래그먼트 관리
    FragmentManager fragmentManager = getSupportFragmentManager();
    // 탭 레이아웃
    TabLayout tabLayout;
    // 플로팅 액션 버튼
    FloatingActionButton fab;

    // 화면 객체
    private MonthlyFragment monthlyFragment = new MonthlyFragment(); // 월간
    private WeeklyFragment weeklyFragment = new WeeklyFragment(); // 주간
    private DailyFragment dailyFragment = new DailyFragment(); // 일간

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.calendar_now:
                // 오늘 날짜로 이동
                calendar = Calendar.getInstance(Locale.KOREA);

                Fragment fragment = fragmentManager.findFragmentById(R.id.frameLayout);
                if (fragment instanceof MonthlyFragment) {
                    ((MonthlyFragment) fragment).getNow();
                } else if (fragment instanceof WeeklyFragment) {
                    ((WeeklyFragment) fragment).getNow();
                } else if (fragment instanceof DailyFragment) {
                    ((DailyFragment) fragment).getNow();
                }

                break;

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 로딩 화면 실행
        Intent loadingIntent = new Intent(this, LoadingActivity.class);
        startActivity(loadingIntent);

        // 툴바
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        this.setSupportActionBar(toolbar);

        // 탭 레이아웃
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.addOnTabSelectedListener(this);

        fab = (FloatingActionButton) findViewById(R.id.addFab);
        fab.setOnClickListener(this);

        // Shared Preference를 불러온다
        SharedPreferences pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);
        int pos = pref.getInt("tab", 0);
        // 저장된 값을 불러와 탭을 이동
        tabLayout.getTabAt(pos).select();
        // 화면 전환
        changeView(pos);

        Log.d("MainActivity.onCreate()", "Tab : " + pos);

        actionBar = this.getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true); // 커스텀 사용 O
        actionBar.setDisplayShowTitleEnabled(false); // 제목 표시 X
        actionBar.setDisplayShowHomeEnabled(false); // 뒤로가기 버튼 표시 X

    }

    @Override
    protected void onStop() {
        super.onStop();

        // 화면이 사라질때 탭 정보를 저장
        SharedPreferences pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        int pos = tabLayout.getSelectedTabPosition();
        editor.putInt("tab", pos);
        editor.commit();

        Log.d("MainActivity.onStop()", "Tab : " + pos);
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        int pos = tab.getPosition();
        // 화면 전환
        changeView(pos);
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    // 화면 전환
    private void changeView(int index) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        switch (index) {
            case 0:
                transaction.replace(R.id.frameLayout, monthlyFragment).commitNowAllowingStateLoss();
                break;

            case 1:
                transaction.replace(R.id.frameLayout, weeklyFragment).commitNowAllowingStateLoss();
                break;

            case 2:
                transaction.replace(R.id.frameLayout, dailyFragment).commitNowAllowingStateLoss();
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addFab:

                // 일정 입력 화면으로 이동
                Intent intent = new Intent(getApplicationContext(), EditActivity.class);
                intent.putExtra("date", calendar.getTime());
                startActivity(intent);

                break;
        }
    }
}
