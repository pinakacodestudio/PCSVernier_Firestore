package com.pcs.pcsvernier_firebase;

import android.content.Context;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MyUtil {
//    public static String users[] = {"Rajesh", "Jayesh", "Bhavesh", "Kanti"};
//    public static String items[] = {"365214-G", "365323-S", "365423-R", "362541-Q"};
//    public static String process[] = {"Drilling", "Threading", "Cutting"};
//    public static String machines[] = {"DR-21", "Dr-22", "CT-11", "CT-25", "DR-26"};
    public static ArrayList users = new ArrayList();
    public static ArrayList items = new ArrayList();
    public static ArrayList process = new ArrayList();
    public static ArrayList machines = new ArrayList();
    public static Map<String, Object> readingMap = new HashMap<String, Object>();
    public static final String userKey = "user";
    public static final String itemKey = "item";
    public static final String processKey = "process";
    public static final String machineKey = "machine";
    public static final String parameterKey = "parameter";
    public static final String upperToleranceKey = "uTolerance";
    public static final String lowerToleranceKey = "lTolerance";
    public static final String readingKey = "reading";
    public static final String resultKey = "result";
    public static final String dateTimeKey = "datetime";

    public static void showMessage(Context context, String text){
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }
}
