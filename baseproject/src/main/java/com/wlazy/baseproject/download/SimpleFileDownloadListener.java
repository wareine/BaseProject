package com.wlazy.baseproject.download;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.wlazy.baseproject.utils.LogUtils;

/**
 * Created by Wang on 2017/8/25.
 */

public abstract class SimpleFileDownloadListener extends FileDownloadListener {
    private static final String TAG = "SimpleFileDownloadListener";

    @Override
    protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
    }

    @Override
    protected void connected(BaseDownloadTask task, String etag, boolean isContinue, int soFarBytes, int totalBytes) {
    }

    @Override
    protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
        int percent = (int) (((float) soFarBytes / totalBytes) * 100);
        updateProgress(task,percent);
    }

    protected void updateProgress(BaseDownloadTask task,int percent) {

    }

    @Override
    protected void blockComplete(BaseDownloadTask task) {
    }

    @Override
    protected void retry(final BaseDownloadTask task, final Throwable ex, final int retryingTimes, final int soFarBytes) {
    }

    @Override
    protected abstract void completed(BaseDownloadTask task);

    @Override
    protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
    }

    @Override
    protected void error(BaseDownloadTask task, Throwable e) {
        LogUtils.e(TAG, task.getFilename() + "下载失败:" + e.getMessage());
        e.printStackTrace();
    }

    @Override
    protected void warn(BaseDownloadTask task) {
    }
}
