package com.wlazy.baseproject.widget.refreshlayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.wlazy.baseproject.R;


/**
 * Created by Wang on 2017/9/5.
 */

public class SwipeRefreshLayout extends SmartRefreshLayout{
    public SwipeRefreshLayout(Context context) {
        super(context);
        initViews(context,null);
    }

    public SwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews(context,attrs);
    }

    public SwipeRefreshLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews(context,attrs);
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    public SwipeRefreshLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initViews(context,attrs);
    }

    private void initViews(Context context,AttributeSet attrs) {
        this.setRefreshHeader(new JxbRefreshHeader(context));
        this.setRefreshFooter(new JxbRefreshFooter(context));
        boolean enableLoadmore = false;
        if(attrs != null){
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.SmartRefreshLayout);
            if(ta != null){
                enableLoadmore = ta.getBoolean(R.styleable.SmartRefreshLayout_srlEnableLoadmore, false);
            }
            ta.recycle();
        }
        //默认关闭上拉加载功能
        if(!enableLoadmore) {
            //this.setEnableRefresh(true);//是否启用下拉刷新功能
            this.setEnableLoadmore(false);//是否启用上拉加载功能
            this.setEnableAutoLoadmore(false);//是否启用列表惯性滑动到底部时自动加载更多
            this.setEnablePureScrollMode(false);//是否启用纯滚动模式
            this.setEnableNestedScroll(false);//是否启用嵌套滚动
        }
    }
}
