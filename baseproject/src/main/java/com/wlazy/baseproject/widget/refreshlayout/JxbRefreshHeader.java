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

import com.scwang.smartrefresh.layout.api.RefreshHeader;
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

public class JxbRefreshHeader extends LinearLayout implements RefreshHeader {

    public static String REFRESH_HEADER_PULLDOWN = "下拉刷新";
    public static String REFRESH_HEADER_REFRESHING = "正在刷新...";
    public static String REFRESH_HEADER_LOADING = "正在加载...";
    public static String REFRESH_HEADER_RELEASE = "释放刷新...";
    public static String REFRESH_HEADER_FINISH = "刷新完成";
    public static String REFRESH_HEADER_FAILED = "刷新失败";

    static final int ROTATION_ANIMATION_DURATION = 1200;
    private Animation mRotateAnimation;

    TextView mHeaderText;
    ImageView mProgressView;

    public JxbRefreshHeader(Context context) {
        super(context);
        initView(context);
    }

    public JxbRefreshHeader(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public JxbRefreshHeader(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context){
        this.setGravity(Gravity.CENTER);
        this.setOrientation(LinearLayout.VERTICAL);
        mHeaderText = new TextView(context);
        mHeaderText.setText(REFRESH_HEADER_PULLDOWN);
        mHeaderText.setTextColor(0xff8b8b8b);
        mHeaderText.setTextSize(13);

        LayoutParams lpProgress = new LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        mProgressView = new ImageView(context);
        mProgressView.setImageResource(R.drawable.default_ptr_rotate);
        addView(mProgressView, lpProgress);

        LayoutParams lpHeaderText = new LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        lpHeaderText.topMargin = DensityUtil.dp2px(5);
        addView(mHeaderText, lpHeaderText);

        setPadding(0,DensityUtil.dp2px(10),0,DensityUtil.dp2px(18));
        setMinimumHeight(DensityUtil.dp2px(60));

        mRotateAnimation = new RotateAnimation(0, 720, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        mRotateAnimation.setInterpolator(new LinearInterpolator());
        mRotateAnimation.setDuration(ROTATION_ANIMATION_DURATION);
        mRotateAnimation.setRepeatCount(Animation.INFINITE);
        mRotateAnimation.setRepeatMode(Animation.RESTART);
    }

    @Override
    public void onPullingDown(float percent, int offset, int headerHeight, int extendHeight) {
        mProgressView.setRotation(-180*percent);
    }

    @Override
    public void onReleasing(float percent, int offset, int headerHeight, int extendHeight) {
        mProgressView.setRotation(-180*percent);
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
        mProgressView.startAnimation(mRotateAnimation);
    }

    @Override
    public int onFinish(RefreshLayout layout, boolean success) {
        mProgressView.clearAnimation();//停止动画
        if (success){
            mHeaderText.setText(REFRESH_HEADER_FINISH);
        } else {
            mHeaderText.setText(REFRESH_HEADER_FAILED);
        }
        return 500;//延迟500毫秒之后再弹回
    }

    @Override
    public boolean isSupportHorizontalDrag() {
        return false;
    }

    @Override
    public void onStateChanged(RefreshLayout refreshLayout, RefreshState oldState, RefreshState newState) {
        switch (newState) {
            case None:
            case PullDownToRefresh:
                mHeaderText.setText(REFRESH_HEADER_PULLDOWN);
                break;
            case Refreshing:
                mHeaderText.setText(REFRESH_HEADER_LOADING);
                break;
            case ReleaseToRefresh:
                mHeaderText.setText(REFRESH_HEADER_RELEASE);
                break;
        }
    }
}
