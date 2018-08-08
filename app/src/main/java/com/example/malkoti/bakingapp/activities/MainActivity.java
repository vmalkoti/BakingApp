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
import com.example.malkoti.bakingapp.fragments.OnFragmentItemClickListener;
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
    Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewModel = ViewModelProviders.of(MainActivity.this).get(RecipeViewModel.class);
        viewModel.getSelectedRecipe().observe(MainActivity.this, recipe -> {
            updateWidget(recipe);
        });

        // Listener passed to Recipe Details fragment
        // to load Step Details fragment when a step is clicked
        OnFragmentItemClickListener stepListener = () -> {
            boolean isTwoPaneLayout = getResources().getBoolean(R.bool.twoPaneLayout);
            if(!isTwoPaneLayout) {
                loadStepDetailsFragment();
            }
        };

        // Listener passed to Recipe List fragment
        // to load Recipe Details fragment when a recipe is clicked
        OnFragmentItemClickListener recipeListener = () -> {
            loadRecipeDetailsFragment(stepListener);
        };

        // Load first fragment only if its not a config change
        if(findViewById(R.id.fragment_container) != null) {
            if(savedInstanceState==null) {
                loadRecipeListFragment(recipeListener);
            }
        }

        // When widget is clicked, Recipe object is passed in intent
        Recipe passedRecipe = getIntent().getParcelableExtra(RecipeWidgetService.RECIPE_EXTRA);
        if (passedRecipe != null) {
            viewModel.setSelectedRecipe(passedRecipe);
            loadRecipeDetailsFragment(stepListener);
        }
    }


    /**
     * Update app widgets with recipe object
     * @param recipe Recipe object to get Ingredients
     */
    private void updateWidget(Recipe recipe) {
        Context context = this;

        PreferencesUtil.savePreferences(context, recipe);

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
    }

    /**
     * Load fragment to display list of recipes
     * @param recipeClickListener Listener for recipe item clicked in list of recipes
     */
    private void loadRecipeListFragment(OnFragmentItemClickListener recipeClickListener) {
        RecipeListFragment recipeListFragment = RecipeListFragment.newInstance(recipeClickListener);
        fragment = recipeListFragment;
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container, recipeListFragment)
                .commit();
    }

    /**
     * Load fragment to display ingredients and steps
     * @param stepClickListener Listener for step item clicked in recipe details
     */
    private void loadRecipeDetailsFragment(OnFragmentItemClickListener stepClickListener) {
        final String fragmentTag = "recipe-details";

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Fragment existingFragment = getSupportFragmentManager().findFragmentByTag(fragmentTag);

        //Log.d(LOG_TAG, "Loading recipe details fragment");
        if(existingFragment==null) {
            RecipeDetailsFragment stepListFragment = RecipeDetailsFragment.newInstance(stepClickListener);
            fragment = stepListFragment;
            transaction
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(fragmentTag)
                    .commit();
            //Log.d(LOG_TAG, "Added new recipe detail fragment with backstack");
        } else {
            fragment = existingFragment;
            transaction
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            //Log.d(LOG_TAG, "Used existing recipe detail fragment");
        }
    }

    /**
     * Load fragment to display step details
     */
    private void loadStepDetailsFragment() {
        final String fragmentTag = "step-details";
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Fragment existingFragment = getSupportFragmentManager().findFragmentByTag(fragmentTag);

        //Log.d(LOG_TAG, "Loading step details fragment");
        if(existingFragment==null) {
            StepDetailsFragment stepDetailsFragment = StepDetailsFragment.newInstance();
            fragment = stepDetailsFragment;
            transaction
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(fragmentTag)
                    .commit();
            //Log.d(LOG_TAG, "Added new step detail fragment with backstack");
        } else {
            fragment = existingFragment;
            transaction
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            //Log.d(LOG_TAG, "Used existing step detail fragment");
        }
    }
}
