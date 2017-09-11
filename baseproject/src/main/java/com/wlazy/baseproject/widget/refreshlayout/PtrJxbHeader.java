package com.wlazy.baseproject.widget.refreshlayout;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wlazy.baseproject.R;
import com.wlazy.baseproject.utils.DisplayUtil;

import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrUIHandler;
import in.srain.cube.views.ptr.indicator.PtrIndicator;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * Created by Wang on 2017/9/4.
 */

public class PtrJxbHeader extends LinearLayout implements PtrUIHandler {
    public static String REFRESH_HEADER_PULLDOWN = "下拉刷新";
    public static String REFRESH_HEADER_REFRESHING = "正在刷新...";
    public static String REFRESH_HEADER_LOADING = "正在加载...";
    public static String REFRESH_HEADER_RELEASE = "释放刷新...";
    public static String REFRESH_HEADER_FINISH = "刷新完成";
    public static String REFRESH_HEADER_FAILED = "刷新失败";

    static final int ROTATION_ANIMATION_DURATION = 1200;
    private Animation mRotateAnimation;

    TextView mTitleText;
    ImageView mProgressView;
    public PtrJxbHeader(Context context) {
        super(context);
        initView(context);
    }

    public PtrJxbHeader(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public PtrJxbHeader(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context){
        this.setGravity(Gravity.CENTER);
        this.setOrientation(LinearLayout.VERTICAL);
        mTitleText = new TextView(context);
        mTitleText.setText(REFRESH_HEADER_PULLDOWN);
        mTitleText.setTextColor(0xff8b8b8b);
        mTitleText.setTextSize(13);

        LayoutParams lpProgress = new LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        mProgressView = new ImageView(context);
        mProgressView.setImageResource(R.drawable.default_ptr_rotate);
        addView(mProgressView, lpProgress);

        LayoutParams lpHeaderText = new LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        lpHeaderText.topMargin = DisplayUtil.dp2px(context,5);
        addView(mTitleText, lpHeaderText);

        setPadding(0,DisplayUtil.dp2px(context,10),0, DisplayUtil.dp2px(context,18));
        setMinimumHeight(DisplayUtil.dp2px(context,60));

        mRotateAnimation = new RotateAnimation(0, 720, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        mRotateAnimation.setInterpolator(new LinearInterpolator());
        mRotateAnimation.setDuration(ROTATION_ANIMATION_DURATION);
        mRotateAnimation.setRepeatCount(Animation.INFINITE);
        mRotateAnimation.setRepeatMode(Animation.RESTART);
    }

    @Override
    public void onUIReset(PtrFrameLayout frame) {
        mProgressView.clearAnimation();
        mProgressView.setVisibility(INVISIBLE);
        mTitleText.setText(REFRESH_HEADER_PULLDOWN);
    }

    @Override
    public void onUIRefreshPrepare(PtrFrameLayout frame) {
        mProgressView.setVisibility(VISIBLE);
        if (frame.isPullToRefresh()) {
            mTitleText.setText(REFRESH_HEADER_PULLDOWN);
        } else {
            mTitleText.setText(REFRESH_HEADER_PULLDOWN);
        }
    }

    @Override
    public void onUIRefreshBegin(PtrFrameLayout frame) {
        mTitleText.setText(REFRESH_HEADER_LOADING);
        mProgressView.startAnimation(mRotateAnimation);
    }

    @Override
    public void onUIRefreshComplete(PtrFrameLayout frame) {
        //mProgressView.setVisibility(INVISIBLE);
        mTitleText.setText(REFRESH_HEADER_FINISH);
        mProgressView.clearAnimation();
    }

    @Override
    public void onUIPositionChange(PtrFrameLayout frame, boolean isUnderTouch, byte status, PtrIndicator ptrIndicator) {
        float percent = ptrIndicator.getCurrentPercent();
        if (status == PtrFrameLayout.PTR_STATUS_PREPARE) {
            if(percent < 1){
                mTitleText.setText(REFRESH_HEADER_PULLDOWN);
            } else {
                mTitleText.setText(REFRESH_HEADER_RELEASE);
            }
            mProgressView.setRotation(-180*percent);
        } else if (status == PtrFrameLayout.PTR_STATUS_COMPLETE){
            mProgressView.setRotation(-180*percent);
        }


    }
}
