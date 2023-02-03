package com.boll.alarmclock.picker;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
public @interface ItemTextAlign {
    int CENTER = 0;
    int LEFT = 1;
    int RIGHT = 2;
}
