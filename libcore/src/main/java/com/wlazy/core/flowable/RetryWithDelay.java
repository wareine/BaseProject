package com.wlazy.core.flowable;

import android.util.Log;

import org.reactivestreams.Publisher;

import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.functions.Function;

public class RetryWithDelay implements Function<Flowable<? extends Throwable>, Flowable<?>> {
    private final String TAG = this.getClass().getSimpleName();

    private final int maxRetries;
    private final int retryDelayMillis;
    private int retryCount;

    public RetryWithDelay(int maxRetries, int retryDelayMillis) {
        this.maxRetries = maxRetries;
        this.retryDelayMillis = retryDelayMillis;
    }


    @Override
    public Flowable<?> apply(Flowable<? extends Throwable> flowable) throws Exception {
        return flowable.flatMap(new Function<Throwable, Publisher<?>>() {
            @Override
            public Publisher<?> apply(Throwable throwable) throws Exception {
                if (++retryCount <= maxRetries) {
                    // When this Observable calls onNext, the original Observable will be retried (i.e. re-subscribed).
                    Log.e(TAG, "getdata error, it will try after " + retryDelayMillis + " millisecond, retry count: " + retryCount);
                    return Flowable.timer(retryDelayMillis, TimeUnit.MILLISECONDS);
                }
                // Max retries hit. Just pass the error along.
                return Flowable.error(throwable);
            }
        });
    }
}