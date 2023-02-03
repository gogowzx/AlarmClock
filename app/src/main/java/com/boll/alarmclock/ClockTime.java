package com.boll.alarmclock;

public class ClockTime {  //每个该响铃的对象
    private int id;       //属于哪个闹铃
    private long time;    //该对象的响铃时间
    private int count;    //属于是第几轮

    public ClockTime() {
    }

    public ClockTime(int id, long time, int count) {
        this.id = id;
        this.time = time;
        this.count = count;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return  id + "," + time + "," + count;
    }
}
