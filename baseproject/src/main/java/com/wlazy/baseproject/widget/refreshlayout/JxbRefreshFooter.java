package com.wlazy.baseproject.widget.refreshlayout;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshKernel;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.util.DensityUtil;
import com.wlazy.baseproject.R;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * Created by Wang on 2017/9/5.
 */

public class JxbRefreshFooter extends LinearLayout implements RefreshFooter {
    public static String REFRESH_FOOTER_PULLUP = "上拉加载更多";
    public static String REFRESH_FOOTER_RELEASE = "释放立即加载";
    public static String REFRESH_FOOTER_LOADING = "正在加载...";
    public static String REFRESH_FOOTER_REFRESHING = "正在刷新...";
    public static String REFRESH_FOOTER_FINISH = "加载完成";
    public static String REFRESH_FOOTER_FAILED = "加载失败";
    public static String REFRESH_FOOTER_ALLLOADED = "全部加载完成";

    static final int ROTATION_ANIMATION_DURATION = 1200;
    private Animation mRotateAnimation;

    TextView mTitleText;
    ImageView mProgressView;
    protected boolean mLoadmoreFinished = false;
    public JxbRefreshFooter(Context context) {
        super(context);
        initView(context);
    }

    public JxbRefreshFooter(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public JxbRefreshFooter(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context){
        this.setGravity(Gravity.CENTER);
        this.setOrientation(LinearLayout.HORIZONTAL);
        mTitleText = new TextView(context);
        mTitleText.setText(REFRESH_FOOTER_PULLUP);
        mTitleText.setTextColor(0xff8b8b8b);
        mTitleText.setTextSize(12);

        LayoutParams lpProgress = new LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        mProgressView = new ImageView(context);
        mProgressView.setImageResource(R.drawable.default_ptr_rotate);
        addView(mProgressView, lpProgress);

        LayoutParams lpHeaderText = new LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        lpHeaderText.leftMargin = DensityUtil.dp2px(10);
        addView(mTitleText, lpHeaderText);

        setMinimumHeight(DensityUtil.dp2px(40));

        mRotateAnimation = new RotateAnimation(0, 720, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        mRotateAnimation.setInterpolator(new LinearInterpolator());
        mRotateAnimation.setDuration(ROTATION_ANIMATION_DURATION);
        mRotateAnimation.setRepeatCount(Animation.INFINITE);
        mRotateAnimation.setRepeatMode(Animation.RESTART);
    }

    @Override
    public void onPullingUp(float percent, int offset, int footerHeight, int extendHeight) {
        if (!mLoadmoreFinished) {
            mProgressView.setRotation(180*percent);
        }
    }

    @Override
    public void onPullReleasing(float percent, int offset, int footerHeight, int extendHeight) {
        if (!mLoadmoreFinished) {
            mProgressView.setRotation(180*percent);
        }
    }

    @Override
    public boolean setLoadmoreFinished(boolean finished) {
        if (mLoadmoreFinished != finished) {
            mLoadmoreFinished = finished;
            if (finished) {
                mTitleText.setText(REFRESH_FOOTER_ALLLOADED);
            } else {
                mTitleText.setText(REFRESH_FOOTER_PULLUP);
            }
            mProgressView.clearAnimation();
            mProgressView.setVisibility(GONE);
        }
        return true;
    }

    @NonNull
    @Override
    public View getView() {
        return this;
    }

    @Override
    public SpinnerStyle getSpinnerStyle() {
        return SpinnerStyle.Translate;
    }

    @Override
    public void setPrimaryColors(int... colors) {

    }

    @Override
    public void onInitialized(RefreshKernel kernel, int height, int extendHeight) {

    }

    @Override
    public void onHorizontalDrag(float percentX, int offsetX, int offsetMax) {

    }

    @Override
    public void onStartAnimator(RefreshLayout layout, int height, int extendHeight) {
        if (!mLoadmoreFinished) {
            mProgressView.setVisibility(VISIBLE);
            mProgressView.startAnimation(mRotateAnimation);
        }
    }

    @Override
    public int onFinish(RefreshLayout layout, boolean success) {
        if (!mLoadmoreFinished) {
            mProgressView.clearAnimation();
            mProgressView.setVisibility(GONE);
            if (success) {
                mTitleText.setText(REFRESH_FOOTER_FINISH);
            } else {
                mTitleText.setText(REFRESH_FOOTER_FAILED);
            }
            return 500;
        }
        return 0;
    }

    @Override
    public boolean isSupportHorizontalDrag() {
        return false;
    }

    @Override
    public void onStateChanged(RefreshLayout refreshLayout, RefreshState oldState, RefreshState newState) {
        if (!mLoadmoreFinished) {
            switch (newState) {
                case None:
                case PullToUpLoad:
                    mProgressView.setVisibility(VISIBLE);
                    mTitleText.setText(REFRESH_FOOTER_PULLUP);
                    break;
                case Loading:
                    mTitleText.setText(REFRESH_FOOTER_LOADING);
                    break;
                case ReleaseToLoad:
                    mTitleText.setText(REFRESH_FOOTER_RELEASE);
                    break;
                case Refreshing:
                    mTitleText.setText(REFRESH_FOOTER_REFRESHING);
                    mProgressView.setVisibility(GONE);
                    break;
            }
        }
    }
}
