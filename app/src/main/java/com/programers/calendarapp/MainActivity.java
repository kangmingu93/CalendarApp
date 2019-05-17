package com.programers.calendarapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.programers.calendarapp.fragments.DailyFragment;
import com.programers.calendarapp.fragments.MonthlyFragment;
import com.programers.calendarapp.fragments.WeeklyFragment;
public class MainActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener, View.OnClickListener {

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 툴바
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        this.setSupportActionBar(toolbar);

        actionBar = this.getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true); // 커스텀 사용 O
        actionBar.setDisplayShowTitleEnabled(false); // 제목 표시 X
        actionBar.setDisplayShowHomeEnabled(false); // 뒤로가기 버튼 표시 X

        // 탭 레이아웃
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.addOnTabSelectedListener(this);

        fab = (FloatingActionButton) findViewById(R.id.addFab);
        fab.setOnClickListener(this);

        // 기본으로 월간
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frameLayout, monthlyFragment).commitNowAllowingStateLoss();
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        int pos = tab.getPosition();
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
                Intent intent = new Intent(getApplicationContext(), EditActivity.class);
                startActivity(intent);
                break;
        }
    }
}
