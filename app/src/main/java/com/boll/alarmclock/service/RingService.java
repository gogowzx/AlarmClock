package com.boll.alarmclock.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;

import com.boll.alarmclock.MainActivity;
import com.boll.alarmclock.R;
import com.boll.alarmclock.utils.AlarmWakeLock;

public class RingService extends Service {
    private static final String CHANNEL_ID = "id";
    private MediaPlayer mediaPlayer;
    private Vibrator vibrator;
    private RingAndVibrate ringAndVibrate;
    private int ringGrade;//记录系统声音等级
    private AudioManager audioManager;

    @Override
    public void onCreate() {
        super.onCreate();
        startTo();
        if (MainActivity.DEBUG) {
            Log.e("eeee", "开启铃声服务");
        }
        startRing();
        startVibrate();
    }

    private void startVibrate() {
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        VibrationEffect effect = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            effect = VibrationEffect.createWaveform(new long[]{1000, 2000, 2000, 3000}, 2);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(effect);
        }
    }

    private void startRing() {
        mediaPlayer = MediaPlayer.create(this, R.raw.kanong);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        //ringGrade=audioManager.getStreamVolume(AudioManager.STREAM_SYSTEM);
        //audioManager.setStreamVolume(AudioManager.STREAM_SYSTEM,0,0);
        /*if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {

        }*/
        mediaPlayer.setVolume(0.6f, 0.6f);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
    }

    @Override
    public IBinder onBind(Intent intent) {
        ringAndVibrate = new RingAndVibrate();
        return ringAndVibrate;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //AlarmWakeLock.releaseCpuLock();
    }

    private void startTo() {
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NotificationChannel Channel;
        Notification notification;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Channel = new NotificationChannel(CHANNEL_ID, "WarningService", NotificationManager.IMPORTANCE_DEFAULT);
        Channel.enableLights(true);
        Channel.setLightColor(Color.RED);
        Channel.setShowBadge(true);
        Channel.setDescription("PowerStateService");
        Channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC); //设置锁屏可见 VISIBILITY_PUBLIC=可见
        manager.createNotificationChannel(Channel);}
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notification = new Notification.Builder(this,CHANNEL_ID)
                    .setContentTitle("闹钟")//标题
                    .setContentText("闹钟服务正在运行")//内容
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.drawable.ic_baseline_access_alarms_24)
                    //.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                    .build();
        startForeground(1, notification);}//服务前台化只能使用startForeground()方法
    }

    public class RingAndVibrate extends Binder {
        public void cancelRing() {
            if (mediaPlayer!=null){
                //audioManager.setStreamVolume(AudioManager.STREAM_SYSTEM,ringGrade,0);
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
            }
        }

        public void cancelVibrate() {
            if (vibrator!=null){
                vibrator.cancel();
                vibrator = null;
            }
        }
    }

}