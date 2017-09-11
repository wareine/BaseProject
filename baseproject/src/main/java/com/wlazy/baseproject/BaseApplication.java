package com.wlazy.baseproject;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.Handler;

import com.squareup.leakcanary.LeakCanary;
import com.wlazy.baseproject.utils.CrashHandler;
import com.wlazy.baseproject.utils.SPUtils;

import java.util.List;

/**
 * Created by Wang on 2017/8/21.
 */

public class BaseApplication extends Application {
    private static BaseApplication mContext;

    private static Handler mMainThreadHandler;

    @Override
    public void onCreate() {
        super.onCreate();
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);
        CrashHandler.getInstance().init(this);
        SPUtils.init(this);
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
