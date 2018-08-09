package com.example.malkoti.bakingapp.widgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Parcelable;
import android.util.Log;
import android.widget.RemoteViews;

import com.example.malkoti.bakingapp.R;
import com.example.malkoti.bakingapp.activities.MainActivity;
import com.example.malkoti.bakingapp.model.Recipe;
import com.example.malkoti.bakingapp.utils.PreferencesUtil;

/**
 * Implementation of App Widget functionality.
 * http://www.vogella.com/tutorials/AndroidWidgets/article.html
 */
public class IngredientsWidget extends AppWidgetProvider {
    private static final String LOG_TAG = "DEBUG_" + IngredientsWidget.class.getSimpleName();
    private Recipe recipe;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.ingredients_widget);
            int id = 0;

            Recipe savedRecipe = PreferencesUtil.getPreferences(context);
            if(savedRecipe!=null) {
                Log.d(LOG_TAG, "recipe object in onUpdate = " + savedRecipe.getName());
                views.setTextViewText(R.id.appwidget_recipe_name, savedRecipe.getName());
            } else {
                Log.d(LOG_TAG, "no recipe object in onUpdate");
            }

            Intent serviceIntent = new Intent(context, RecipeWidgetService.class);
            serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            serviceIntent.putExtra(RecipeWidgetService.RECIPE_EXTRA, recipe);
            //serviceIntent.setData(Uri.parse(serviceIntent.toUri(Intent.URI_INTENT_SCHEME)));
            views.setRemoteAdapter(R.id.recipe_ingredients_list, serviceIntent);

            Intent clickIntent = new Intent(context, MainActivity.class);
            PendingIntent appPendingIntent = PendingIntent.getActivity(context,
                    0, clickIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setPendingIntentTemplate(R.id.recipe_ingredients_list, appPendingIntent);

            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.recipe_ingredients_list);
            appWidgetManager.updateAppWidget(appWidgetId, null);
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Parcelable p = intent.getParcelableExtra(RecipeWidgetService.RECIPE_EXTRA);
        if(p!=null) {
            this.recipe = (Recipe) p;
            Log.d(LOG_TAG, "recipe object in onReceive = " + recipe.getName());
        } else {
            Log.d(LOG_TAG, "no recipe object in onReceive");
        }
        super.onReceive(context, intent);
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

