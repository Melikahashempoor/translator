package com.example.project;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ApiUtilities {

    public static final String BASE_URL = "http://api.vajehyab.com/v3/";
    public static final String token = "68291.Spb59OnPqEhKakudnSnVOk1764fzUvzWLsNRPZZM";

    private static Retrofit retrofit = null;

    public static Retrofit getApiInterface(Context mContext) {

        if (retrofit == null) {
            Gson gson = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd HH:mm:ss")
                    .create();

            retrofit = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create(gson)).build();
        }
        return retrofit;
    }

}
