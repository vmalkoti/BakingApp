package com.example.malkoti.bakingapp.widgets;

import android.content.Intent;
import android.widget.RemoteViewsService;

/**
 * Service class to return an instance of RemoteViewsFactory class
 */
public class IngredientsWidgetRemoteViewsService extends RemoteViewsService {
    public static final String RECIPE_EXTRA = "recipe";

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new IngredientsWidgetRemoteViewsFactory(this.getApplicationContext(), intent);
    }
}
