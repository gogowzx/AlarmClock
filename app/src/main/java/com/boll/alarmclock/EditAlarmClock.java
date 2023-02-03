package com.boll.alarmclock;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.text.TextPaint;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.boll.alarmclock.picker.NumberWheelView;
import com.boll.alarmclock.picker.OnWheelChangedListener;
import com.boll.alarmclock.picker.WheelFormatter;
import com.boll.alarmclock.picker.WheelView;
import com.boll.alarmclock.utils.TimeUtils;
import com.boll.alarmclock.utils.Util;

import java.util.ArrayList;
import java.util.Calendar;

public class EditAlarmClock extends BaseActivity implements View.OnClickListener, OnWheelChangedListener
        , WheelFormatter {
    private NumberWheelView hours;
    private NumberWheelView minutes;
    private ImageView back;
    private Button addClockComplete;
    private LinearLayout addClockLayout;
    private Button editClockComplete;
    private Button editClockDelete;
    private LinearLayout editClockLayout;
    private RelativeLayout setRepeat;

    private String oldData;
    private boolean state;
    private String newData;
    private TextView repeatDate, title;
    private int selectedHour;     //选择器选择的时间小时
    private int selectedMinute;   //选择器选择的时间分钟
    private int nowDateOfWeek;   //Calendar返回的星期几
    private String dataSet;      //设置一周中哪几天有闹钟

    public EditAlarmClock() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_alarm_clock_layout);

        findID();
        initPicker();
        initIntent();
        setLister();
    }

    private void initIntent() {
        Intent intent = getIntent();
        oldData = intent.getStringExtra("data");
        if (oldData == null) {
            title.setText(R.string.create_alarm_clock);
            editClockLayout.setVisibility(View.GONE);
            addClockLayout.setVisibility(View.VISIBLE);
            defaultRepeatDate();
            Calendar calendar = Calendar.getInstance();
            int nowHour = calendar.get(Calendar.HOUR_OF_DAY);
            int nowMinute = calendar.get(Calendar.MINUTE);
            selectedHour = nowHour;
            selectedMinute = nowMinute;
            hours.setDefaultValue(nowHour);
            minutes.setDefaultValue(nowMinute);
            state = true;
        } else {
            title.setText(R.string.edit_clock_title);
            editClockLayout.setVisibility(View.VISIBLE);
            addClockLayout.setVisibility(View.GONE);
            String[] data = Util.stringToArray(oldData);
            int oldHour = Integer.parseInt(data[0].split(":")[0]);
            int oldMinute = Integer.parseInt(data[0].split(":")[1]);
            hours.setDefaultValue(oldHour);
            minutes.setDefaultValue(oldMinute);
            selectedHour = oldHour;
            selectedMinute = oldMinute;
            String textView = Util.dataParseText(oldData);
            repeatDate.setText(textView);
            dataSet = dataSetDefault(data);
            state = Boolean.parseBoolean(Util.stringToArray(oldData)[8]);
        }

    }

    private String dataSetDefault(String[] data) {
        StringBuilder builder = new StringBuilder();
        for (int i = 1; i <8; i++) {
            if (i == 7) {
                builder.append(data[i]);
            } else {
                builder.append(data[i]).append("，");
            }
        }
        return builder.toString();
    }


    private void defaultRepeatDate() {
        //新建闹钟默认设置为今天的这个星期几
        /*Calendar calendar = Calendar.getInstance();
        nowDateOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        String date = TimeUtils.dateOfWeek(nowDateOfWeek);
        dataSet = dataSetDefault(nowDateOfWeek);
        repeatDate.setText(date);*/
        nowDateOfWeek = 0;
        String date = TimeUtils.dateOfWeek(nowDateOfWeek);
        dataSet = dataSetDefault(nowDateOfWeek);
        repeatDate.setText(date);
    }

    private String dataSetDefault(int nowDateOfWeek) {
        if (nowDateOfWeek == 1) {
            return "false，false，false，false，false，false，true";
        } else {
            StringBuilder builder = new StringBuilder();
            int[] dateNum = new int[]{2, 3, 4, 5, 6, 7, 8};
            for (int i = 0; i < dateNum.length; i++) {
                if (nowDateOfWeek == dateNum[i]) {
                    if (i == dateNum.length - 1)
                        builder.append("true");
                    else
                        builder.append("true").append("，");
                } else {
                    if (i == dateNum.length - 1)
                        builder.append("false");
                    else
                        builder.append("false").append("，");
                }
            }
            return builder.toString();
        }
    }

    private void findID() {
        /*back = findViewById(R.id.back);*/
        title = findViewById(R.id.title);
        hours = findViewById(R.id.hours);
        hours.setIndicatorSize(2);
        hours.setIndicatorColor(getResources().getColor(R.color.color_3));
        minutes = findViewById(R.id.minutes);
        minutes.setIndicatorSize(2);
        minutes.setIndicatorColor(getResources().getColor(R.color.color_3));
        setRepeat = findViewById(R.id.set_repeat);
        addClockLayout = findViewById(R.id.add_clock_layout_two);
        addClockComplete = findViewById(R.id.add_clock_complete);
        editClockLayout = findViewById(R.id.edit_clock_layout_one);
        editClockComplete = findViewById(R.id.edit_clock_complete);
        editClockDelete = findViewById(R.id.edit_clock_delete);
        repeatDate = findViewById(R.id.repeat_date);

    }

    private void initPicker() {
        hours.setRange(0, 23, 1);
        minutes.setRange(0, 59, 1);
    }

    private void setLister() {
        /*back.setOnClickListener(this);*/
        setRepeat.setOnClickListener(this);
        addClockComplete.setOnClickListener(this);
        editClockComplete.setOnClickListener(this);
        editClockDelete.setOnClickListener(this);
        hours.setOnWheelChangedListener(this);
        hours.setFormatter(this);
        minutes.setOnWheelChangedListener(this);
        minutes.setFormatter(this);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            /*case R.id.back:
                finish();
                break;*/
            case R.id.set_repeat:
                repeatDialogDisplay();
                break;
            case R.id.add_clock_complete:
            case R.id.edit_clock_complete:
                String result = finalResult();
                Intent intent = new Intent();
                intent.putExtra("newData", result);
                setResult(RESULT_OK, intent);
                finish();
                break;
            case R.id.edit_clock_delete:
                deleteClock();
                break;
        }
    }

    private void deleteClock() {
        View view = View.inflate(this, R.layout.delete_dialog, null);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(view)
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
        lp.width = (int) (display.getWidth() * 0.6);
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        win.setAttributes(lp);

        Button back = view.findViewById(R.id.delete_delete);
        Button delete = view.findViewById(R.id.delete_back);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Intent intent = new Intent();
                intent.putExtra("newData", "");
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    //拼接最后的返回数据
    private String finalResult() {
        String result;
        Log.e("eeee", selectedHour + "小时" + selectedMinute + "分钟");//选择的时间
        String timeSet = new StringBuilder().append(formatNumber(selectedHour)).append(":").append(formatNumber(selectedMinute))
                .append("，").toString();
        result = new StringBuilder().append(timeSet).append(dataSet).append("，true").append("，0").append("，0").toString();
        Log.e("eeee", "完整的数据" + result);
        return result;
    }

    private String formatNumber(int number) {
        if (number < 10) {
            return "0" + number;
        } else {
            return String.valueOf(number);
        }
    }

    //弹出窗口设置重复时间
    private void repeatDialogDisplay() {
        View view = View.inflate(this, R.layout.repeat_window, null);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(view)
                .setCancelable(false)
                .create();
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.item_shape);
        Window win = dialog.getWindow();
        NavigationBarUtil.focusNotAle(win);
        dialog.show();
        NavigationBarUtil.hideNavigationBar(win);
        NavigationBarUtil.clearFocusNotAle(win);

        WindowManager.LayoutParams lp = win.getAttributes();
        Display display = getWindowManager().getDefaultDisplay();
        lp.width = (int) (display.getWidth() * 0.8);
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        win.setAttributes(lp);

        Button monday = view.findViewById(R.id.day1);
        Button tuesday = view.findViewById(R.id.day2);
        Button wednesday = view.findViewById(R.id.day3);
        Button thursday = view.findViewById(R.id.day4);
        Button friday = view.findViewById(R.id.day5);
        Button saturday = view.findViewById(R.id.day6);
        Button sunday = view.findViewById(R.id.day7);
        Button sure = view.findViewById(R.id.day_sure);

        ArrayList<Button> bts = new ArrayList<>();
        bts.add(monday);
        bts.add(tuesday);
        bts.add(wednesday);
        bts.add(thursday);
        bts.add(friday);
        bts.add(saturday);
        bts.add(sunday);

        //  1、进行默认标志  2、更新UI
        if (oldData == null) {
            //setDefaultDate(nowDateOfWeek, bts);
            if (newData!=null){
                setOldData(newData, bts);
            }
        } else {
            if (newData!=null){
                setOldData(newData, bts);
            }else
                setOldData(oldData, bts);
            //setOldData(oldData, bts);//修改闹钟时每次完成不记忆重复情况
        }

        //  1、设置监听   2、进行标志（1是选中 -1是没有选中）   3、更新UI
        for (Button bt : bts) {
            bt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (view.getTag() == null) {
                        view.setTag(1);
                        Button button = (Button) view;
                        updateUI(button, R.color.color_4, R.drawable.repeat_button_background);
                    } else {
                        if ((int) view.getTag() == 1) {
                            view.setTag(-1);
                            Button button = (Button) view;
                            updateUI(button, R.color.color_2, R.drawable.repeat_button_background_2);
                        } else {
                            view.setTag(1);
                            Button button = (Button) view;
                            updateUI(button, R.color.color_4, R.drawable.repeat_button_background);
                        }
                    }
                }
            });
        }
        sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dataSet = getInputData(bts);
                Log.e("eeee", "检查：" + dataSet);//
                newData = selectedHour + ":" + selectedMinute + "，" + dataSet + "，" + state;
                String textView = Util.dataParseText(newData);
                repeatDate.setText(textView);
                dialog.dismiss();
            }
        });

    }

    private void setOldData(String oldData, ArrayList<Button> bts) {
        String[] array = Util.stringToArray(oldData);
        for (int i = 1; i < 8; i++) {
            if ("true".equals(array[i])) {
                Button button = bts.get(i - 1);
                button.setTag(1);
                updateUI(button, R.color.color_4, R.drawable.repeat_button_background);
            }
        }
    }

    //设置闹钟当天的星期几是设置了闹钟的（如：今天星期一，在设置闹钟的时候默认的闹钟重复是星期一）
    private void setDefaultDate(int nowDateOfWeek, final ArrayList<Button> list) {
        if (nowDateOfWeek == 1) {
            Button button = list.get(6);
            button.setTag(1);
            updateUI(button, R.color.color_4, R.drawable.repeat_button_background);
        } else {
            Button button = list.get(nowDateOfWeek - 2);
            button.setTag(1);
            updateUI(button, R.color.color_4, R.drawable.repeat_button_background);
        }
    }

    private void updateUI(Button button, int textColor, int backgroundColor) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            button.setTextColor(getColor(textColor));
        }
        button.setBackground(ResourcesCompat.getDrawable(getResources(), backgroundColor, null));
    }

    private String getInputData(ArrayList<Button> bts) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < bts.size(); i++) {
            if (bts.get(i).getTag() == null || (int) bts.get(i).getTag() == -1) {
                if (i == bts.size() - 1)
                    builder.append("false");
                else
                    builder.append("false").append("，");
            } else {
                if (i == bts.size() - 1)
                    builder.append("true");
                else
                    builder.append("true").append("，");
            }
        }
        return builder.toString();
    }

    @Override
    public void onWheelScrolled(WheelView view, int offset) {

    }

    @Override
    public void onWheelSelected(WheelView view, int position) {
        switch (view.getId()) {
            case R.id.hours:
                selectedHour = (int) view.getData().get(position);
                Log.d("eeee", "滑动选择的小时：" + selectedHour);
                break;
            case R.id.minutes:
                selectedMinute = (int) view.getData().get(position);
                Log.d("eeee", "滑动选择的分钟：" + selectedMinute);
                break;
        }
    }

    @Override
    public void onWheelScrollStateChanged(WheelView view, int state) {

    }

    @Override
    public void onWheelLoopFinished(WheelView view) {

    }

    @Override
    public String formatItem(@NonNull Object item) {
        int size = item.toString().length();
        if (size < 2)
            return "0" + item;
        else
            return item.toString();
        /*else {
            int temp=(int)item-100;
            if (temp < 10)
                return "0" + temp +"时";
            else
                return temp +"时";
        }*/
    }
}