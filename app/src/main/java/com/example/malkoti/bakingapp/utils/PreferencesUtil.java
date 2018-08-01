package com.example.malkoti.bakingapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.malkoti.bakingapp.model.Recipe;
import com.google.gson.Gson;

public class PreferencesUtil {
    private static final String PREF_NAME = "prefs-selected-recipe";
    private static final String PREF_KEY = "recipe";

    public static void savePreferences(Context context, Recipe recipe) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(recipe);
        editor.putString(PREF_KEY, json);
        editor.commit();
    }

    public static Recipe getPreferences(Context context) {
        Gson gson = new Gson();
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String json = preferences.getString(PREF_KEY, "");
        Recipe recipe = gson.fromJson(json, Recipe.class);

        return recipe;
    }
}
