package com.boll.alarmclock;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.boll.alarmclock.utils.Util;

import java.io.File;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

public class MainActivity extends BaseActivity implements View.OnClickListener, ClockAdapter.SwitchChangeListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    public static boolean DEBUG = true;  //测试
    private ImageView toolBarBack;
    private ListView listView;
    private LinearLayout clockPicture;
    private Button createClock;
    private SharedPreferences clockData;
    private ArrayList<String> data;
    private ClockAdapter adapter;
    private int position;
    private final int REQUEST_CODE = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        position = -1;//记录点击的item的位置，默认为-1
        findView();
        readData();
        setClockAdapter();
        createClock.setOnClickListener(this);
        /*oolBarBack.setOnClickListener(this);*/

        /*Alarms.setNextAlert(this, Calendar.getInstance().getTimeInMillis());
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);//结束5次轮询闹铃
        Intent intent = new Intent(MainActivity.this, WarningActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.setPackage(getPackageName());
        @SuppressLint("UnspecifiedImmutableFlag") PendingIntent sender = PendingIntent.getActivity(MainActivity.this, 0, intent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        am.cancel(sender);

        setAlarmIcon();*/
    }

    private void setAlarmIcon() {
        boolean haveClockIcon = false;
        /*ArrayList<String> data;
        data=Alarms.readDataFromSP(this);*/
        for (int i = 0; i < data.size(); i++) {
            String s=data.get(i);
            String[] array=Util.stringToArray(s);
            if ("true".equals(array[8]))
                haveClockIcon = true;
        }
        Log.e(TAG, "状态栏系统UI是否有闹钟图标: "+haveClockIcon);
        Alarms.setStatusBarIcon(this, haveClockIcon);
        Alarms.saveState(this,haveClockIcon);
    }

    private void setClockAdapter() {
       /* if (data.size() == 0) {
            data.add("19:20，true，true，true，true，true，true，false，true，nowTime，count);//测试
        }*/
        if (MainActivity.DEBUG) {
            Log.e(TAG, "有多少闹钟数据：" + data.size());
        }
        if (data != null) {
            adapter = new ClockAdapter(this, data, this);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    position = i;
                    Intent intent = new Intent(MainActivity.this, EditAlarmClock.class);
                    intent.putExtra("data", data.get(i));
                    startActivityForResult(intent, REQUEST_CODE);
                }
            });
        }
    }

    //读取数据
    private void readData() {
        data = new ArrayList<>();
        File file = new File("/data/data/com.boll.alarmclock/shared_prefs/ClockData.xml");
        clockData = getSharedPreferences("ClockData", MODE_PRIVATE);
        if (!file.exists() | file.length() < 100) {
            clockPicture.setVisibility(View.VISIBLE);
        } else {
            clockPicture.setVisibility(View.GONE);
            readDataFromSP();
        }
    }

    private void readDataFromSP() {
        data = Alarms.readDataFromSP(this);
        //sort();
        Log.e(TAG, "读取出来的数据data: "+data);
    }

    private void findView() {
        /*toolBarBack = findViewById(R.id.back);*/
        listView = findViewById(R.id.list);
        clockPicture = findViewById(R.id.clock_picture);
        createClock = findViewById(R.id.create_clock);
        listView.setVerticalScrollBarEnabled(false);
        listView.setFastScrollEnabled(false);
    }

    //数据保存
    private void saveData() {
        SharedPreferences.Editor editor = clockData.edit();
        editor.clear();
        if (data.size() == 0) {
            clockPicture.setVisibility(View.VISIBLE);
        } else {
            int i;
            clockPicture.setVisibility(View.GONE);
            for (i = 0; i < data.size(); i++) {
                editor.putString(1 + i + "", data.get(i));
            }
        }
        editor.apply();
        Log.e(TAG, "保存的最新数据data: " + data);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Alarms.setNextAlert(this, Calendar.getInstance().getTimeInMillis());
        }

        setAlarmIcon();
    }

    //switch的状态改变
    private void notifyChange(int position, boolean state) {
        String[] array = Util.stringToArray(data.get(position));
        array[8] = String.valueOf(state);
        array[9]="0";
        array[10]="0";
        data.set(position, Util.arrayToString(array));
        saveData();
        adapter.notifyDataSetChanged();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.create_clock:
                if (data.size() == 10) {
                    View preventDialog = View.inflate(this, R.layout.prevent_dialog, null);
                    AlertDialog dialog = new AlertDialog.Builder(this)
                            .setView(preventDialog)
                            .setCancelable(false)
                            .create();
                    dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_shape);
                    Window win = dialog.getWindow();
                    NavigationBarUtil.focusNotAle(win);
                    dialog.show();
                    NavigationBarUtil.hideNavigationBar(win);
                    NavigationBarUtil.clearFocusNotAle(win);
                    WindowManager.LayoutParams lp = win.getAttributes();
                    Display display = getWindowManager().getDefaultDisplay();
                    lp.width = (int) (display.getWidth() * 0.55);
                    lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                    win.setAttributes(lp);

                    Button button = preventDialog.findViewById(R.id.ok);
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });
                } else {
                    Intent intent = new Intent(this, EditAlarmClock.class);
                    startActivityForResult(intent, REQUEST_CODE);
                }
                break;
            /*case R.id.back:
                finish();
                break;*/
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent nowData) {
        super.onActivityResult(requestCode, resultCode, nowData);
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE && nowData != null) {
            if ("".equals(nowData.getStringExtra("newData"))) {
                data.remove(position);
            } else {
                if (position == -1) {
                    String s = nowData.getStringExtra("newData");
                    data.add(s);
                    if (MainActivity.DEBUG) {
                        Log.e("eeee", "新增闹钟的信息："+s);
                    }
                } else {
                    data.set(position, nowData.getStringExtra("newData"));
                }
            }
            position = -1;//每次操作后恢复默认状态
            sort();
            saveData();
            adapter.notifyDataSetChanged();
        }
    }

    private void sort() {
        //冒泡排序算法
        if (data.size()>0){
            int[] array=new int[data.size()];
            for (int i=0;i<data.size();i++){
                String s=data.get(i).substring(0,5);
                String[] ss=s.split(":");
                array[i]=Integer.parseInt(ss[0])*60+Integer.parseInt(ss[1]);
            }
            for (int i=0;i<array.length-1;i++){
                for (int j=0;j< array.length-1-i;j++){
                    if (array[j]>=array[j+1]){
                        int tem=array[j];
                        array[j]=array[j+1];
                        array[j+1]=tem;
                        String temp=data.get(j);
                        data.set(j,data.get(j+1));
                        data.set(j+1,temp);
                    }
                }
            }
        }
    }

    @Override
    public void changeListener(int position, boolean state) {
        notifyChange(position, state);
    }

}