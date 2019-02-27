package com.eblasting.Connect;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ConnectApi {
    @FormUrlEncoded
    @POST("yzb_cjy_updata")
    Observable<ResponseBody> login(@Field("key") String key,
                                   @Field("dataType") String dataType,
                                   @Field("jsonData") String jsonData);

    @FormUrlEncoded
    @POST("yzb_cjy_updata")
    Observable<ResponseBody> online(@Field("key") String key,
                                    @Field("dataType") String dataType);

    @FormUrlEncoded
    @POST("yzb_cjy_updata")
    Observable<ResponseBody> testNet(@Field("key") String key,
                                     @Field("dataType") String dataType);


    @FormUrlEncoded
    @POST("yzb_cjy_updata")
    Observable<ResponseBody> queryPersonInfo(@Field("key") String key,
                                             @Field("dataType") String dataType,
                                             @Field("jsonData") String jsonData);

    @FormUrlEncoded
    @POST("yzb_cjy_updata")
    Observable<ResponseBody> personInfo(@Field("key") String key,
                                        @Field("dataType") String dataType,
                                        @Field("jsonData") String jsonData);


    @FormUrlEncoded
    @POST("yzb_cjy_updata")
    Observable<ResponseBody> work(@Field("key") String key,
                                  @Field("dataType") String dataType,
                                  @Field("jsonData") String jsonData);

    @FormUrlEncoded
    @POST("yzb_cjy_updata")
    Observable<ResponseBody> workRecord(@Field("key") String key,
                                        @Field("dataType") String dataType,
                                        @Field("jsonData") String jsonData);

    @FormUrlEncoded
    @POST("yzb_cjy_updata")
    Observable<ResponseBody> checkDevice(@Field("key") String key,
                                         @Field("dataType") String dataType,
                                         @Field("jsonData") String jsonData);
}

