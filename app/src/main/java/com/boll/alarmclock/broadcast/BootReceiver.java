package com.boll.alarmclock.broadcast;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.boll.alarmclock.Alarms;
import com.boll.alarmclock.MainActivity;

import java.util.Calendar;

public class BootReceiver extends BroadcastReceiver {

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    @Override
    public void onReceive(Context context, Intent intent) {
        /*if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            Log.e("eeee","开机广播");
            Toast.makeText(context,"开机广播",Toast.LENGTH_LONG).show();
        }
        if (intent.getAction().equals("android.intent.action.MEDIA_MOUNTED")){
            Log.e("eeee","SD卡");
            Toast.makeText(context,"SD卡",Toast.LENGTH_LONG).show();
        }
        if (intent.getAction().equals("android.intent.action.MEDIA_UNMOUNTED")){
            Log.e("eeee","有SD卡");
            Toast.makeText(context,"有SD卡",Toast.LENGTH_LONG).show();
        }
        Log.e("eeee","接收到广播:"+intent.getAction());
        Toast.makeText(context,"接收到广播:"+intent.getAction(),Toast.LENGTH_LONG).show();*/
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED"))
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Alarms.setNextAlert(context, Calendar.getInstance().getTimeInMillis());
            }
        //Log.e("eeee","接收到广播+廖："+intent.getAction());
        //Toast.makeText(context,"接收到广播+廖:"+intent.getAction(),Toast.LENGTH_LONG).show();
    }
}