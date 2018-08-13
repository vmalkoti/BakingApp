package com.example.malkoti.bakingapp.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public final class ApiClient {
    private static Retrofit retrofit;

    private static Retrofit getRetrofitClient() {
        if(retrofit==null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(ApiRetrofitInterface.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return retrofit;
    }

    public static ApiRetrofitInterface getApiInterface() {
        return getRetrofitClient().create(ApiRetrofitInterface.class);
    }
}
