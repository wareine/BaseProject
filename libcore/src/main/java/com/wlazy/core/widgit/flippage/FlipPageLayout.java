package com.wlazy.core.widgit.flippage;
 

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;

/**
 * 商品详情页面的上拉显示详情页面实现
 */
public class FlipPageLayout extends ViewGroup {
	private final String TAG = "FlipPageLayout";
	
    private VelocityTracker mVelocityTracker;
    private int mMaximumVelocity;
    private static final int SNAP_VELOCITY = 1000;
    
    public static final int FLIP_DIRECTION_CUR = 0;
	public static final int FLIP_DIRECTION_UP = -1;
	public static final int FLIP_DIRECTION_DOWN = 1;
	
	private int mFlipDrection = FLIP_DIRECTION_CUR;

	
	private Scroller mScroller;
	// 记录了上次鼠标按下时的XY值，在ACTION_MOVE中赋值；
	private float mLastMotionX;
	private float mLastMotionY;
	// 记录触摸状态
	private final static int TOUCH_STATE_REST = 0;
	private final static int TOUCH_STATE_SCROLLING = 1;
	private int mTouchState = TOUCH_STATE_REST;
	public static int mDefaultSlop;
	public static int mTouchSlop; // 这个值表示需要滑动多少距离的时候才翻到下一页

	private int mDataIndex = 0; // 当前View中的数据在总数据所在位置
	private int mCurrentScreen = 0;
	private int mNextDataIndex = 0;
	
	private boolean isAddFinish = false;

    private OnClickListener mOnClickListener;
    private ViewGroup fatherView;
    
    private IFlipPage mPageTop, mPageBottom;
    
    private IFlipListener mFlipListener;
    
	/**
	 * Used to inflate the Workspace from XML.
	 */
	public FlipPageLayout(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	/**
	 * Used to inflate the Workspace from XML.
	 */
	public FlipPageLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView();
	}

	private void initView() {
		mScroller = new Scroller(getContext());

		final ViewConfiguration configuration = ViewConfiguration
				.get(getContext());
		mDefaultSlop = configuration.getScaledTouchSlop();
		mTouchSlop = mDefaultSlop;
		mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
	}
	
	/**
	 * 设置上下页面
	 * @param pageTop
	 * @param pageBottom
	 */
	public void setFlipPage(IFlipPage pageTop, IFlipPage pageBottom){
		mPageTop = pageTop;
		mPageBottom = pageBottom;
		showPage();
	}
	
	private void showPage(){
		//设置页面id
		mPageTop.getRootView().setId(0);
		mPageBottom.getRootView().setId(1);
		addView(mPageTop.getRootView());
		addView(mPageBottom.getRootView());
		postInvalidate();
	}

	@Override
	public void computeScroll() {
		if (mScroller.computeScrollOffset()) {
			if ((isAddFinish)
					&& (mScroller.getCurrY() == (mScroller.getFinalY()))) {
				if (mNextDataIndex > mDataIndex) {
				    mFlipDrection = FLIP_DIRECTION_DOWN;
					scrollToNext(mNextDataIndex);
				} else if (mNextDataIndex < mDataIndex) {
				    mFlipDrection = FLIP_DIRECTION_UP;
					scrollToPrev(mNextDataIndex);
				}else{
				    mFlipDrection = FLIP_DIRECTION_CUR;
				}
				if(mFlipListener != null){
					mFlipListener.onFlipCompleted(mFlipDrection);
				}
				isAddFinish = false;
			}
			scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
			postInvalidate();
		}
	}
	
	public void setFatherView(ViewGroup view){
		fatherView = view;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * 绘制ViewGroup的View
	 */
	@Override
	public void dispatchDraw(Canvas canvas) {
		final long drawingTime = getDrawingTime();
		// If we are flinging, draw only the current screen and the target
		// screen
		
			// If we are scrolling, draw all of our children
		final int count = getChildCount();
		try {
			for (int i = 0; i < count; i++) {
				drawChild(canvas, getChildAt(i), drawingTime);
			}
		} catch (Exception e) {
//			if(Log.E) {
				e.printStackTrace();
//			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View#onMeasure(int, int)
	 * 重写了父类的onMeasure()，主要功能是设置屏幕的显示大小。由每个child的measure()方法设置
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
	    
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		final int width = MeasureSpec.getSize(widthMeasureSpec);
		final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		// 仅当ViewGroup为fill_parent才处于EXACTLY模式
//		if (widthMode != MeasureSpec.EXACTLY) {
//			throw new IllegalStateException(
//					"Workspace can only be used in EXACTLY mode.");
//		}

		final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
//		if (heightMode != MeasureSpec.EXACTLY) {
//			throw new IllegalStateException(
//					"Workspace can only be used in EXACTLY mode.");
//		}

		// The children are given the same width and height as the workspace
		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
			getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.ViewGroup#onLayout(boolean, int, int, int, int)
	 * 重写了父类的onLayout()，主要功能是设置屏幕的显示位置。由child的layout()方法设置
	 */
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		int childTop = 0;
		int count = getChildCount();
//		 DLog.i(TAG, "onLayout mDataIndex = " + mDataIndex);
		// 设置布局，将子视图顺序竖屏排列
		for (int i = 0; i < count; i++) {
			final View child = getChildAt(i);
			if (child.getVisibility() != View.GONE) {
				final int childWidth = child.getMeasuredWidth();
				final int childHeight = child.getMeasuredHeight();
				childTop = childHeight * i;
				child.layout(0, childTop, childWidth,
						childTop + childHeight);
			}
		}
		if(count > 0){
			snapToScreen(mDataIndex);
		}
	}

	@Override
	public boolean dispatchUnhandledMove(View focused, int direction) {
		if (direction == View.FOCUS_LEFT) {
			if (getCurrentScreen() > 0) {
				snapToScreen(getCurrentScreen() - 1);
				return true;
			}
		} else if (direction == View.FOCUS_RIGHT) {
			if (getCurrentScreen() < getChildCount() - 1) {
				snapToScreen(getCurrentScreen() + 1);
				return true;
			}
		}
		return super.dispatchUnhandledMove(focused, direction);
	}

	public int getCurrentScreen() {
		for (int i = 0; i < getChildCount(); i++) {
			if (getChildAt(i).getId() == mDataIndex) {
				return i;
			}
		}
		return mCurrentScreen;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.view.ViewGroup#onInterceptTouchEvent(android.view.MotionEvent)
	 * 重写了父类的onInterceptTouchEvent()，主要功能是在onTouchEvent()方法之前处理
	 * touch事件。包括：down、up、move事件。
	 * 当onInterceptTouchEvent()返回true时进入onTouchEvent()。
	 */
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		final int action = ev.getAction();
		if ((action == MotionEvent.ACTION_MOVE)
				&& (mTouchState != TOUCH_STATE_REST)) {
			return true;
		}
		final float x = ev.getX();
		final float y = ev.getY();

		switch (action) {
		case MotionEvent.ACTION_MOVE:
//			Log.i(TAG, "onInterceptTouchEvent ACTION_MOVE");
			/*
			 * 记录xy与mLastMotionX、mLastMotionY差值的绝对值。xDiff和yDiff大于
			 * touchSlop时就认为界面拖动了足够大的距离，屏幕就可以移动了。
			 */
			final int yDiff = (int)(y - mLastMotionY);
			final int touchSlop = mTouchSlop;
			boolean yMoved = Math.abs(yDiff) > touchSlop;
			if (yMoved) {
				if(yDiff < 0 && mPageTop.isFlipToBottom() && mCurrentScreen == 0 
						|| yDiff > 0 && mPageBottom.isFlipToTop() && mCurrentScreen == 1){
					mTouchState = TOUCH_STATE_SCROLLING;
					if(fatherView != null){
						fatherView.requestDisallowInterceptTouchEvent(true);
					}
				}
			}
			break;
		case MotionEvent.ACTION_DOWN:
			// Remember location of down touch
			mLastMotionX = x;
			mLastMotionY = y;
			mTouchState = mScroller.isFinished() ? TOUCH_STATE_REST
					: TOUCH_STATE_SCROLLING;
			break;
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
//			Log.i(TAG, "onInterceptTouchEvent ACTION_UP");
			// Release the drag
			mTouchState = TOUCH_STATE_REST;
			break;
		}
		return mTouchState != TOUCH_STATE_REST;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View#onTouchEvent(android.view.MotionEvent)
	 * 主要功能是处理onInterceptTouchEvent()返回值为true时传递过来的touch事件
	 */
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
	    if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(ev);
        
		final int action = ev.getAction();
		final float x = ev.getX();
		final float y = ev.getY();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
//			Log.i(TAG, "onTouchEvent ACTION_DOWN");
			if (!mScroller.isFinished()) {
				mScroller.abortAnimation();
			}
			// Remember where the motion event started
			mLastMotionX = x;
			break;
		case MotionEvent.ACTION_MOVE:
//			Log.i(TAG, "onTouchEvent ACTION_MOVE");
		    if(mTouchState != TOUCH_STATE_SCROLLING){
                /*
                 * 记录xy与mLastMotionX、mLastMotionY差值的绝对值。xDiff和yDiff大于
                 * touchSlop时就认为界面拖动了足够大的距离，屏幕就可以移动了。
                 */
                //final int xDiff = (int) Math.abs(x - mLastMotionX);
                final int yDiff = (int) Math.abs(y - mLastMotionY);
                final int touchSlop = mTouchSlop;
                //boolean xMoved = (xDiff > touchSlop);
                boolean yMoved = yDiff > touchSlop;
                if (yMoved) {
                        // Scroll if the user moved far enough along the X axis
                	mTouchState = TOUCH_STATE_SCROLLING;
                }
            }
            // 手指拖动屏幕的处理
            if ((mTouchState == TOUCH_STATE_SCROLLING) && ((!isAddFinish))) {
            	if(fatherView != null){
        			fatherView.requestDisallowInterceptTouchEvent(true);
        		}
                // Scroll to follow the motion event
                final int deltaY = (int) (mLastMotionY - y);
                mLastMotionY = y;
                final int scrollY = getScrollY();
                if(mCurrentScreen == 0){//显示第一张图，只能上拉时使用
                	if(mPageTop.isFlipToBottom()){
                		scrollBy(0, Math.max(-1 * scrollY, deltaY));
                	}
                }else{
                	if(mPageBottom.isFlipToTop()){
                		 scrollBy(0, deltaY);
                	}
                }
            }
			break;
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
//			Log.i(TAG, "onTouchEvent ACTION_UP");
			// 弹起手指后，切换屏幕的处理
			if (mTouchState == TOUCH_STATE_SCROLLING) {
			    final VelocityTracker velocityTracker = mVelocityTracker;
                velocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
                int velocityY = (int) velocityTracker.getYVelocity();
                if (Math.abs(velocityY) > SNAP_VELOCITY) {
                    if( velocityY > 0 && mCurrentScreen == 1 && mPageBottom.isFlipToTop()){
                        snapToScreen(mDataIndex-1);
                    }else if(velocityY < 0  && mCurrentScreen == 0){
                        snapToScreen(mDataIndex+1);
                    }else{
                        snapToScreen(mDataIndex);
                    }
                } else {
                    snapToDestination();
                }
                if (mVelocityTracker != null) {
                    mVelocityTracker.recycle();
                    mVelocityTracker = null;
                }
			}else{
//			    DLog.i(TAG, "onTouchEvent ACTION_UP  on Click");
                if(mOnClickListener != null){
                    mOnClickListener.onClick(this);
                }
			}
			mTouchState = TOUCH_STATE_REST;
			break;
		
		default:
			break;
		}
		return true;
	}
	
	private void clearOnTouchEvents(){
		mTouchState = TOUCH_STATE_REST;
		 if (mVelocityTracker != null) {
             mVelocityTracker.recycle();
             mVelocityTracker = null;
         }
	}

    private void snapToDestination() {
		// 计算应该去哪个屏
		final int flipHeight = getHeight() / 8;
        
        int whichScreen = -1;
        final int topEdge = getCurrentView().getTop();

        if(topEdge < getScrollY() && (getScrollY()-topEdge) >= flipHeight && mCurrentScreen == 0){
            //向左滑动    
            whichScreen = mDataIndex + 1;
        }else if(topEdge > getScrollY() && (topEdge - getScrollY()) >= flipHeight && mCurrentScreen == 1){
            //向右滑动
            whichScreen = mDataIndex - 1;
        }else{
            whichScreen = mDataIndex;
        }
//        Log.i(TAG, "snapToDestination mDataIndex = " + mDataIndex);
//    	Log.i(TAG, "snapToDestination whichScreen = " + whichScreen);
        snapToScreen(whichScreen);
	}
    
    private void snapToScreen(int dataIndex) {
        if (!mScroller.isFinished())
            return;
        isAddFinish = true;
       
        final int dirction = dataIndex - mDataIndex;
//		Log.i(TAG, "snapToScreen dataIndex1 = " + dataIndex);
//		Log.i(TAG, "snapToScreen mDataIndex1 = " + mDataIndex);
        mNextDataIndex = dataIndex;
        boolean changingScreens = dataIndex != mDataIndex;
        View focusedChild = getFocusedChild();
        if (focusedChild != null && changingScreens) {
            focusedChild.clearFocus();
        }
        //在这里判断是否已到目标位置~
        int newY = 0;
        switch (dirction) {
            case 1:
            	newY = getCurrentView().getBottom(); // 最终停留的位置
                break;
            case -1:
            	newY =  getCurrentView().getTop() - getHeight(); // 最终停留的位置
                break;
            case 0:
            	newY = getCurrentView().getTop(); // 最终停留的位置
                break;
            default:
                break;
        }
        final int cy = getScrollY(); // 启动的位置
        final int delta = newY - cy; // 滑动的距离，正值是往左滑<—，负值是往右滑—>
        mScroller.startScroll(0, cy, 0, delta, Math.abs(delta));
        invalidate();
    }
	
	private void scrollToNext(int dataIndex) {
		mDataIndex = dataIndex;
        mCurrentScreen = getCurrentScreen();
	}

	private void scrollToPrev(int dataIndex) {
		mDataIndex = dataIndex;
        mCurrentScreen = getCurrentScreen();
	}

	public View getCurrentView() {
		for (int i = 0; i < getChildCount(); i++) {
			if (getChildAt(i).getId() == mDataIndex) {
				return getChildAt(i);
			}
		}
		return null;
	}

    public void snapToPrev(){
    	if(mCurrentScreen == 1){
    		 snapToScreen(0);
    	}
    }
    
    public void snapToNext(){
        if(mCurrentScreen == 0){
            snapToScreen(1);
        }
    }
    
    public void snapToCurrent(){
        snapToScreen(mCurrentScreen);
        clearOnTouchEvents();
    }
    
    public void setFlipListener(IFlipListener listener){
    	mFlipListener = listener;
    }
}
