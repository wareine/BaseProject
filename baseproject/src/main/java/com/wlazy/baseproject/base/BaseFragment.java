package com.wlazy.baseproject.base;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.trello.rxlifecycle2.components.RxFragment;

/**
 * Created by Wang on 2017/8/21.
 */

public abstract class BaseFragment extends RxFragment implements View.OnClickListener{
    protected final String TAG = getClass().getSimpleName();
    protected Context mContext;
    private ProgressDialog mLoadingDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public BaseFragment getFragment(){
        return this;
    }

    protected <T extends View> T findViewById(int viewId) {
        if (viewId > 0) {
            return (T) getView().findViewById(viewId);
        }
        return null;
    }

    protected <T extends View> T findViewInContainer(View containerView, int childViewId) {
        if (containerView == null || childViewId <= 0) {
            return null;
        }
        return (T) containerView.findViewById(childViewId);
    }

    protected void showLoading() {
        if (mLoadingDialog == null) {
            mLoadingDialog = ProgressDialog.show(mContext, null, "正在努力加载中...", true, true);
        }
        mLoadingDialog.show();
    }

    protected void hideLoading() {
        if (mLoadingDialog != null) {
            mLoadingDialog.dismiss();
        }
    }

    @Override
    public void onClick(View v) {

    }
}

