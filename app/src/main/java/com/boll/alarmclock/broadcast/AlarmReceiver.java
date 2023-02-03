package com.boll.alarmclock.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.boll.alarmclock.WarningActivity;

public class AlarmReceiver extends BroadcastReceiver {

    private static final String TAG = AlarmReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        long time = intent.getLongExtra("AlarmTime", 0);
        Log.e(TAG, "onReceive:接到广播");

        //跳转响铃界面
        Intent warningActivity = new Intent(context, WarningActivity.class);
        warningActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        warningActivity.putExtra("AlarmTime", time);
        context.startActivity(warningActivity);
    }
}
