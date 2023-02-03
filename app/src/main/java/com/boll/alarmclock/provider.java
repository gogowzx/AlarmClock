package com.boll.alarmclock;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class provider extends ContentProvider {
    private SQLiteOpenHelper openHelper;
    private static final int RING = 1;

    private static final UriMatcher sURLMatcher = new UriMatcher(
            UriMatcher.NO_MATCH);

    static {
        sURLMatcher.addURI("com.boll.alarmclock", "ring", RING);
    }

    @Override
    public boolean onCreate() {
        openHelper=new OpenHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] strings, @Nullable String s, @Nullable String[] strings1, @Nullable String s1) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        switch (sURLMatcher.match(uri)){
            case RING:
                qb.setTables("ring");
                break;
            default:
                throw new IllegalArgumentException("Unknown URL " + uri);
        }
        SQLiteDatabase database=openHelper.getReadableDatabase();
        Cursor ret = qb.query(database,strings, s, strings1, null,
                null, s1);
        return ret;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        int match = sURLMatcher.match(uri);
        switch (match) {
            case RING:
                return "vnd.android.cursor.dir/vnd.com.boll.alarmclock.ring";
            default:
                throw new IllegalArgumentException("Unknown URL");
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        SQLiteDatabase dataBase = openHelper.getWritableDatabase();
        long newId = dataBase.insert("Ring", null, contentValues);
        Uri returnUri = Uri.parse("content://" + "com.boll.alarmclock"+ "/ring/" + newId);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        SQLiteDatabase db = openHelper.getWritableDatabase();
        int count;
        long rowId = 0;
        switch (sURLMatcher.match(uri)) {
            case RING:
                count = db.delete("ring",s, strings);
                break;
            default:
                throw new IllegalArgumentException("Cannot delete from URL: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        int count;
        long rowId = 0;
        int match = sURLMatcher.match(uri);
        SQLiteDatabase db = openHelper.getWritableDatabase();
        if (match == RING) {
            count = db.update("ring", contentValues, s, strings);
        } else {
            throw new UnsupportedOperationException("Cannot update URL: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }
}
