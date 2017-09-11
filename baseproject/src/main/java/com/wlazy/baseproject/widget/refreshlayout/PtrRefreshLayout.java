package com.wlazy.baseproject.widget.refreshlayout;

import android.content.Context;
import android.util.AttributeSet;

import in.srain.cube.views.ptr.PtrFrameLayout;

/**
 * Created by Wang on 2017/9/4.
 */

public class PtrRefreshLayout extends PtrFrameLayout {

    private PtrJxbHeader mPtrClassicHeader;

    public PtrRefreshLayout(Context context) {
        super(context);
        initViews();
    }

    public PtrRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews();
    }

    public PtrRefreshLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initViews();
    }

    private void initViews() {
        mPtrClassicHeader = new PtrJxbHeader(getContext());
        setHeaderView(mPtrClassicHeader);
        addPtrUIHandler(mPtrClassicHeader);
        // the following are default settings
        this.setResistance(1.7f);
        this.setRatioOfHeaderHeightToRefresh(1.2f);
        this.setDurationToClose(200);
        this.setDurationToCloseHeader(1000);
        // default is false
        this.setPullToRefresh(false);
        // default is true
        this.setKeepHeaderWhenRefresh(true);
    }

    public PtrJxbHeader getHeader() {
        return mPtrClassicHeader;
    }

}

