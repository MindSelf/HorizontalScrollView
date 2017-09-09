package com.example.zhaolexi.horizontalscrollviewex;

import android.content.Context;
import android.util.DisplayMetrics;

/**
 * Created by ZHAOLEXI on 2017/7/12.
 */

public class MyUtils {

    public static DisplayMetrics getScreenMetrics(Context context) {
        return context.getResources().getDisplayMetrics();
    }

    public static int dip2px(Context context, float dip){
        final float scale=getScreenMetrics(context).density;
        return (int) (dip * scale + 0.5f);
    }

    public static int sp2pt(Context context, float sp){
        final float scale=getScreenMetrics(context).scaledDensity;
        return (int) (sp * scale + 0.5f);
    }
}
