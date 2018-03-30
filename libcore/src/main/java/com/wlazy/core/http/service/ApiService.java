package com.wlazy.core.http.service;


import com.google.gson.Gson;
import com.trello.rxlifecycle2.LifecycleProvider;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.android.FragmentEvent;
import com.trello.rxlifecycle2.components.RxFragment;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;
import com.wlazy.core.http.RetrofitClient;

import org.json.JSONObject;

import java.util.Map;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

/**
 * API接口实例获取类，通过此类访问对应API
 */

public class ApiService {
    private static IBaseService sIBaseService;

    private static IBaseService getIBaseService() {
        if (sIBaseService == null) {
            sIBaseService = new RetrofitClient.Builder().hostUrl("").build().createNetService(IBaseService.class);
        }
        return sIBaseService;
    }

    public static BuildFlowable build() {
        return new BuildFlowable(null);
    }



    public static class BuildFlowable {

        private Object mLifecycle;



        public BuildFlowable(Object lifecycle) {
            mLifecycle = lifecycle;
        }

        private Flowable<ResponseBody> bindLifeCycle(Flowable<ResponseBody> flowable) {
            flowable = flowable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread());
            if(mLifecycle instanceof RxAppCompatActivity) {
                return flowable.compose(((RxAppCompatActivity)mLifecycle).<ResponseBody>bindUntilEvent(ActivityEvent.DESTROY));
            } else if(mLifecycle instanceof RxFragment) {
                return flowable.compose(((RxFragment)mLifecycle).<ResponseBody>bindUntilEvent(FragmentEvent.DESTROY));
            } else if(mLifecycle instanceof com.trello.rxlifecycle2.components.support.RxFragment){
                return flowable.compose(((com.trello.rxlifecycle2.components.support.RxFragment)mLifecycle).<ResponseBody>bindUntilEvent(FragmentEvent.DESTROY));
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
