package com.example.malkoti.bakingapp.widgets;

import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.malkoti.bakingapp.R;
import com.example.malkoti.bakingapp.model.Recipe;

/**
 * Adapter class for widget's ListView
 */
public class IngredientsWidgetRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private Context context;
    private Recipe recipe;

    public IngredientsWidgetRemoteViewsFactory(Context applicationContext, Intent intent) {
        this.context = applicationContext;
        this.recipe = intent.getParcelableExtra(IngredientsWidgetRemoteViewsService.RECIPE_EXTRA);
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
        final long identityToken = Binder.clearCallingIdentity();
        // get/set new data here
        Binder.restoreCallingIdentity(identityToken);
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return recipe==null? 0: recipe.getIngredients().size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        if(position== AdapterView.INVALID_POSITION || recipe==null) {
            return null;
        }
        RemoteViews remoteViews = new RemoteViews(this.context.getPackageName(),
                R.layout.ingredient_item);
        remoteViews.setTextViewText(R.id.recipe_ingredient_item,
                recipe.getIngredients().get(position).toString());
        return remoteViews;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }
}
