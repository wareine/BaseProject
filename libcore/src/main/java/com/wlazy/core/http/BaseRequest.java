package com.wlazy.core.http;

import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.wlazy.core.flowable.Transformers;
import com.wlazy.core.http.service.IBaseService;

import java.util.Map;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

public abstract class BaseRequest<T> implements Function<ResponseBody, T> {

    private IBaseService baseService;

    public BaseRequest(String baseUrl) {
        baseService = buildClient(baseUrl).createNetService(IBaseService.class);
    }

    protected RetrofitClient buildClient(String baseUrl){
        //可以重载此方法 修改相关参数
        return new RetrofitClient.Builder().hostUrl(baseUrl).build();
    }

    public Flowable reqPost(final String func, @Nullable final Map<String, String> params) {
        if(this.baseService == null) {
            throw new IllegalStateException("未初始化IBaseService");
        } else {
            return this.baseService.postFunc(func, buildRequestBody(params)).map(this).compose(Transformers.schedulersTransformer());
        }
    }

    public <K> Flowable reqPost(final String func, K h) {
        if(this.baseService == null) {
            throw new IllegalStateException("未初始化IBaseService");
        } else {
            return this.baseService.postFunc(func, buildRequestBody(new Gson().toJson(h))).map(this).compose(Transformers.schedulersTransformer());
        }
    }

    protected RequestBody buildRequestBody(@Nullable final Map<String, String> params){
        //此处可以加一些公共参数
        return buildRequestBody(new Gson().toJson(params));
    }

    protected RequestBody buildRequestBody(@Nullable final String json){
        //此处可以加一些公共参数
        return RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);
    }

}
