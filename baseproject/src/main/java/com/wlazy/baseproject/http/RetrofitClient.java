package com.wlazy.baseproject.http;


import android.text.TextUtils;

import com.wlazy.baseproject.BaseApplication;
import com.wlazy.baseproject.http.interceptor.CacheInterceptor;
import com.wlazy.baseproject.http.interceptor.LoggingInterceptor;

import java.io.File;
import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * User: fee(lifei@cloudtone.com.cn)
 * Date: 2016-05-16
 * Time: 16:19
 * DESC: 基于Retrofit的网络请求客户端
 * retrofit2官网地址：https://github.com/square/retrofit/
 */
public class RetrofitClient {
    private static final long DEFAULT_TIME_OUT = 12;

    private static RetrofitClient retrofitClient;
    private Retrofit mRetrofit;
    /**
     * 使用之前先配置一下要访问的服务器的基础地址
     */
    public static String HOST_BASE_URL = "https://github.com/square/retrofit/";
    private WeakHashMap<Call,Integer> cachedCalls;
    private final Object syncLockObj = new Object();
    //added
    private Retrofit.Builder retrofitBuilder;
    private RetrofitClient() {
        if (mRetrofit == null) {
            File cacheDir = new File(BaseApplication.getApplication().getCacheDir(), "http");
            Cache cache = new Cache(cacheDir, 10 * 1024 * 1024);

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.connectTimeout(DEFAULT_TIME_OUT, TimeUnit.SECONDS);
            builder.readTimeout(DEFAULT_TIME_OUT, TimeUnit.SECONDS);
            builder.writeTimeout(DEFAULT_TIME_OUT, TimeUnit.SECONDS);
            builder.addNetworkInterceptor(new CacheInterceptor());
            builder.addInterceptor(new CacheInterceptor());
            builder.addInterceptor(new LoggingInterceptor());

            builder.cache(cache);
            //builder.cookieJar(new CookieManager(App.getApplication()));
            OkHttpClient client = builder.build();

            retrofitBuilder = new Retrofit.Builder();
            mRetrofit = retrofitBuilder.baseUrl(HOST_BASE_URL)
                    .client(client)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
    }

    /**
     * 如果有自定义的ConverterFactory，则依设置进来的对象重新构建Retrofit
     *
     * @param baseUrl
     * @param converterFactory
     */
    public void initRetrofitModule(String baseUrl, Converter.Factory converterFactory) {
        if (TextUtils.isEmpty(baseUrl)) {
            return;
        }
        if (retrofitBuilder != null) {
            mRetrofit = retrofitBuilder.baseUrl(baseUrl).addConverterFactory(converterFactory).build();
        }
        else{
            OkHttpClient.Builder httpBuilder = new OkHttpClient.Builder();
            httpBuilder.retryOnConnectionFailure(true)//失败重连
                    .readTimeout(DEFAULT_TIME_OUT, TimeUnit.SECONDS)
                    .writeTimeout(DEFAULT_TIME_OUT, TimeUnit.SECONDS)
                    .connectTimeout(DEFAULT_TIME_OUT, TimeUnit.SECONDS);
            mRetrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(httpBuilder.build())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(converterFactory)
                .build();
        }
    }

    /**
     * 重置自定义的OkHttpClient客户端对象
     * @param customOkHttpClient
     * @return
     */
    public RetrofitClient resetOkHttpClient(OkHttpClient customOkHttpClient) {
        if (retrofitBuilder != null) {
            mRetrofit = retrofitBuilder.callFactory(customOkHttpClient).build();
        }
        return this;
    }

    public Retrofit.Builder getRetrofitBuilder() {
        return this.retrofitBuilder;
    }

    public void resetRetrofit() {
        if (this.retrofitBuilder != null) {
            mRetrofit = this.retrofitBuilder.build();
        }
    }
    public static RetrofitClient getInstanse() {
//        if (Util.isEmpty(HOST_BASE_URL)) {
//            throw new NullPointerException("please config the host base url first");
//        }
        if (retrofitClient == null) {
            synchronized (RetrofitClient.class) {
                if (retrofitClient == null) {
                    retrofitClient = new RetrofitClient();
                }
            }
        }
        return retrofitClient;
    }

    /**
     * 通过Retrofit 创建访问服务器的接口
     *
     * @param iServiceClass
     * @param <T>
     * @return
     */
    public <T> T createNetService(Class<T> iServiceClass) {
        if (mRetrofit != null) {
            return mRetrofit.create(iServiceClass);
        }
        return null;
    }

    /**
     * 缓存当前的一个网络请求
     * @param curCallRequest 当前网络请求
     * @param curRequestType 网络请求类型
     */
    public void cacheCall(Call curCallRequest,int curRequestType) {
        if (cachedCalls == null) {
            cachedCalls = new WeakHashMap<>();
        }
        synchronized (syncLockObj) {
            cachedCalls.put(curCallRequest,curRequestType);
        }
    }

    /**
     * 取消对应的一个网络请求
     * @param callRequestType 要对应取消的网络请求类型
     */
    public void cancelCall(int callRequestType) {
        if (cachedCalls == null || cachedCalls.isEmpty()) {
            return;
        }
        Call toCancelCall = null;
        synchronized (syncLockObj) {
            Iterator entrySetIterator = cachedCalls.entrySet().iterator();
            while (entrySetIterator.hasNext()) {
                Map.Entry<Call,Integer> entry = (Map.Entry<Call, Integer>) entrySetIterator.next();
                int curValue = entry.getValue();
                if (curValue == callRequestType) {
                    toCancelCall = entry.getKey();
                    break;
                }
            }
            if (toCancelCall != null) {
                cachedCalls.remove(toCancelCall);
                toCancelCall.cancel();
            }
        }
    }

    /**
     * 取消所以添加过的网络请求
     */
    public void cancelAllCall() {
        if (cachedCalls == null || cachedCalls.isEmpty()) {
            return;
        }
        synchronized (syncLockObj) {
            Iterator<Call> callIterator = cachedCalls.keySet().iterator();
            while (callIterator.hasNext()) {
                Call curCall = callIterator.next();
                if (curCall != null) {
                    curCall.cancel();
                }
            }
            cachedCalls.clear();
        }
    }
}
