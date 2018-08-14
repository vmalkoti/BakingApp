package com.example.malkoti.bakingapp.network;



import android.arch.lifecycle.LiveData;
import android.util.Log;

import com.example.malkoti.bakingapp.model.Recipe;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public final class NetworkUtils {
    private static final String LOG_TAG = NetworkUtils.class.getSimpleName();

    public static LiveData<List<Recipe>> getRecipes() {
        Call<List<Recipe>> call = ApiClient.getApiInterface().getRecipes();
        return new RetrofitLiveData(call);
    }

    /**
     *
     */
    static class RetrofitLiveData extends LiveData<List<Recipe>> implements Callback<List<Recipe>> {
        private static final String LOG_TAG = RetrofitLiveData.class.getSimpleName();
        private Call<List<Recipe>> call;

        RetrofitLiveData(Call<List<Recipe>> call) {
            this.call = call;
        }

        /* LiveData method */
        @Override
        protected void onActive() {
            if(!this.call.isCanceled() && !this.call.isExecuted()) {
                this.call.enqueue(this);
            }
        }

        /* Retrofit methods */
        @Override
        public void onResponse(Call<List<Recipe>> call, Response<List<Recipe>> response) {
            setValue(response.body());
        }

        @Override
        public void onFailure(Call<List<Recipe>> call, Throwable t) {
            Log.e(LOG_TAG, "ERROR: " + t.getMessage());
        }
    }
}
