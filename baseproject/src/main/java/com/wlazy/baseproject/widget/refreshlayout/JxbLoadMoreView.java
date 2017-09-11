package com.wlazy.baseproject.widget.refreshlayout;

import com.chad.library.adapter.base.loadmore.LoadMoreView;
import com.wlazy.baseproject.R;


/**
 * Created by Wang on 2017/9/5.
 */

public class JxbLoadMoreView extends LoadMoreView {

    @Override public int getLayoutId() {
        return R.layout.rc_view_load_more;
    }


    @Override protected int getLoadingViewId() {
        return R.id.load_more_loading_view;
    }

    @Override protected int getLoadFailViewId() {
        return R.id.load_more_load_fail_view;
    }

    /**
     * isLoadEndGone()为true，可以返回0
     * isLoadEndGone()为false，不能返回0
     */
    @Override protected int getLoadEndViewId() {
        return 0;
    }

}
