package com.programers.calendarapp.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.programers.calendarapp.R;
import com.programers.calendarapp.db.DatabaseOpenHelper;
import com.programers.calendarapp.db.MyCalendar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ShowActivity extends AppCompatActivity {
    private static final int KEY_EDIT = 1000;
    // 레코드의 기본키 값
    long KEY_ID;
    // 텍스트뷰
    TextView tv_title, tv_date, tv_context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // 액션바 내용
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.drawable.ic_close));

        // 값 전달 받음
        KEY_ID = getIntent().getLongExtra("id", -1);
        // 화면 구성
        init(KEY_ID);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 화면 구성
        init(KEY_ID);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.show_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int id = item.getItemId();
        switch (id) {
            // 취소 버튼
            case android.R.id.home:
                finish();
                break;

            // 수정 버튼
            case R.id.calendar_edit:
                Intent intent = new Intent(ShowActivity.this, EditActivity.class);
                intent.putExtra("key", KEY_EDIT);
                intent.putExtra("id", KEY_ID);
                startActivity(intent);

                break;

            // 삭제 버튼
            case R.id.calendar_delete:
                // 확인용 다이얼로그 창
                isDelete();

                break;
        }

        return super.onOptionsItemSelected(item);
    }

    // 화면 구성
    private void init(long id) {
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_date = (TextView) findViewById(R.id.tv_date);
        tv_context = (TextView) findViewById(R.id.tv_context);

        try {
            DatabaseOpenHelper db = new DatabaseOpenHelper(this);
            MyCalendar myCalendar = db.getCalendar(id);

            tv_title.setText(myCalendar.getTitle());
            tv_date.setText(getFormatDateString(myCalendar.getDate()));
            tv_context.setText(myCalendar.getContext());

        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    // 삭제 확인 다이얼로그 창
    private void isDelete() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ShowActivity.this);
        builder.setMessage("삭제하시겠습니까?");
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (KEY_ID != -1) {
                    // 레코드 삭제
                    DatabaseOpenHelper db = new DatabaseOpenHelper(ShowActivity.this);
                    db.delete(KEY_ID);

                    Toast.makeText(getApplicationContext(), "삭제 완료", Toast.LENGTH_SHORT).show();
                    // 종료
                    finish();
                }
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.show();
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
