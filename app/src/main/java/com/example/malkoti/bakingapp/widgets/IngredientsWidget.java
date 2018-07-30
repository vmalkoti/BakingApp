package com.example.malkoti.bakingapp.widgets;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Parcelable;
import android.util.Log;
import android.widget.RemoteViews;

import com.example.malkoti.bakingapp.R;
import com.example.malkoti.bakingapp.model.Recipe;

/**
 * Implementation of App Widget functionality.
 * http://www.vogella.com/tutorials/AndroidWidgets/article.html
 */
public class IngredientsWidget extends AppWidgetProvider {
    private static final String LOG_TAG = "DEBUG_" + IngredientsWidget.class.getSimpleName();
    private Recipe recipe;

    /*
    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        CharSequence widgetText = context.getString(R.string.appwidget_text);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.ingredients_widget);
        views.setTextViewText(R.id.appwidget_text, widgetText);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }
    */

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            Intent intent = new Intent(context, WidgetService.class);
            if(recipe!=null) {
                Log.d(LOG_TAG, "recipe object in onUpdate = " + recipe.getId());
            } else {
                Log.d(LOG_TAG, "no recipe object in onUpdate");
            }
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            intent.putExtra(WidgetService.RECIPE_EXTRA, recipe);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.ingredients_widget);
            views.setRemoteAdapter(R.id.recipe_ingredients_list, intent);

            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Parcelable p = intent.getParcelableExtra(WidgetService.RECIPE_EXTRA);
        if(p!=null) {
            this.recipe = (Recipe) p;
            Log.d(LOG_TAG, "recipe object in onReceive = " + recipe.getName());
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

