package com.wlazy.core;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.Handler;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.ObjectUtils;
import com.blankj.utilcode.util.Utils;
import com.wlazy.core.util.CrashHandler;

import java.util.List;
import java.util.Stack;

/**
 * 应用程序管理类
 */
public class AppManager {

  @SuppressLint("StaticFieldLeak")
  private static Application sApplication;
  private static Handler mMainThreadHandler;

  private AppManager() {
    throw new UnsupportedOperationException("u can't instantiate me...");
  }

  public static void init(Application application) {
    sApplication = application;
    CrashHandler.getInstance().init(application);
    Utils.init(application);
    mMainThreadHandler = new Handler();
  }

  public static Application getApp() {
    if (sApplication != null) return sApplication;
    throw new NullPointerException("u should init first");
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