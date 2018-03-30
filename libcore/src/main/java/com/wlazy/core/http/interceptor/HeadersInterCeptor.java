package com.wlazy.core.http.interceptor;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by dingjinzhu on 2018/3/1.
 * 自定义基础拦截器，使用map键值对进行构建，可以用于设置通用请求头，Header
 */

public class HeadersInterCeptor implements Interceptor{

    private Map<String, String> headers;

    public HeadersInterCeptor(Map<String, String> headers) {
        this.headers = headers;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request.Builder builder = chain.request().newBuilder();
        if (headers != null && headers.size() > 0) {
            Set<String> keys = headers.keySet();
            for (String key : keys) {
                builder.addHeader(key, headers.get(key));
            }
        }

        return chain.proceed(builder.build());
    }
}
