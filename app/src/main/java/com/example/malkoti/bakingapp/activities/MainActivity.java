package com.example.malkoti.bakingapp.activities;

import android.appwidget.AppWidgetManager;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.malkoti.bakingapp.R;
import com.example.malkoti.bakingapp.data.RecipeViewModel;
import com.example.malkoti.bakingapp.fragments.RecipeListFragment;
import com.example.malkoti.bakingapp.fragments.StepDetailsFragment;
import com.example.malkoti.bakingapp.fragments.RecipeDetailsFragment;
import com.example.malkoti.bakingapp.model.Recipe;
import com.example.malkoti.bakingapp.utils.PreferencesUtil;
import com.example.malkoti.bakingapp.widgets.IngredientsWidget;
import com.example.malkoti.bakingapp.widgets.RecipeWidgetService;


public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = "DEBUG_" + MainActivity.class.getSimpleName();

    private RecipeViewModel viewModel;
    private int numGridColumns = 1;

    Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewModel = ViewModelProviders.of(MainActivity.this).get(RecipeViewModel.class);

        viewModel.getSelectedRecipe().observe(MainActivity.this, recipe -> {
            //if(savedInstanceState!=null) return;

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            Fragment existingFragment = getSupportFragmentManager().findFragmentByTag("recipe-details");

            if(existingFragment==null) {
                RecipeDetailsFragment stepListFragment = RecipeDetailsFragment.newInstance();
                fragment = stepListFragment;
                transaction
                        .replace(R.id.fragment_container, fragment)
                        .addToBackStack("recipe-detail")
                        .commit();
            } else {
                fragment = existingFragment;
                transaction
                        .replace(R.id.fragment_container, fragment)
                        .commit();
            }

            // update widget with selected recipe ingredients
            updateWidget(recipe);
        });

        viewModel.getSelectedStep().observe(MainActivity.this, step -> {
            //if(savedInstanceState!=null) return;

            boolean isTwoPaneLayout = getResources().getBoolean(R.bool.twoPaneLayout);
            if(!isTwoPaneLayout) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                Fragment existingFragment = getSupportFragmentManager().findFragmentByTag("step-details");

                if(existingFragment==null) {
                    StepDetailsFragment stepDetailsFragment = StepDetailsFragment.newInstance();
                    fragment = stepDetailsFragment;
                    transaction
                            .replace(R.id.fragment_container, fragment)
                            .addToBackStack("step-details")
                            .commit();
                } else {
                    fragment = existingFragment;
                    transaction
                            .replace(R.id.fragment_container, fragment)
                            .commit();
                }

            }
        });

        if(findViewById(R.id.fragment_container) != null) {
            if(savedInstanceState!=null) return;

            RecipeListFragment recipeListFragment = RecipeListFragment.newInstance();
            fragment = recipeListFragment;
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragment_container, recipeListFragment)
                    .commit();
        }

        Recipe passedRecipe = getIntent().getParcelableExtra(RecipeWidgetService.RECIPE_EXTRA);
        if(passedRecipe != null) {
            viewModel.setSelectedRecipe(passedRecipe);
        }

    }

    @Override
    public void onBackPressed() {
        if(getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
        getSupportFragmentManager().beginTransaction().remove(fragment).commit();
    }

    /**
     * Update app widgets with recipe object
     * @param recipe Recipe object to get Ingredients
     */
    private void updateWidget(Recipe recipe) {
        Context context = this;
        Intent intent = new Intent(context, IngredientsWidget.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        intent.putExtra(RecipeWidgetService.RECIPE_EXTRA, recipe);
        int[] ids = AppWidgetManager
                .getInstance(context)
                .getAppWidgetIds(
                        new ComponentName(context, IngredientsWidget.class)
                );
        //Log.d(LOG_TAG, "recipe object sent in intent = " + recipe.getName());
        if(ids!=null && ids.length>0) {
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
            context.sendBroadcast(intent);
            //Log.d(LOG_TAG, "Broadcast sent");
        }

        PreferencesUtil.savePreferences(context, recipe);
    }
}
