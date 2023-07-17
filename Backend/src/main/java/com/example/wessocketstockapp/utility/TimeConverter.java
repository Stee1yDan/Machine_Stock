package com.example.wessocketstockapp.utility;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeConverter
{
    static DateFormat fullFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
    static DateFormat shortFormat = new SimpleDateFormat("HH:mm:ss");

    public static String getFormatedDate(String milliseconds)
    {
        Date res = new Date(Long.valueOf(milliseconds));
        return shortFormat.format(res);
    }
}
