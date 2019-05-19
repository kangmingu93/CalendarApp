package com.programers.calendarapp.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class DatabaseOpenHelper extends SQLiteOpenHelper {

    // 데이터베이스 버전
    private static final int DATABASE_VERSION = 1;
    // 데이터베이스 이름
    private static final String DATABASE_NAME = "calendar.db";
    // 테이블 이름
    private static final String TABLE_NAME = "calendar";
    // 컬럼
    private static final String KEY_ID = "id";
    private static final String KEY_TITLE = "title";
    private static final String KEY_CONTEXT = "context";
    private static final String KEY_DATE = "date";

    // 데이터 포맷
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * 데이터베이스 헬퍼 생성자
     * @param context context
     * */
    public DatabaseOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE "
                + TABLE_NAME
                + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + KEY_TITLE + " TEXT NOT NULL, "
                + KEY_CONTEXT + " TEXT, "
                + KEY_DATE + " DATE NOT NULL "
                + ");";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
        db.execSQL(DROP_TABLE);
        onCreate(db);
    }

    /**
     * 일정 추가
     * @param myCalendar 일정 정보 데이터 객체
     */
    public void add(MyCalendar myCalendar) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, myCalendar.getTitle());
        values.put(KEY_CONTEXT, myCalendar.getContext());
        values.put(KEY_DATE, dateFormat.format(myCalendar.getDate()));

        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    /**
     * 일정 가져오기
     * @param start 시작일
     * @param end 종료일
     * @return 시작일부터 종료일까지 해당되는 일정 리스트
     * @throws ParseException
     */
    public ArrayList<MyCalendar> getAll(String start, String end) throws ParseException {
        ArrayList<MyCalendar> myCalendarList = new ArrayList<MyCalendar>();

        String SELECT = "SELECT * FROM "
                + TABLE_NAME
                + " WHERE " + KEY_DATE +" BETWEEN '"
                + start
                + "' AND '"
                + end
                + "' ORDER BY " + KEY_DATE + " ASC, " + KEY_ID + " ASC;";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(SELECT, null);

        while (cursor.moveToNext()) {
            MyCalendar myCalendar = new MyCalendar();
            myCalendar.setId(Integer.parseInt(cursor.getString(0)));
            myCalendar.setTitle(cursor.getString(1));
            myCalendar.setContext(cursor.getString(2));
            Date date = dateFormat.parse(cursor.getString(3));
            myCalendar.setDate(date);

            myCalendarList.add(myCalendar);
        }

        return myCalendarList;
    }

    /**
     * 조건 검색
     * @param id 검색할 레코드의 기본키
     * @return MyCalendar 객체
     * @throws ParseException
     */
    public MyCalendar getCalendar(long id) throws ParseException {
        MyCalendar myCalendar = new MyCalendar();

        String SELECT = "SELECT * FROM "
                + TABLE_NAME
                + " WHERE " + KEY_ID +" = " + id
                + ";";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(SELECT, null);
        cursor.moveToFirst();

        myCalendar.setId(Integer.parseInt(cursor.getString(0)));
        myCalendar.setTitle(cursor.getString(1));
        myCalendar.setContext(cursor.getString(2));
        Date date = dateFormat.parse(cursor.getString(3));
        myCalendar.setDate(date);

        return myCalendar;
    }

    /**
     * 일정 수정
     * @param myCalendar 일정 정보 데이터 객체
     */
    public void update(MyCalendar myCalendar) {
        String UPDATE = "UPDATE "
                + TABLE_NAME
                + " SET " + KEY_TITLE + " = '" + myCalendar.getTitle()
                + "' , " + KEY_DATE + " = '" + dateFormat.format(myCalendar.getDate())
                + "' , " + KEY_CONTEXT + " = '" + myCalendar.getContext()
                + "' WHERE " + KEY_ID + " = " + myCalendar.getId()
                + ";";

        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(UPDATE);

        Log.d("Database.update", "id : " + myCalendar.getId() + " 수정 완료");
    }

    /**
     * 일정 삭제
     * @param id 삭제할 레코드의 기본키
     */
    public void delete(long id) {
        String DELETE = "DELETE FROM "
                + TABLE_NAME
                + " WHERE " + KEY_ID + " = " + id
                + ";";

        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(DELETE);

        Log.d("Database.delete", "id : " + id + " 삭제 완료");
    }
}
