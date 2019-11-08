package com.japg.mastermoviles.opengl10.util;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;

public class Utils {

    public static int convertDpToPixel(float dp, Context context){
        return (int)(dp * (getDisplayMetrics(context).densityDpi / 160f));
    }

    public static float convertPixelsToDp(int px, Context context){
        return px / (getDisplayMetrics(context).densityDpi / 160f);
    }

    public static DisplayMetrics getDisplayMetrics(Context context){
        Resources resources = context.getResources();
        return resources.getDisplayMetrics();
    }
}
