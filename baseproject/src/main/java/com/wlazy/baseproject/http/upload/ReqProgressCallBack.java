package com.wlazy.baseproject.http.upload;

/**
 * Created by Wang on 2017/8/30.
 */

public interface ReqProgressCallBack<T>  extends ReqCallBack<T>{
    /**
     * 响应进度更新
     */
    void onProgress(long total, long current);
}
