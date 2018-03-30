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

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.FlowableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

/**
 * Flowable 相关
 */

public class FlowableCreator {


    public static <T>Flowable<T> create(final OnFlowableRun<T> bb){
        return Flowable.create(new FlowableOnSubscribe<T>() {

            @Override
            public void subscribe(FlowableEmitter<T> e) throws Exception {
                T result = bb.run();
                if(result != null){
                    e.onNext(result);
                }
                e.onComplete();
            }
        }, BackpressureStrategy.ERROR) //指定背压处理策略，抛出异常
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public interface OnFlowableRun<T>{
        T run();
    }


    public static <T>Flowable<T> createEmptyFlowable(){
        return Flowable.create(new FlowableOnSubscribe<T>() {

            @Override
            public void subscribe(FlowableEmitter<T> e) throws Exception {
                e.onComplete();
            }
        }, BackpressureStrategy.ERROR) //指定背压处理策略，抛出异常
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }



    public static <T>Flowable<T> bindLifeCycle(LifecycleProvider mLifecycle, Flowable<T> flowable) {
        flowable = flowable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        if (mLifecycle != null) {
            if(mLifecycle instanceof RxAppCompatActivity) {
                return flowable.compose(((RxAppCompatActivity)mLifecycle).<T>bindUntilEvent(ActivityEvent.DESTROY));
            } else if(mLifecycle instanceof RxFragment) {
                return flowable.compose(((RxFragment)mLifecycle).<T>bindUntilEvent(FragmentEvent.DESTROY));
            } else if( mLifecycle instanceof com.trello.rxlifecycle2.components.support.RxFragment) {
                return flowable.compose(((com.trello.rxlifecycle2.components.support.RxFragment)mLifecycle).<T>bindUntilEvent(FragmentEvent.DESTROY));
            }
        }
        return flowable;
    }

    public static <T>Flowable<T> bindLifeCycle(Object object, Flowable<T> flowable) {
        if (object != null && object instanceof LifecycleProvider) {
            return bindLifeCycle((LifecycleProvider) object, flowable);
        } else {
            return flowable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());
        }
    }
}
