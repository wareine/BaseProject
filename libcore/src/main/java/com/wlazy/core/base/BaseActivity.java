package com.wlazy.core.base;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

public abstract class BaseActivity extends RxAppCompatActivity implements View.OnClickListener {
    protected final String TAG = getClass().getSimpleName();
    protected Context mContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {

    }

    public BaseActivity getActivity(){
        return this;
    }

    protected void updateFragment(Fragment fragment, int viewId) {
        if (fragment == null || viewId <= 0) {
            return;
        }
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(viewId, fragment);
        transaction.commit();
    }

    protected <T extends View> T findAviewById(int viewId) {
        if (viewId > 0) {
            return (T) findViewById(viewId);
        }
        return null;
    }

    protected <T extends View> T findAviewInContainer(ViewGroup containerView, int childViewId) {
        if (containerView == null || childViewId <= 0) {
            return null;
        }
        return (T) containerView.findViewById(childViewId);
    }







}
