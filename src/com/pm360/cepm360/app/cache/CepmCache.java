package com.pm360.cepm360.app.cache;

import android.util.Log;

import java.util.ArrayList;

public class CepmCache {
    
    public static final String TAG = "CEPM360";
    public static final boolean DEBUG = true;
    
    public static void dumpCache(String clazz, ArrayList<?> list) {
        if (DEBUG) Log.i(TAG, "[" + clazz + "] size = " + list.size() + "\n" + list);
    }
}
