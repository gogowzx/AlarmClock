package com.boll.alarmclock;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class OpenHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "Ring.db";
    private static final int DATABASE_VERSION = 1;

    public OpenHelper(@Nullable Context context) {
        super(context,DATABASE_NAME,null ,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table ring(id integer primary key autoincrement,name text,state integer)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("drop table if exists ring");
    }
}
