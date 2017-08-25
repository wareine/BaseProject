package com.wlazy.baseproject;

import android.app.Application;
import android.os.Handler;

import com.wlazy.baseproject.utils.SPUtils;

/**
 * Created by Wang on 2017/8/21.
 */

public class BaseApplication extends Application {
    private static BaseApplication mContext;

    private static Handler mMainThreadHandler;

    @Override
    public void onCreate() {
        super.onCreate();
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
}
