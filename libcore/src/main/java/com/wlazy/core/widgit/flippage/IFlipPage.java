/**
 * 
 */
package com.wlazy.core.widgit.flippage;

import android.view.View;


public interface IFlipPage {
	/**
	 * 返回page根节点
	 * @return
	 */
	 View getRootView();
	 
	 /**
	  * 是否滑动到最顶端
	  */
	 boolean isFlipToTop();
	 
	 /**
	  * 是否滑动到最底部
	  */
	 boolean isFlipToBottom();
}
