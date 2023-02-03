package com.boll.alarmclock.utils;

import android.content.res.TypedArray;
import android.util.Log;

import com.boll.alarmclock.R;

public class Util {
    //字符串结构["16:20"，"true"，"true"，"true"，"true"，"true"，"true"，"false"，"true"]
    //  分别为：  时间   周一闹钟  周二闹钟  周三闹钟 周四闹钟 周五闹钟  周六闹钟 周日没有  闹钟是否开启


    //数据分割成数组
    public static String[] stringToArray(String s) {
        String[] item;
        item = s.split("，");
        return item;
    }

    //数组拼接成字符串
    public static String arrayToString(String[] array) {
        StringBuilder builder = new StringBuilder();
        if (array != null && array.length > 0) {
            for (int i = 0; i < array.length; i++) {
                if (i < array.length - 1)
                    builder.append(array[i]).append("，");
                else builder.append(array[i]);
            }
            return builder.toString();
        }
        return "";
    }

    public static String dataParseText(String s) {
        int num;
        String[] array = Util.stringToArray(s);
        num = count(array);
       /* if (num <= 0) {
            try {
                throw new Exception("数据有误");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }*/
        if (num == 7) {
            return "每天";
        } else {
            String s1=buildText(array, num);
            if (s1.equals("周一、周二、周三、周四、周五"))
                s1="工作日";
            return s1;
        }
    }

    private static String buildText(String[] array, int num) {
        int nowNum = 0;
        StringBuilder builder = new StringBuilder();
        String[] week = new String[]{"周一", "周二", "周三", "周四", "周五", "周六", "周日"};
        for (int i = 1; i < 8; i++) {
            if (Boolean.parseBoolean(array[i])) {
                builder.append(week[i - 1]);
                nowNum++;
                if (nowNum < num) {
                    builder.append("、");
                }
            }
        }
        if (num > 0)
            return builder.toString();
        else
            return "无";
    }


    //计数有几天有闹铃
    private static int count(String[] array) {
        int num = 0;
        for (int i = 1; i < 8; i++) {
            if (Boolean.parseBoolean(array[i])) {
                num++;
            }
        }
        return num;
    }

    public static int[] concat(int[] a, int[] b) {
        int[] c= new int[a.length+b.length];
        System.arraycopy(a, 0, c, 0, a.length);
        System.arraycopy(b, 0, c, a.length, b.length);
        return c;
    }

    public static String format(int time){
        StringBuilder builder=new StringBuilder();
        if (time<10)
            return builder.append("0").append(time).toString();
        else
            return builder.append(time).toString();
    }

}
