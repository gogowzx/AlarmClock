package com.boll.alarmclock;

import android.app.KeyguardManager;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.boll.alarmclock.broadcast.HomeKeyListener;
import com.boll.alarmclock.service.RingService;
import com.boll.alarmclock.utils.AlarmWakeLock;
import com.boll.alarmclock.utils.TimeUtils;
import com.boll.alarmclock.utils.Util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class WarningActivity extends BaseActivity {
    private static final String TAG = WarningActivity.class.getSimpleName();
    private boolean isStop;
    private long time, constantTime;//constantTime为闹铃的设置时间
    private ArrayList<ClockTime> clockTimes;
    private Button stop;
    private RingService.RingAndVibrate ring;
    private KeyguardManager.KeyguardLock keyguardLock;
    private TextView TVHour, TVMinute, TVTime;
    private HomeKeyListener listener;
    private ArrayList<ClockTime> temp1;
    private ArrayList<ClockTime> temp2;
    private final ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            ring = (RingService.RingAndVibrate) iBinder;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
        }
    };
    private final Handler handler = new Handler(Looper.getMainLooper());


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_warning);

        Intent alarm = getIntent();
        time = alarm.getLongExtra("AlarmTime", 0);

        initView();

        AlarmWakeLock.acquireCpuWakeLock(this);
        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        keyguardLock = keyguardManager.newKeyguardLock("UnlockScreen");
        keyguardLock.disableKeyguard();

        Intent intent = new Intent("action.RING");
        intent.setPackage(getPackageName());
        bindService(intent, connection, BIND_AUTO_CREATE);

        //Alarms.saveRingState(this,true);
        add();

        postTask();
        clockTimes=Alarms.read(this);

        temp1=new ArrayList<>();//和第一个闹铃完全一样
        temp2=new ArrayList<>();//和第一个闹铃不一样，但该轮响铃开始时间一样
        long time1=clockTimes.get(0).getTime();
        temp1.add(clockTimes.get(0));
        for (int i=1;i<clockTimes.size();i++){
            long time2=clockTimes.get(i).getTime();
            long time3=time2-time1;
            Log.e(TAG, "onCreate: ffffffffffff time3:"+time3);
            if(time3<600){//即为相同时间闹铃
                if (clockTimes.get(0).getCount()==clockTimes.get(i).getCount()){
                    temp1.add(clockTimes.get(i));
                }else{
                    SimpleDateFormat format=new SimpleDateFormat("HH:mm");
                    String s=format.format(new Date(time2))+"的闹钟也来了";
                    temp2.add(clockTimes.get(i));
                    Toast.makeText(WarningActivity.this,s, Toast.LENGTH_SHORT).show();
                }
            }else {
                Log.e(TAG, "onCreate:time3: fffffffffffffff两次闹铃中间的时间差（单位ms）："+time3);
                SimpleDateFormat format=new SimpleDateFormat("HH:mm");
                Log.e(TAG, "onCreate: ffffffffff正在响铃的开始时间："+format.format(new Date(time1)));
                Log.e(TAG, "onCreate: ffffffffff马上到的下次响铃时间："+format.format(new Date(time2)));
                if (time3>600&&time3<(180-2)*1000){
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            stopClock();
                        }
                    },time3-3000);
                    return;
                }
            }

        }
    }

    private void stopClock() {
        long time = Calendar.getInstance().getTimeInMillis();
        ArrayList<String> list = Alarms.readDataFromSP(WarningActivity.this);

        //完全相同
        for(int i=0;i<temp1.size();i++){
            int num=temp1.get(i).getCount();
            int id=temp1.get(i).getId();
            Alarm alarm=Alarms.stringDataToAlarm(list.get(id-1),id);
            if (num!=0){//已经响过一次
                num++;
                if (num==5){
                    alarm.setNowTime(0);
                    alarm.setCount(0);
                    if (!alarm.getIsRepeat())
                        alarm.setState(false);
                }else {
                    alarm.setCount(num);
                }
            }else {
                alarm.setCount(1);
                alarm.setNowTime(clockTimes.get(i).getTime());
            }
            list.set(id-1,alarm.toString());
        }

        //和第一个闹铃不一样，但该轮响铃开始时间一样
        for(int i=0;i<temp2.size();i++){
            int num=temp2.get(i).getCount();
            int id=temp2.get(i).getId();
            Alarm alarm=Alarms.stringDataToAlarm(list.get(id-1),id);
            if (num!=0){
                num++;
                if (num==5){
                    alarm.setNowTime(0);
                    alarm.setCount(0);
                    if (!alarm.getIsRepeat())
                        alarm.setState(false);
                }else {
                    alarm.setCount(num);
                }
            }else {
                alarm.setCount(1);
                alarm.setNowTime(clockTimes.get(i).getTime());
            }
            list.set(id-1,alarm.toString());
        }

        //数据保存
        SharedPreferences clockData = getSharedPreferences("ClockData", MODE_PRIVATE);
        SharedPreferences.Editor editor = clockData.edit();
        editor.clear();
        int i;
        editor.apply();
        for (i = 0; i < list.size(); i++) {
            editor.putString(1 + i + "", list.get(i));
        }
        editor.apply();

        Log.e(TAG, "响铃后自然停止修改---->数据的保存"+list);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Alarms.setNextAlert(WarningActivity.this, time);
        }

        if(ring!=null){
            ring.cancelRing();
            ring.cancelVibrate();
        }
        unbindService(connection);
        AlarmWakeLock.releaseCpuLock();
        keyguardLock.reenableKeyguard();
        isStop=true;
        //Alarms.saveRingState(WarningActivity.this,false);
        deleteData();
        finish();
    }


    private void postTask() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(new Date(time + (long)60 * 1000));
                String hour = Util.format(calendar.get(Calendar.HOUR_OF_DAY));
                String minute = Util.format(calendar.get(Calendar.MINUTE));
                TVHour.setText(hour);
                TVMinute.setText(minute);
            }
        },60000);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(new Date(time + (long)2*60 * 1000));
                String hour = Util.format(calendar.get(Calendar.HOUR_OF_DAY));
                String minute = Util.format(calendar.get(Calendar.MINUTE));
                TVHour.setText(hour);
                TVMinute.setText(minute);
            }
        },120000);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                stopClock();
            }
        },(180-2)*1000);
    }

    private void initView() {
        TVHour = findViewById(R.id.ClockHour);
        TVMinute = findViewById(R.id.ClockMin);
        TVTime = findViewById(R.id.layout_3);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(time));
        String hour = Util.format(calendar.get(Calendar.HOUR_OF_DAY));
        String minute = Util.format(calendar.get(Calendar.MINUTE));
        TVHour.setText(hour);
        TVMinute.setText(minute);
        Calendar cl = Calendar.getInstance();
        StringBuilder builder = new StringBuilder();
        builder.append(cl.get(Calendar.MONTH) + 1).append("月").append(cl.get(Calendar.DAY_OF_MONTH))
                .append("日").append(" ").append(TimeUtils.OfWeek(cl.get(Calendar.DAY_OF_WEEK)));
        TVTime.setText(builder.toString());

        stop = findViewById(R.id.stop);
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isStop=true;
                long time = Calendar.getInstance().getTimeInMillis();
                ArrayList<String> list = Alarms.readDataFromSP(WarningActivity.this);

                //完全相同
                for(int i=0;i<temp1.size();i++){
                    int id=temp1.get(i).getId();
                    Alarm alarm=Alarms.stringDataToAlarm(list.get(id-1),id);
                    alarm.setNowTime(0);
                    alarm.setCount(0);
                    if (!alarm.getIsRepeat())
                        alarm.setState(false);
                    list.set(id-1,alarm.toString());
                }

                //和第一个闹铃不一样，但该轮响铃开始时间一样
                for(int i=0;i<temp2.size();i++){
                    int num=temp2.get(i).getCount();
                    int id=temp2.get(i).getId();
                    Alarm alarm=Alarms.stringDataToAlarm(list.get(id-1),id);
                    if (num!=0){
                        num++;
                        if (num==5){
                            alarm.setNowTime(0);
                            alarm.setCount(0);
                            if (!alarm.getIsRepeat())
                                alarm.setState(false);
                        }else {
                            alarm.setCount(num);
                        }
                    }else {
                        alarm.setCount(1);
                        alarm.setNowTime(clockTimes.get(i).getTime());
                    }
                    list.set(id-1,alarm.toString());
                }

                //数据保存
                SharedPreferences clockData = getSharedPreferences("ClockData", MODE_PRIVATE);
                SharedPreferences.Editor editor = clockData.edit();
                editor.clear();
                int i;
                editor.apply();
                for (i = 0; i < list.size(); i++) {
                    editor.putString(1 + i + "", list.get(i));
                }
                editor.apply();
                Log.e(TAG, "响铃后点击停止修改---->数据的保存"+list);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    Alarms.setNextAlert(WarningActivity.this, time);
                }

                if (ring!=null){
                    ring.cancelRing();
                    ring.cancelVibrate();
                }
                unbindService(connection);
                AlarmWakeLock.releaseCpuLock();
                keyguardLock.reenableKeyguard();
                //Alarms.saveRingState(WarningActivity.this,false);
                deleteData();
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
        handler.removeMessages(0);
        if (!isStop)
            stop.performClick();
    }

    private void insertData(int flag){
        ContentValues values=new ContentValues();
        values.put("name","state");
        values.put("state",flag);
        getContentResolver().update(Alarms.uri,values,"state=?",new String[]{"state"});
    }

    private void deleteData(){
        getContentResolver().delete(Alarms.uri,null,null);
    }

    public void add(){
        ContentValues values=new ContentValues();
        values.put("name","state");
        values.put("state",1);
        getContentResolver().insert(Alarms.uri,values);
    }
}