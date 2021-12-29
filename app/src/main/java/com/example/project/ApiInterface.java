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

}
