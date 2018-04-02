package com.wlazy.core.http;


import com.blankj.utilcode.util.ObjectUtils;
import com.blankj.utilcode.util.Utils;
import com.wlazy.core.http.config.DefaultParams;
import com.wlazy.core.http.cookie.CookieManger;
import com.wlazy.core.http.interceptor.CacheInterceptor;
import com.wlazy.core.http.interceptor.HeadersInterCeptor;
import com.wlazy.core.http.ssl.DefaultHttpsFactroy;

import java.io.File;
import java.net.InetAddress;
import java.net.Proxy;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSocketFactory;

import okhttp3.Cache;
import okhttp3.CertificatePinner;
import okhttp3.ConnectionPool;
import okhttp3.Dns;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * DESC: 基于Retrofit的网络请求客户端
 * retrofit2官网地址：https://github.com/square/retrofit/
 */
public class RetrofitClient {

    private Retrofit retrofit;
    private WeakHashMap<String,Object> baseServices;

    private RetrofitClient(final Builder builder) {

        //构建OkHttpClient
        OkHttpClient.Builder okHttpBuilder = new OkHttpClient.Builder();
        if(ObjectUtils.isNotEmpty(builder.headers)){
            okHttpBuilder.addInterceptor(new HeadersInterCeptor(builder.headers));
        }
        if (builder.isCookie) {
            okHttpBuilder.cookieJar(new CookieManger());
        }
        if (builder.isLog) {
            okHttpBuilder.addNetworkInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY));
        }
        if (builder.isCache) {
            File cacheDir = new File(Utils.getApp().getCacheDir(), DefaultParams.KEY_CACHE);
            Cache cache = new Cache(cacheDir, DefaultParams.DEFAULT_CACHE_MAXSIZE);
            okHttpBuilder.addInterceptor(new CacheInterceptor())
                    .addNetworkInterceptor(new CacheInterceptor())
                    .cache(cache);
        }
        okHttpBuilder.writeTimeout(builder.mWriteTimeOut, TimeUnit.SECONDS)
                        .readTimeout(builder.mReaderTimeOut, TimeUnit.SECONDS)
                        .connectTimeout(builder.mConnectTimeOut, TimeUnit.SECONDS)
                        .connectionPool(new ConnectionPool(DefaultParams.MAX_IDLE_CONNECTIONS, DefaultParams.KEEP_ALIVE_DURATION, TimeUnit.SECONDS));
        if(builder.connectionPool != null) {
            okHttpBuilder.connectionPool(builder.connectionPool);
        }
        if(builder.proxy != null) {
            okHttpBuilder.proxy(builder.proxy);
        }
        if (builder.dns != null) {
            okHttpBuilder.dns(new Dns() {
                @Override
                public List<InetAddress> lookup(String hostname) throws UnknownHostException {
                    List<InetAddress> host = builder.dns.lookup(hostname);
                    if (host != null) {
                        return host;
                    }
                    return Dns.SYSTEM.lookup(hostname);
                }
            });
        }
        if (builder.isSkipSSL) {
            okHttpBuilder.sslSocketFactory(DefaultHttpsFactroy.getSSLSocketFactory(), DefaultHttpsFactroy.creatX509TrustManager());
            okHttpBuilder.hostnameVerifier(DefaultHttpsFactroy.creatSkipHostnameVerifier());
        } else {
            if (builder.sslSocketFactory != null) {
                okHttpBuilder.sslSocketFactory(builder.sslSocketFactory);
            }
            if (builder.hostnameVerifier != null) {
                okHttpBuilder.hostnameVerifier(builder.hostnameVerifier);
            }
        }
        if (builder.certificatePinner != null) {
            okHttpBuilder.certificatePinner(builder.certificatePinner);
        }
        if (builder.sslHosts != null) {
            okHttpBuilder.hostnameVerifier(DefaultHttpsFactroy.getHostnameVerifier(builder.sslHosts));
        }
        if (builder.sslCertificates != null) {
            okHttpBuilder.sslSocketFactory(DefaultHttpsFactroy.getSSLSocketFactory(Utils.getApp(), builder.sslCertificates));
        }


        //构建Retrofit
        retrofit = new Retrofit.Builder().baseUrl(builder.hostUrl)
                .addConverterFactory(builder.converterFactory == null ? GsonConverterFactory.create():builder.converterFactory)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())//rxjava2支持
                .client(okHttpBuilder.build())
                .build();

    }



    /**
     * 通过Retrofit 创建访问服务器的接口
     *
     * @param iServiceClass
     * @param <T>
     * @return
     */
    public <T> T createNetService(Class<T> iServiceClass) {
        if (retrofit == null) {
            retrofit = defaultRetrofit("https://github.com/square/retrofit/");

        }
        return retrofit.create(iServiceClass);
    }

    private static OkHttpClient defaultOkHttpClient() {
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
        clientBuilder.connectTimeout(DefaultParams.CONNECTION_TIME_OUT, TimeUnit.SECONDS);
        clientBuilder.readTimeout(DefaultParams.READER_TIME_OUT, TimeUnit.SECONDS);
        clientBuilder.writeTimeout(DefaultParams.WRITE_TIME_OUT, TimeUnit.SECONDS);
        return clientBuilder.build();
    }

    private static Retrofit defaultRetrofit(String baseUrl) {
        return new Retrofit.Builder().baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())//gson转换器
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())//rxjava2支持
                .client(defaultOkHttpClient())
                .build();
    }



    public static class Builder {
        String hostUrl = DefaultParams.HOST_URL;
        boolean isLog = DefaultParams.isLog;
        boolean isCookie = DefaultParams.isCookie;
        boolean isCache = DefaultParams.isCache;
        boolean isSkipSSL = DefaultParams.isSkipSSL;
        Map<String, String> headers;
        int mWriteTimeOut = DefaultParams.WRITE_TIME_OUT;
        int mReaderTimeOut = DefaultParams.READER_TIME_OUT;
        int mConnectTimeOut = DefaultParams.CONNECTION_TIME_OUT;
        ConnectionPool connectionPool;
        Proxy proxy = null;
        Dns dns = new Dns() {
            @Override
            public List<InetAddress> lookup(String hostname) throws UnknownHostException {
                return null;
            }
        };
        SSLSocketFactory sslSocketFactory;
        HostnameVerifier hostnameVerifier;
        CertificatePinner certificatePinner;
        String[] sslHosts;
        int[] sslCertificates;

        String url;//request请求 url 参数
        Map<String, String> mapParam = new HashMap<>();//request请求的map参数
        Converter.Factory converterFactory;


        /**
         * 设置base url
         */
        public Builder hostUrl(String hostUrl) {
            this.hostUrl = hostUrl;
            return this;
        }

        public Builder converterFactory(Converter.Factory converterFactory) {
            this.converterFactory = converterFactory;
            return this;
        }

        /**
         * 增加通用请求头header之外的header
         */
        public Builder addHeader(Map<String, String> headers) {
            this.headers = headers;
            return this;
        }


        /**
         * add cookieJar
         */
        public Builder addCookie(boolean isCookie) {
            this.isCookie = isCookie;

            return this;
        }

        public Builder addLog(boolean isLog) {
            this.isLog = isLog;

            return this;
        }

        public Builder addCache(boolean isCache) {
            this.isCache = isCache;

            return this;
        }


        public Builder setMapParam(Map<String, String> mapParam) {
            if (mapParam != null) {
                this.mapParam = mapParam;
            }
            return this;
        }

        public Builder setWriteTimeOut(int timeOut) {
            this.mWriteTimeOut = timeOut;

            return this;
        }

        public Builder setReaderTimeOut(int timeOut) {
            this.mReaderTimeOut = timeOut;

            return this;
        }

        public Builder setConnectionTimeOut(int timeOut) {
            this.mConnectTimeOut = timeOut;

            return this;
        }

        private Builder connectionPool(int maxIdleConnections, long keepAliveDuration) {
            if (connectionPool == null) {
                connectionPool = new ConnectionPool(maxIdleConnections, keepAliveDuration, TimeUnit.SECONDS);
            }
            return this;
        }

        public Builder proxy(Proxy proxy) {
            this.proxy = proxy;
            if (proxy != null) {
                //如果设置了代理，则httpdns会失败
                String host = System.getProperty("http.proxyHost");
                String port = System.getProperty("http.proxyPort");
                if (host != null && port != null) {
                    dns = null;
                }
            }
            return this;
        }

        public Builder httpDns(Dns dns) {
            this.dns = dns;
            return this;
        }

        public Builder addSSLSocketFactory(boolean isSkipSSL, SSLSocketFactory sslSocketFactory, HostnameVerifier hostnameVerifier) {
            this.isSkipSSL = isSkipSSL;
            this.sslSocketFactory = sslSocketFactory;
            this.hostnameVerifier = hostnameVerifier;

            return this;
        }

        /**
         * 用于证书锁定
         */
        public Builder addCertificatePinner(CertificatePinner certificatePinner) {
            this.certificatePinner = certificatePinner;

            return this;
        }

        public Builder addSSL(String[] hosts, int[] certificates) {
            this.sslHosts = hosts;
            this.sslCertificates = certificates;
            return this;
        }

        public RetrofitClient build() {

            return new RetrofitClient(this);
        }
    }

}
