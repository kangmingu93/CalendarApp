package com.programers.calendarapp.activity;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
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

import com.programers.calendarapp.R;
import com.programers.calendarapp.db.DatabaseOpenHelper;
import com.programers.calendarapp.db.MyCalendar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class EditActivity extends AppCompatActivity implements View.OnClickListener {

    // 수정 | 입력
    private int KEY;
    // 기본키
    private long KEY_ID;

    // 액션바
    ActionBar actionBar;
    // 제목
    EditText et_title;
    // 메모
    EditText et_context;
    // 저장 버튼
    TextView tv_save;
    // 날짜
    TextView tv_date;
    Date currentDate;
    // 캘린더
    Calendar cal = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        // 툴바
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        this.setSupportActionBar(toolbar);

        // 액션바 설정
        actionBar = this.getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true); // 커스텀 사용 O
        actionBar.setDisplayShowTitleEnabled(false); // 제목 표시 X
        actionBar.setDisplayShowHomeEnabled(false); // 뒤로가기 버튼 표시 X

        et_title = (EditText) findViewById(R.id.et_title);
        et_context = (EditText) findViewById(R.id.et_context);
        tv_date = (TextView) findViewById(R.id.tv_date);
        tv_save = (TextView) findViewById(R.id.tv_save);

        KEY = getIntent().getIntExtra("key", -1);

        // 수정
        if (KEY != -1) {
            KEY_ID = getIntent().getLongExtra("id", -1);
            // 저장 버튼 내용 변경
            tv_save.setText("수정");

            // DB에서 정보 가져오기
            try {
                DatabaseOpenHelper db = new DatabaseOpenHelper(EditActivity.this);
                MyCalendar myCalendar = db.getCalendar(KEY_ID);

                et_title.setText(myCalendar.getTitle());
                et_context.setText(myCalendar.getContext());
                currentDate = myCalendar.getDate();

            } catch (ParseException e) {
                e.printStackTrace();
            }
        // 입력
        } else {
            // 현재 날짜를 전달받음
            Date date = (Date) getIntent().getSerializableExtra("date");
            cal.setTime(date);
            currentDate = cal.getTime();
        }

        // 일정 텍스트뷰 변경
        tv_date.setText(getFormatDateString(currentDate));
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        isCancel();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_save: // 완료 버튼

                // 제목을 입력하지 않을경우 메시지 표시
                if (et_title.getText().toString().trim().equals("") || et_title.getText().toString() == null) {
                    Toast.makeText(getApplicationContext(), "제목을 입력하세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 저장 확인용 다이얼로그 창
                isSave();

                break;

            case R.id.tv_close: // 취소 버튼
                isCancel();
                break;

            case R.id.tv_date: // 저장할 날짜
                DatePickerDialog startDialog = new DatePickerDialog(EditActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int date) {
                        Calendar cal1 = Calendar.getInstance();
                        cal1.set(year, month, date);
                        currentDate = cal1.getTime();

                        Log.d("currentDate", currentDate.toString());

                        // 일정 텍스트뷰 변경
                        tv_date.setText(getFormatDateString(currentDate));

                    }
                }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE));

                startDialog.show();
                break;
        }
    }

    // 저장
    private void save() {
        // 데이터베이스 헬퍼 객체 생성
        DatabaseOpenHelper db = new DatabaseOpenHelper(EditActivity.this);

        // 캘린더 객체 생성 및 데이터 저장
        MyCalendar myCalendar = new MyCalendar();
        myCalendar.setTitle(et_title.getText().toString());
        myCalendar.setContext(et_context.getText().toString());
        myCalendar.setDate(currentDate);

        // 수정
        if (KEY != -1) {
            // 수정할 레코드 기본키
            myCalendar.setId(KEY_ID);

            // 데이터 업데이트
            db.update(myCalendar);
            Toast.makeText(getApplicationContext(), "수정 완료", Toast.LENGTH_SHORT).show();

        // 입력
        } else {
            // 데이터 저장
            db.add(myCalendar);
            Toast.makeText(getApplicationContext(), "입력 완료", Toast.LENGTH_SHORT).show();
        }
    }

    // 취소 확인용 다이얼로그 창
    private void isCancel() {
        // 확인용 다이얼로그 창
        AlertDialog.Builder builder = new AlertDialog.Builder(EditActivity.this);
        builder.setMessage("취소하시겠습니까?");
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.show();
    }

    // 수정 확인용 다이얼로그 창
    private void isSave() {
        AlertDialog.Builder builder = new AlertDialog.Builder(EditActivity.this);
        builder.setMessage(KEY != -1 ? "수정하시겠습니까?" : "저장하시겠습니까?");
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 저장
                save();
                // 종료
                finish();
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
