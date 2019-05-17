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
import java.util.List;

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

    // 추가
    public void add(MyCalendar myCalendar) {
        SQLiteDatabase db = this.getWritableDatabase();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, myCalendar.getTitle());
        values.put(KEY_CONTEXT, myCalendar.getContext());
        values.put(KEY_DATE, dateFormat.format(myCalendar.getDate()));

        Log.d("add", myCalendar.getTitle() + "/" + myCalendar.getContext() + "/" + myCalendar.getDate());

        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    // 검색
    public ArrayList<MyCalendar> getAll(String start, String end) throws ParseException {
        ArrayList<MyCalendar> myCalendarList = new ArrayList<MyCalendar>();

        String SELECT = "SELECT * FROM "
                + TABLE_NAME
                + " WHERE date BETWEEN '"
                + start
                + "' AND '"
                + end
                + "';";

//        String SELECT = "SELECT * FROM "
//                + TABLE_NAME
//                + ";";

        Log.d("getAll", SELECT);

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(SELECT, null);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        while (cursor.moveToNext()) {
            Log.d("getAll", cursor.getString(0) + "/" + cursor.getString(1) + "/" + cursor.getString(2) + "/" + cursor.getString(3));

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
}
