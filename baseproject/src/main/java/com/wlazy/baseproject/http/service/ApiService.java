package com.wlazy.baseproject.http.service;


import com.google.gson.Gson;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.android.FragmentEvent;
import com.trello.rxlifecycle2.components.RxFragment;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;
import com.wlazy.baseproject.http.RetrofitClient;

import org.json.JSONObject;

import java.util.Map;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

/**
 * Created by 王灿 on 2016/12/19.
 * API接口实例获取类，通过此类访问对应API
 */

public class ApiService {
    private static IBaseService sIBaseService;

    private static IBaseService getIBaseService() {
        if (sIBaseService == null) {
            sIBaseService = RetrofitClient.getInstanse().createNetService(IBaseService.class);
        }
        return sIBaseService;
    }

    public static BuildFlowable build(RxAppCompatActivity rxAppCompatActivity) {
        return new BuildFlowable(rxAppCompatActivity);
    }

    public static BuildFlowable build(RxFragment rxFragment) {
        return new BuildFlowable(rxFragment);
    }

    public static BuildFlowable build(com.trello.rxlifecycle2.components.support.RxFragment v4Fragment){
        return new BuildFlowable(v4Fragment);
    }

    public static class BuildFlowable {
        private RxAppCompatActivity mRxAppCompatActivity;
        private RxFragment mRxFragment;
        private com.trello.rxlifecycle2.components.support.RxFragment mV4Fragment;

        public BuildFlowable(RxAppCompatActivity rxAppCompatActivity) {
            mRxAppCompatActivity = rxAppCompatActivity;
        }

        public BuildFlowable(RxFragment rxFragment) {
            mRxFragment = rxFragment;
        }

        public BuildFlowable(com.trello.rxlifecycle2.components.support.RxFragment v4Fragment){
            mV4Fragment = v4Fragment;
        }

        private Flowable<ResponseBody> bindLifeCycle(Flowable<ResponseBody> flowable) {
            flowable = flowable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());
            if (mRxAppCompatActivity != null) {
                return flowable.compose(mRxAppCompatActivity.<ResponseBody>bindUntilEvent(ActivityEvent.DESTROY));
            } else if (mRxFragment != null) {
                return flowable.compose(mRxFragment.<ResponseBody>bindUntilEvent(FragmentEvent.DESTROY));
            } else if (mV4Fragment != null){
                return flowable.compose(mV4Fragment.<ResponseBody>bindUntilEvent(FragmentEvent.DESTROY));
            }
            return flowable;
        }

        public Flowable<ResponseBody> post(String url, Map<String, String> params) {
            return bindLifeCycle(getIBaseService().post(url, params));
        }

        public Flowable<ResponseBody> get(String url) {
            return get(url, null);
        }

        public Flowable<ResponseBody> get(String url, Map<String, String> params) {
            final StringBuilder builder = new StringBuilder(url);
            if (params != null) {
                if (!url.endsWith("?")){
                    builder.append("?");
                }
                Flowable.fromIterable(params.entrySet()).subscribe(
                        new Consumer<Map.Entry<String, String>>() {
                            @Override
                            public void accept(Map.Entry<String, String> entry) throws Exception {
                                if (!builder.toString().endsWith("?")){
                                    builder.append("&");
                                }
                                builder.append(entry.getKey()+"="+entry.getValue());
                            }
                        });
            }
            return bindLifeCycle(getIBaseService().get(builder.toString()));
        }

        public Flowable<ResponseBody> postJson(String url, Map<String,String> body) {
            return bindLifeCycle(getIBaseService().postJson(url, getRequestBody(body)));
        }

        public Flowable<ResponseBody> postJson(String url, JSONObject body) {
            return bindLifeCycle(getIBaseService().postJson(url, getRequestBody(body)));
        }

        private RequestBody getRequestBody(Map<String,String> map){
            return RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8")
                    ,new Gson().toJson(map));
        }

        private RequestBody getRequestBody(JSONObject json){
           return RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8")
                   ,json.toString());
        }
    }
}
