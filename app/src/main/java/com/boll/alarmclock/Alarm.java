package com.boll.alarmclock;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Arrays;

public class Alarm implements Parcelable{
    private int id;
    private int hour;
    private int minute;
    private boolean state;         //闹钟是否开启
    private boolean isRepeat;      //闹钟是否重复
    private int[] repeatDates;    //闹钟重复情况
    private long nextTime;        //下次响铃时间
    private long nowTime;         //五轮循环的开始时间
    private int count;             //第几轮

    public Alarm() {
    }

    protected Alarm(Parcel in) {
        id = in.readInt();
        hour = in.readInt();
        minute = in.readInt();
        state = in.readByte() != 0;
        isRepeat = in.readByte() != 0;
        repeatDates = in.createIntArray();
        nextTime = in.readLong();
        nowTime = in.readLong();
        count = in.readInt();
    }

    public static final Creator<Alarm> CREATOR = new Creator<Alarm>() {
        @Override
        public Alarm createFromParcel(Parcel in) {
            return new Alarm(in);
        }

        @Override
        public Alarm[] newArray(int size) {
            return new Alarm[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeInt(hour);
        parcel.writeInt(minute);
        parcel.writeByte((byte) (state ? 1 : 0));
        parcel.writeByte((byte) (isRepeat ? 1 : 0));
        parcel.writeIntArray(repeatDates);
        parcel.writeLong(nextTime);
        parcel.writeLong(nowTime);
        parcel.writeInt(count);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public boolean getState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public boolean getIsRepeat() {
        return isRepeat;
    }

    public void setIsRepeat(boolean repeat) {
        isRepeat = repeat;
    }

    public int[] getRepeatDates() {
        return repeatDates;
    }

    public void setRepeatDates(int[] repeatDates) {
        this.repeatDates = repeatDates;
    }

    public long getNextTime() {
        return nextTime;
    }

    public void setNextTime(long nextTime) {
        this.nextTime = nextTime;
    }

    public long getNowTime() {
        return nowTime;
    }

    public void setNowTime(long nowTime) {
        this.nowTime = nowTime;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

   /* @Override
    public String toString() {
        return "Alarm{" +
                "id=" + id +
                ", hour=" + hour +
                ", minute=" + minute +
                ", state=" + state +
                ", isRepeat=" + isRepeat +
                ", repeatDates=" + Arrays.toString(repeatDates) +
                ", nextTime=" + nextTime +
                '}';
    }*/

    @Override
    public String toString() {
        return format(hour)+":"+format(minute)+"，"+
                formatInt(this.repeatDates[0])+"，"+
                formatInt(this.repeatDates[1])+"，"+
                formatInt(this.repeatDates[2])+"，"+
                formatInt(this.repeatDates[3])+"，"+
                formatInt(this.repeatDates[4])+"，"+
                formatInt(this.repeatDates[5])+"，"+
                formatInt(this.repeatDates[6])+"，"+
                this.getState()+"，"+
                this.getNowTime()+"，"+
                this.getCount();
    }

    private String format(int time){
        if (time<10)
            return "0"+time;
        else
            return time+"";
    }

    private boolean formatInt(int num){
        if (num==1)
            return true;
        else
            return false;
    }
}
