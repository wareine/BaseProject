package com.wlazy.core.http.service;

import java.util.Map;

import io.reactivex.Flowable;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Streaming;
import retrofit2.http.Url;


public interface IBaseService {
    @FormUrlEncoded
    @POST()
    Flowable<ResponseBody> post(@Url String url, @FieldMap Map<String, String> params);

    @GET()
    Flowable<ResponseBody> get(@Url String url);

    @Headers({"Content-Type: application/json", "Accept: application/json"})//需要添加头
    @FormUrlEncoded
    @POST
    Flowable<ResponseBody> postJson(@Url String url, @Body RequestBody route);

    /**
     * 下载文件，返回数据流
     */
    @Streaming
    @GET
    Flowable<ResponseBody> downloadFile(@Url String fileUrl);


    @Multipart
    @POST("{url}")
    Flowable<ResponseBody> upLoadFile(
            @Path("url") String url,
            @Part("image\"; filename=\"image.jpg") RequestBody requestBody);

    @POST("{url}")
    Call<ResponseBody> uploadFiles(
            @Path("url") String url,
            @Path("headers") Map<String, String> headers,
            @Part("filename") String description,
            @PartMap() Map<String, RequestBody> maps);
}
