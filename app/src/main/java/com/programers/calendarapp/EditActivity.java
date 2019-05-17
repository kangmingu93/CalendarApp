package com.programers.calendarapp;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.programers.calendarapp.db.DatabaseOpenHelper;
import com.programers.calendarapp.db.MyCalendar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class EditActivity extends AppCompatActivity implements View.OnClickListener {

    // 액션바
    ActionBar actionBar;
    // 제목
    EditText et_title;
    // 완료
    Button bt_save;
    // 취소
    LinearLayout iv_close;
    // 날짜
    TextView tv_date;
    Date currentDate;
    // 메모
    EditText et_context;

    Calendar cal = Calendar.getInstance();
    SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.KOREA);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        // 툴바
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        this.setSupportActionBar(toolbar);

        actionBar = this.getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true); // 커스텀 사용 O
        actionBar.setDisplayShowTitleEnabled(false); // 제목 표시 X
        actionBar.setDisplayShowHomeEnabled(false); // 뒤로가기 버튼 표시 X

        et_title = (EditText) findViewById(R.id.et_title);
        et_context = (EditText) findViewById(R.id.et_context);
        bt_save = (Button) findViewById(R.id.bt_save);
        bt_save.setOnClickListener(this);
        iv_close = (LinearLayout) findViewById(R.id.iv_close);
        iv_close.setOnClickListener(this);
        tv_date = (TextView) findViewById(R.id.tv_date);
        tv_date.setOnClickListener(this);

        Date date = (Date) getIntent().getSerializableExtra("date");
        cal.setTime(date);
        currentDate = cal.getTime();
        tv_date.setText(cal.get(Calendar.YEAR) + " 년 " + (cal.get(Calendar.MONTH) + 1) + " 월 " + cal.get(Calendar.DATE) + " 일 ( " + dayFormat.format(cal.getTime()) + " )");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_save: // 완료 버튼

                if (et_title.getText().toString().trim().equals("") || et_title.getText().toString() == null) {
                    Toast.makeText(getApplicationContext(), "제목을 입력하세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 데이터베이스에 저장
                DatabaseOpenHelper db = new DatabaseOpenHelper(this);

                MyCalendar myCalendar = new MyCalendar();
                myCalendar.setTitle(et_title.getText().toString());
                myCalendar.setContext(et_context.getText().toString());
                myCalendar.setDate(currentDate);

                db.add(myCalendar);

                finish();
                break;

            case R.id.iv_close: // 취소 버튼
                finish();
                break;

            case R.id.tv_date: // 시작일
                DatePickerDialog startDialog = new DatePickerDialog(EditActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int date) {
                        Calendar cal1 = Calendar.getInstance();
                        cal1.set(year, month, date);
                        currentDate = cal1.getTime();

                        Log.d("onDateSet", currentDate.toString());

                        String dayOfWeek = dayFormat.format(currentDate);
                        String msg = String.format("%d 년 %d 월 %d 일 ( %s )", cal1.get(Calendar.YEAR), cal1.get(Calendar.MONTH) + 1, cal1.get(Calendar.DAY_OF_MONTH), dayOfWeek);
                        tv_date.setText(msg);
                    }
                }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE));

//                startDialog.getDatePicker().setMinDate(new Date().getTime());    //입력한 날짜 이전으로 클릭 안되게 옵션
                startDialog.show();
                break;
        }
    }
}
