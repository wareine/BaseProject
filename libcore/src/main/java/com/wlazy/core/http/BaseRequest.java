package com.wlazy.core.http;

import android.support.annotation.Nullable;

import com.wlazy.core.flowable.Transformers;
import com.wlazy.core.http.service.IBaseService;

import java.util.Map;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

public abstract class BaseRequest<T> implements Function<ResponseBody, T> {

    private IBaseService baseService;

    public BaseRequest(String baseUrl) {
        baseService = new RetrofitClient.Builder().hostUrl(baseUrl).build().createNetService(IBaseService.class);
    }

    public Flowable<T> reqPost(final String func, @Nullable final Map<String, String> param) {
        if(this.baseService == null) {
            throw new IllegalStateException("未初始化IBaseService");
        } else {
            return this.baseService.postFunc(func, buildParms(param)).map(this).subscribeOn(Schedulers.io()).compose(Transformers.schedulersTransformer());
        }
    }

    abstract protected RequestBody buildParms(@Nullable final Map<String, String> param);

}
