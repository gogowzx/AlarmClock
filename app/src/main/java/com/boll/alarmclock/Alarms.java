package com.boll.alarmclock;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.boll.alarmclock.utils.Util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Alarms {
    private static final int MAX_ITEM_COUNT = 100;
    private static final String TAG = Alarms.class.getSimpleName();

    public static Alarm stringDataToAlarm(String s,int id) {
        String[] array = Util.stringToArray(s);
        Alarm alarm = new Alarm();

        alarm.setId(id);

        String mHour = array[0].split(":")[0];
        alarm.setHour(Integer.parseInt(mHour));

        String mMinute = array[0].split(":")[1];
        alarm.setMinute(Integer.parseInt(mMinute));

        String mState = array[8];
        alarm.setState(Boolean.parseBoolean(mState));

        int[] week = new int[7];
        for (int i = 1; i < 8; i++) {
            week[i - 1] = Boolean.parseBoolean(array[i]) ? 1 : 0;
        }
        int num = 0;
        for (int j : week) {
            if (j > 0)
                num++;
        }
        alarm.setIsRepeat(num > 0);
        alarm.setRepeatDates(week);

        alarm.setNowTime(Long.parseLong(array[9]));
        alarm.setCount(Integer.parseInt(array[10]));

        return alarm;
    }

    //生成闹钟对象
    public static ArrayList<Alarm> stringDataToAlarms(ArrayList<String> list) {
        ArrayList<Alarm> alarms = new ArrayList<>();
        for (int k = 0; k < list.size(); k++) {
            String[] array = Util.stringToArray(list.get(k));
            Alarm alarm = new Alarm();

            alarm.setId(k + 1);

            String mHour = array[0].split(":")[0];
            alarm.setHour(Integer.parseInt(mHour));

            String mMinute = array[0].split(":")[1];
            alarm.setMinute(Integer.parseInt(mMinute));

            String mState = array[8];
            alarm.setState(Boolean.parseBoolean(mState));

            int[] week = new int[7];
            for (int i = 1; i < 8; i++) {
                week[i - 1] = Boolean.parseBoolean(array[i]) ? 1 : 0;
            }
            int num = 0;
            for (int j : week) {
                if (j > 0)
                    num++;
            }
            alarm.setIsRepeat(num > 0);
            alarm.setRepeatDates(week);

            alarm.setNowTime(Long.parseLong(array[9]));
            alarm.setCount(Integer.parseInt(array[10]));

            alarms.add(alarm);
        }
        return alarms;
    }

    //闹钟状态是开启的，填入下次闹钟响起时间
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static ArrayList<ClockTime> calculateNextTime(ArrayList<Alarm> alarms, long nowTime) {
        ArrayList<ClockTime> clockTimes = new ArrayList<>();
        for (int j = 0; j < alarms.size(); j++) {
            Alarm alarm = alarms.get(j);
            if (!alarm.getState())
                continue;
            if (alarm.getCount() != 0) {
                alarm.setNextTime(0);
                //加入该闹铃的第几轮
                clockTimes.add(new ClockTime(alarm.getId(), alarm.getNowTime() + alarm.getCount() * 8L * 60 * 1000, alarm.getCount()));
            } else {
                long nextTime;
                Calendar cal = Calendar.getInstance();
                int dayofweek = cal.get(Calendar.DAY_OF_WEEK);
                cal.set(Calendar.HOUR_OF_DAY, alarm.getHour());
                cal.set(Calendar.MINUTE, alarm.getMinute());
                cal.set(Calendar.SECOND, 0);
                long alarmTime = cal.getTimeInMillis();
                if (MainActivity.DEBUG) {
                    Log.e(TAG, "发生计算的时间: " + nowTime);
                    Log.e(TAG, "发生计算此时的闹钟: " + alarmTime);
                }
                if (alarm.getIsRepeat()) {//重复闹钟
                    //两周的闹钟合成一个数组，方便找下一次闹钟的时间
                    int[] twoWeek = Util.concat(alarm.getRepeatDates(), alarm.getRepeatDates());
                    //开始的下标
                    int startIndex;
                    if (dayofweek == 1) {
                        startIndex = 6;
                    } else {
                        startIndex = dayofweek - 2;
                    }
                    //当天距离下一次闹钟的间隔天数
                    int num = 0;
                    if (alarmTime >= nowTime) {
                        for (int i = startIndex; i < twoWeek.length; i++) {
                            if (twoWeek[i] == 1)
                                break;
                            num++;
                        }
                    } else {
                        for (int i = startIndex + 1; i < twoWeek.length; i++) {
                            num++;
                            if (twoWeek[i] == 1) {
                                break;
                            }
                        }
                    }
                    Log.e(TAG, "重复闹钟返回的num：" + num);//
                    nextTime = alarmTime + 24L * 60 * 60 * 1000 * num;

                } else {//一次性闹钟
                    if (alarmTime >= nowTime) {
                        nextTime = alarmTime;
                    } else {
                        nextTime = alarmTime + 24 * 60 * 60 * 1000;
                    }
                }
                alarm.setNextTime(nextTime);
                if (MainActivity.DEBUG) {
                    SimpleDateFormat format = new SimpleDateFormat("dd日HH点mm");
                    Log.e(TAG, "该闹钟下次闹钟时间为: " + format.format(new Date(nextTime)));
                }
                clockTimes.add(new ClockTime(alarm.getId(), nextTime, 0));
            }
        }
        return clockTimes;
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void setNextAlert(Context context, long time) {
        ArrayList<String> list = Alarms.readDataFromSP(context);
        ArrayList<Alarm> alarms = Alarms.stringDataToAlarms(list);
        ArrayList<ClockTime> clockTimes = Alarms.calculateNextTime(alarms, time);
        sort(clockTimes);

        long nextTime;
        boolean haveClock;
        if (clockTimes.size() == 0) {
            nextTime = Long.MAX_VALUE;
            haveClock=false;
        } else {
            nextTime = clockTimes.get(0).getTime();
            haveClock=true;
        }
        setStatusBarIcon(context, haveClock);
        saveState(context, haveClock);

        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("dd日HH点mm");
        String alarmNext = format.format(nextTime);
        Log.e(TAG, "闹钟马上响铃时间~~~~~~~~: " + alarmNext);

        //设置闹钟
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent("action.ALARM_RECEIVER");
        intent.setPackage(context.getPackageName());
        intent.putExtra("AlarmTime", nextTime);
        @SuppressLint("UnspecifiedImmutableFlag") PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, nextTime, sender);//待机模式下准时响铃
        }else {
            am.setExact(AlarmManager.RTC_WAKEUP, nextTime, sender);
        }

        //存储
        save(context,clockTimes);
    }

    private static void save(Context context,ArrayList<ClockTime> clockTimes) {
        String s;
        StringBuilder builder=new StringBuilder();
        if (clockTimes.size()==0){
            s="";
        }else {
            for (int i=0;i<clockTimes.size();i++){
                if (i==clockTimes.size()-1){
                    builder.append(clockTimes.get(i).toString());
                }else {
                    builder.append(clockTimes.get(i).toString()).append("；");
                }
            }
            s=builder.toString();
        }
        SharedPreferences sp=context.getSharedPreferences("clockTime",0);
        SharedPreferences.Editor editor=sp.edit();
        editor.clear();
        editor.apply();
        editor.putString("clock_time",s);
        editor.apply();

        Log.e(TAG, "setNextAlert: ffffffffffffffff存储"+s);
    }

    public static void setStatusBarIcon(Context context, boolean enabled) {
        Intent alarmChanged = new Intent("android.intent.action.ALARM_CHANGED");
        alarmChanged.putExtra("alarmSet", enabled);
        context.sendBroadcast(alarmChanged);
    }

    public static ArrayList<String> readDataFromSP(Context context) {
        ArrayList<String> data = new ArrayList<>();
        SharedPreferences clockData = context.getSharedPreferences("ClockData", Context.MODE_PRIVATE);
        int i;
        String mData;
        //默认最多100条闹钟数据
        for (i = 1; i <= MAX_ITEM_COUNT; i++) {
            String s = i + "";
            if (null == clockData.getString(s, null)) {

                return data;
            }
            mData = clockData.getString(s, null);
            data.add(mData);
        }
        return data;
    }

    //告诉主界面状态栏是否需要闹铃图标
    public static void saveState(Context context, boolean haveClockIcon) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                File cachedFile = new File(context.getExternalFilesDir(null), "clock.txt");
                Log.e(TAG, "文件路径" + cachedFile.getPath());////////////////
                try {
                    FileOutputStream fileOutputStream = new FileOutputStream(cachedFile);
                    BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
                    bufferedOutputStream.write((haveClockIcon + "").getBytes(StandardCharsets.UTF_8));
                    bufferedOutputStream.close();
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private static void sort(ArrayList<ClockTime> clockTimes) {
        if (clockTimes.size() == 0)
            return;
        for (int i = 0; i < clockTimes.size() - 1; i++) {
            for (int j = 0; j < clockTimes.size() - i - 1; j++) {
                if (clockTimes.get(j).getTime() > clockTimes.get(j + 1).getTime()) {
                    ClockTime temple = clockTimes.get(j);
                    clockTimes.set(j, clockTimes.get(j + 1));
                    clockTimes.set(j + 1, temple);
                }
            }
        }
    }

    public static ArrayList<ClockTime> read(Context context){
        ArrayList<ClockTime> clockTimes=new ArrayList<>();
        SharedPreferences sp= context.getSharedPreferences("clockTime",0);
        String s=sp.getString("clock_time","");
        if (s.length() != 0) {
            String[] clock = s.split("；");
            for (int i = 0; i < clock.length; i++) {
                String[] thisClock=clock[i].split(",");
                ClockTime clockTime = new ClockTime(Integer.parseInt(thisClock[0]), Long.parseLong(thisClock[1]), Integer.parseInt(thisClock[2]));
                clockTimes.add(clockTime);
            }
        }
        Log.e(TAG, "read: ffffffffff取出"+clockTimes);
        return clockTimes;
    }

    //
    public static void saveRingState(Context context, boolean haveRing) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                File cachedFile = new File("/storage/emulated/0/ring.txt");
                try {
                    FileOutputStream fileOutputStream = new FileOutputStream(cachedFile);
                    BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
                    bufferedOutputStream.write((haveRing + "").getBytes(StandardCharsets.UTF_8));
                    bufferedOutputStream.close();
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static Uri uri=Uri.parse("content://com.boll.alarmclock/ring");
}



