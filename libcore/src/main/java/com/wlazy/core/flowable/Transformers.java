package com.wlazy.core.flowable;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.trello.rxlifecycle2.LifecycleProvider;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.android.FragmentEvent;
import com.trello.rxlifecycle2.components.RxFragment;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;
import com.wlazy.core.http.config.DefaultParams;
import com.wlazy.core.http.exception.ExceptionHandle;

import org.reactivestreams.Publisher;

import java.lang.reflect.Type;

import io.reactivex.Flowable;
import io.reactivex.FlowableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

public class Transformers {

    public static <T>FlowableTransformer<T,T> schedulersBindLifeCycle(final LifecycleProvider lifecycle) {
        return new FlowableTransformer<T,T>() {
            @Override
            public Publisher<T> apply(@NonNull Flowable<T> upstream) {
                upstream = upstream.subscribeOn(Schedulers.io())
                        .unsubscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
                if(lifecycle instanceof RxAppCompatActivity) {
                    return upstream.compose(((RxAppCompatActivity)lifecycle).<T>bindUntilEvent(ActivityEvent.DESTROY));
                } else if(lifecycle instanceof RxFragment) {
                    return upstream.compose(((RxFragment)lifecycle).<T>bindUntilEvent(FragmentEvent.DESTROY));
                } else if(lifecycle instanceof com.trello.rxlifecycle2.components.support.RxFragment){
                    return upstream.compose(((com.trello.rxlifecycle2.components.support.RxFragment)lifecycle).<T>bindUntilEvent(FragmentEvent.DESTROY));
                } else{
                    return upstream;
                }
            }
        };
    }

    public static FlowableTransformer schedulersTransformer() {
        return new FlowableTransformer() {
            @Override
            public Publisher apply(@NonNull Flowable upstream) {
                return upstream.retryWhen(new RetryWithDelay(DefaultParams.MAX_RETRY_COUNTS, DefaultParams.RETRY_DELAY_MILLIS))
                        .subscribeOn(Schedulers.io())
                        .unsubscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }

    public static FlowableTransformer schedulersTransformerIOThread() {
        return new FlowableTransformer() {
            @Override
            public Publisher apply(@NonNull Flowable upstream) {
                return upstream.subscribeOn(Schedulers.io())
                        .unsubscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }


    public static FlowableTransformer schedulersTransformernewThread() {
        return new FlowableTransformer() {
            @Override
            public Publisher apply(@NonNull Flowable upstream) {
                return upstream.subscribeOn(Schedulers.newThread())
                        .unsubscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }

    public static <T> FlowableTransformer<ResponseBody, T> transformer() {
        return new FlowableTransformer<ResponseBody, T>() {
            @Override
            public Publisher<T> apply(Flowable<ResponseBody> upstream) {
                return upstream.map(new Function<ResponseBody, T>() {
                    @Override
                    public T apply(ResponseBody responseBody) throws Exception {
                        String response = responseBody.string();
                        Gson gson = new Gson();
                        Type objectType = new TypeToken<T>() {}.getType();
                        return gson.fromJson(response, objectType);
                    }
                }).onErrorResumeNext(new Function<Throwable, Publisher<? extends T>>() {
                    @Override
                    public Publisher<? extends T> apply(Throwable throwable) throws Exception {
                        return Flowable.error(ExceptionHandle.handleException(throwable));
                    }
                });
            }
        };
    }

//    public static <T> FlowableTransformer<ResponseBody,ApiResponse<T>> transformers(){
//        return new FlowableTransformer<ResponseBody, ApiResponse<T>>() {
//            @Override
//            public Publisher<ApiResponse<T>> apply(Flowable<ResponseBody> upstream) {
//                return upstream.map(new Function<ResponseBody, ApiResponse<T>>() {
//                    @Override
//                    public ApiResponse<T> apply(ResponseBody responseBody) throws Exception {
//                        String response = responseBody.string();
//                        Gson gson = new Gson();
//                        Type objectType = new TypeToken<ApiResponse<T>>() {}.getType();
//                        return gson.fromJson(response, objectType);
//                    }
//                }).onErrorResumeNext(new Function<Throwable, Publisher<? extends ApiResponse<T>>>() {
//                    @Override
//                    public Publisher<? extends ApiResponse<T>> apply(Throwable throwable) throws Exception {
//                        return Flowable.error(ExceptionHandle.handleException(throwable));
//                    }
//                });
//            }
//        };
//    }

    public static <T> FlowableTransformer<ResponseBody,T> transformers(final Class<T> cls){
        return new FlowableTransformer<ResponseBody, T>() {
            @Override
            public Publisher<T> apply(Flowable<ResponseBody> upstream) {
                return upstream.map(new Function<ResponseBody, T>() {
                    @Override
                    public T apply(ResponseBody responseBody) throws Exception {
                        String response = responseBody.string();
                        Gson gson = new Gson();
                        return gson.fromJson(response, cls);
                    }
                }).onErrorResumeNext(new Function<Throwable, Publisher<? extends T>>() {
                    @Override
                    public Publisher<? extends T> apply(Throwable throwable) throws Exception {
                        return Flowable.error(ExceptionHandle.handleException(throwable));
                    }
                });
            }
        };
    }
}
