package com.wlazy.core.http.config;

/**
 * http默认参数
 */

public class DefaultParams {
    public static final boolean isLog = true;//默认打印网络相关日志
    public static final boolean isCookie = false;
    public static final boolean isCache = true;
    public static final boolean isSkipSSL = true;

    public static final String HOST_URL = "https://www.baidu.com/"; //默认的host url
    public static final String KEY_CACHE = "http_cache";//缓存相关的子目录

    public static final int CONNECTION_TIME_OUT = 15;
    public static final int READER_TIME_OUT = 15;
    public static final int WRITE_TIME_OUT = 10;
    public static final long DEFAULT_CACHE_MAXSIZE = 10 * 1024 * 1024;

    public static final int MAX_IDLE_CONNECTIONS = 5;
    public static final long KEEP_ALIVE_DURATION = 10;

    public static final int MAX_RETRY_COUNTS = 3;
    public static final int RETRY_DELAY_MILLIS = 3000;
}
