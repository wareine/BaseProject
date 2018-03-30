package com.wlazy.core.http.interceptor;


import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.blankj.utilcode.util.NetworkUtils;

import java.io.IOException;

import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by ZHT on 2016/12/25.
 *
 * OkHttp缓存拦截器
 * 只能缓存get请求，不能缓存post请求
 */

public class CacheInterceptor implements Interceptor {

    private String TAG = CacheInterceptor.class.getSimpleName();

    final int maxStale = 60 * 60 * 24 * 3;


    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        if (NetworkUtils.isConnected()) {
            Response response = chain.proceed(request);
            // read from cache for 60 s
            int maxAge = 60;
            String cacheControl = request.cacheControl().toString();
            Log.d(TAG, "60s load cahe" + cacheControl);
            return response.newBuilder()
                    .removeHeader("Pragma")
                    .removeHeader("Cache-Control")
                    .header("Cache-Control", "public, max-age=" + maxAge)
                    .build();
        } else {
//            ((Activity) context).runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    Toast.makeText(context, "当前无网络! 为你智能加载缓存", Toast.LENGTH_SHORT).show();
//                }
//            });
            Log.d(TAG, " no network load cahe");
            request = request.newBuilder()
                    .cacheControl(CacheControl.FORCE_CACHE)
                    .build();
            Response response = chain.proceed(request);
            return response.newBuilder()
                    .removeHeader("Pragma")
                    .removeHeader("Cache-Control")
                    .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                    .build();
        }
    }
}
