package com.wlazy.baseproject.http.upload;

/**
 * Created by Wang on 2017/8/30.
 */

public interface ReqCallBack <T> {
    /**
     * 响应成功
     */
    void onReqSuccess(T result);

    /**
     * 响应失败
     */
    void onReqFailed(String errorMsg);
}
