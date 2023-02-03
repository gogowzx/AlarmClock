package com.boll.alarmclock.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

/**
 * HOME键监听类。
 */
public class HomeKeyListener extends BroadcastReceiver {
    private Context context;

    public HomeKeyListener(Context context) {
        this.context = context;
    }

    /**
     * 通常在Activity的onStart方法中调用
     */
    public void start() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        context.registerReceiver(this, filter);
    }

    /**
     * 通常在Activity的onStop方法中调用
     */
    public void stop() {
        context.unregisterReceiver(this);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (Intent.ACTION_CLOSE_SYSTEM_DIALOGS.equals(action)) {
            String reason = intent.getStringExtra("reason");
            if ("homekey".equals(reason)) {
                // 按下HOME健
                if (mOnHomeKeyPressListener != null) {
                    mOnHomeKeyPressListener.onHomeKeyPress();
                }
            } else if ("recentapps".equals(reason)) {
                // 长按HOME键
                if (mOnHomeKeyLongPressListener != null) {
                    mOnHomeKeyLongPressListener.onHomeKeyLongPress();
                }
            }
        }
    }

    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // 按下
    // （这里把 Press 和 LongPress 分开是为了能够使用Lambda）
    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++

    private OnHomeKeyPressListener mOnHomeKeyPressListener;

    public void setOnHomeKeyPressListener(OnHomeKeyPressListener listener) {
        mOnHomeKeyPressListener = listener;
    }

    public interface OnHomeKeyPressListener {
        void onHomeKeyPress();
    }

    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // 长按
    // (长按通常不用。很多手机把长按做成了系统级别的其它功能，比如启动语音助手)
    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++

    private OnHomeKeyLongPressListener mOnHomeKeyLongPressListener;

    public void setOnHomekeyLongPressListener(OnHomeKeyLongPressListener listener) {
        mOnHomeKeyLongPressListener = listener;
    }

    public interface OnHomeKeyLongPressListener {
        void onHomeKeyLongPress();
    }
}