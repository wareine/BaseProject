package com.wlazy.core;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.Handler;


import com.blankj.utilcode.util.Utils;
import com.wlazy.core.util.CrashHandler;

import java.util.List;

/**
 * BaseApplication
 */

public class BaseApplication extends Application {
    private static BaseApplication mContext;
    private static Handler mMainThreadHandler;

    @Override
    public void onCreate() {
        super.onCreate();
        if(!isMainProcess(this)) return;

        CrashHandler.getInstance().init(this);
        Utils.init(this);
        mContext = this;
        mMainThreadHandler = new Handler();
    }

    public static BaseApplication getApplication() {
        return mContext;
    }

    public static Handler getMainThreadHandler() {
        return mMainThreadHandler;
    }

    public static boolean isMainProcess(Context context) {
        ActivityManager am = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE));
        List<ActivityManager.RunningAppProcessInfo> processInfoList = am.getRunningAppProcesses();
        String mainProcessName = context.getPackageName();
        int myPid = android.os.Process.myPid();
        for (ActivityManager.RunningAppProcessInfo info : processInfoList) {
            if (info.pid == myPid && mainProcessName.equals(info.processName)) {
                return true;
            }
        }
        return false;
    }
}
