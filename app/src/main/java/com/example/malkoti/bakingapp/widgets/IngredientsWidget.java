package com.example.malkoti.bakingapp.widgets;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.widget.RemoteViews;

import com.example.malkoti.bakingapp.R;
import com.example.malkoti.bakingapp.model.Recipe;

/**
 * Implementation of App Widget functionality.
 * http://www.vogella.com/tutorials/AndroidWidgets/article.html
 */
public class IngredientsWidget extends AppWidgetProvider {
    private Recipe recipe;

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        CharSequence widgetText = context.getString(R.string.appwidget_text);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.ingredients_widget);
        views.setTextViewText(R.id.appwidget_text, widgetText);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.ingredients_widget);
            Intent intent = new Intent(context, IngredientsWidgetRemoteViewsService.class);
            intent.putExtra(IngredientsWidgetRemoteViewsService.RECIPE_EXTRA, recipe);
            views.setRemoteAdapter(R.id.recipe_ingredients_list, intent);
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Parcelable p = intent.getParcelableExtra(IngredientsWidgetRemoteViewsService.RECIPE_EXTRA);
        if(p!=null) {
            this.recipe = (Recipe) p;
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

