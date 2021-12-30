package com.example.project;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiInterface {

    @GET("suggest")
    Call<suggestionModel> suggest(
            @Query("token") String token,
            @Query("q") String q
    );

    @GET("search")
    Call<searchModel> search(
            @Query("token") String token,
            @Query("q") String q,
            @Query("type") String type,
            @Query("filter") String filter
    );

    @GET("search")
    Call<searchModel> search(
            @Query("token") String token,
            @Query("q") String q,
            @Query("type") String type
    );

}
