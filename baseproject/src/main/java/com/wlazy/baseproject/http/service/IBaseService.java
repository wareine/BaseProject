package com.wlazy.baseproject.http.service;

import java.util.Map;

import io.reactivex.Flowable;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Url;

/**
 * Created by 王灿 on
 * 2016/12/5.
 */

public interface IBaseService {
    @FormUrlEncoded
    @POST()
    Flowable<ResponseBody> post(@Url String url, @FieldMap Map<String, String> params);

    @GET()
    Flowable<ResponseBody> get(@Url String url);

    @Headers({"Content-Type: application/json", "Accept: application/json"})//需要添加头
    @POST
    Flowable<ResponseBody> postJson(@Url String url, @Body RequestBody route);
}
