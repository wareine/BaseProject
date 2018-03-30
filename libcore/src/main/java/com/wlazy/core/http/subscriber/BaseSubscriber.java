package com.wlazy.core.http.subscriber;

import android.app.ProgressDialog;
import android.content.Context;
import android.text.TextUtils;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;

import io.reactivex.subscribers.DisposableSubscriber;

/**
 * Created by Wang on 2017/8/25.
 */

abstract public class BaseSubscriber<T> extends DisposableSubscriber<T> {
    private static final String TAG = "BaseSubscriber";
    //加载对话框
    protected ProgressDialog mLoadingDialog;
    //是否显示加载对话框
    protected boolean mIsShowDialog;
    protected Context mContext;
    protected String mLoadText;
    protected String mTextTag;


    public BaseSubscriber(Context context, String testTag, String loadText, boolean isShowDialog) {
        mContext = context;
        mTextTag = testTag;
        mLoadText = loadText;
        mIsShowDialog = isShowDialog;
    }

    public BaseSubscriber(Context context, String testTag, boolean isShowDialog) {
        this(context,testTag,"",isShowDialog);
    }

    public BaseSubscriber(Context context, String testTag) {
        this(context,testTag,"",true);
    }

    public BaseSubscriber(String textTag) {
        this(null,textTag,null,false);
    }

    protected void showDialog() {
        if (mLoadingDialog == null) {
            if (TextUtils.isEmpty(mLoadText)) {
                mLoadText = "正在努力加载中...";
            }
            mLoadingDialog = ProgressDialog.show(mContext, null, mLoadText,
                    true, true);
        }
        if (!mLoadingDialog.isShowing()) {
            mLoadingDialog.show();
        }
    }

    protected void disMissDialog() {
        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        //示对话框
        if (mIsShowDialog) {
            showDialog();
        }
    }

    @Override
    public void onNext(T t) {
        doOnNext(t);
    }


    @Override
    public void onError(Throwable t) {
        //显示错误
        if (mIsShowDialog) {
            disMissDialog();
        }
        ToastUtils.showShort(t.getMessage());

        if (t != null) {
            t.printStackTrace();
        }
        LogUtils.e(TAG, mTextTag + ":onError--->>" + ((t == null || TextUtils.isEmpty(t.getMessage())) ? "未知错误" : t.getMessage()));
    }

    @Override
    public void onComplete() {
        //消失对话框
        if (mIsShowDialog) {
            disMissDialog();
        }
    }

    //子类去实现具体的解析逻辑
    public abstract void doOnNext(T result);
}

