package com.example.malkoti.bakingapp.widgets;

import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.Parcelable;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.malkoti.bakingapp.R;
import com.example.malkoti.bakingapp.model.Recipe;
import com.example.malkoti.bakingapp.utils.PreferencesUtil;

/**
 * Service class to return an instance of RemoteViewsFactory class
 */
public class RecipeWidgetService extends RemoteViewsService {
    private static final String LOG_TAG = "DEBUG_" + RecipeWidgetService.class.getSimpleName();
    public static final String RECIPE_EXTRA = "recipe";

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        Log.d(LOG_TAG, "Service invoked");
        Parcelable p = intent.getParcelableExtra(RecipeWidgetService.RECIPE_EXTRA);
        if(p!=null) {
            Recipe recipe = (Recipe) p;
            //Log.d(LOG_TAG, "recipe object in onGetViewFactory = " + recipe.getName());
        }
        //else {
            //Log.d(LOG_TAG, "no recipe object in onGetViewFactory");
        //}
        return new IngredientsWidgetRemoteViewsFactory(this.getApplicationContext(), intent);
    }


    /**
     * Adapter class for widget's ListView
     */
    public static class IngredientsWidgetRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
        private static final String LOG_TAG = "DEBUG_" + IngredientsWidgetRemoteViewsFactory.class.getSimpleName();
        private Context context;
        private Recipe recipe;

        IngredientsWidgetRemoteViewsFactory(Context applicationContext, Intent intent) {
            this.context = applicationContext;
            this.recipe = intent.getParcelableExtra(RecipeWidgetService.RECIPE_EXTRA);
            /*
            if(this.recipe!=null) {
                Log.d(LOG_TAG, "recipe object received in factory constructor =" + recipe.getName());
            } else {
                Log.d(LOG_TAG, "no recipe object received in factory constructor");
            }
            */
        }

        @Override
        public void onCreate() {
            // run when app widget is created
        }


        @Override
        public void onDataSetChanged() {
            final long identityToken = Binder.clearCallingIdentity();
            //Log.d(LOG_TAG, "Factory dataset changed");
            // get/set new data here
            this.recipe = PreferencesUtil.getPreferences(context);
            Binder.restoreCallingIdentity(identityToken);
        }

        @Override
        public void onDestroy() {
            // run when app widget is deleted
        }

        @Override
        public int getCount() {
            //int count = recipe==null? 0: recipe.getIngredients().size();
            //Log.d(LOG_TAG, "in getCount = " + count);
            return (recipe==null? 0: recipe.getIngredients().size());
        }

        @Override
        public RemoteViews getViewAt(int position) {
            if(position==AdapterView.INVALID_POSITION || recipe==null) {
                return null;
            }

            RemoteViews remoteViews = new RemoteViews(this.context.getPackageName(),
                    android.R.layout.simple_list_item_1);
            remoteViews.setTextViewText(android.R.id.text1,
                    recipe.getIngredients().get(position).toString());

            Intent fillInIntent = new Intent();
            fillInIntent.putExtra(RECIPE_EXTRA, this.recipe);
            remoteViews.setOnClickFillInIntent(android.R.id.text1, fillInIntent);

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

}
