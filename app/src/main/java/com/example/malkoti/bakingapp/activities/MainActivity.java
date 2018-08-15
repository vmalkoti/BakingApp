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


public class MainActivity extends AppCompatActivity
        implements RecipeListFragment.RecipeListItemClickListener,
        RecipeDetailsFragment.RecipeDetailStepClickListener {
    private static final String LOG_TAG = "DEBUG_" + MainActivity.class.getSimpleName();
    private static final String SELECTED_RECIPE_KEY = "selected-recipe";
    private static final String SELECTED_STEP_KEY = "selected-step";

    private RecipeViewModel viewModel;
    Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewModel = ViewModelProviders.of(MainActivity.this).get(RecipeViewModel.class);
        viewModel.getSelectedRecipe().observe(MainActivity.this, this::updateWidget);

        /*
         * These listener objects are no longer needed
         *
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
        OnFragmentItemClickListener recipeListener = () -> loadRecipeDetailsFragment();
        */

        // Load first fragment only if its not a config change
        if(findViewById(R.id.fragment_container) != null) {
            if(savedInstanceState==null) {
                //loadRecipeListFragment(recipeListener);
                loadRecipeListFragment();
            } else {
                // restore selected recipe and step
                Recipe retrievedRecipe = savedInstanceState.getParcelable(SELECTED_RECIPE_KEY);
                Recipe.Step retrievedStep = savedInstanceState.getParcelable(SELECTED_STEP_KEY);

                if(retrievedRecipe != null) {
                    viewModel.setSelectedRecipe(retrievedRecipe);
                }

                if(retrievedStep != null) {
                    viewModel.setSelectedStep(retrievedStep);
                }
                /*
                // to handle null listeners after rotation
                Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                if(fragment instanceof RecipeListFragment) {
                    ((RecipeListFragment) fragment).setClickListener(recipeListener);
                } else if(fragment instanceof RecipeDetailsFragment) {
                    ((RecipeDetailsFragment) fragment).setClickListener(stepListener);
                }
                */
            }
        }

        // When widget is clicked, Recipe object is passed in intent
        Recipe passedRecipe = getIntent().getParcelableExtra(RecipeWidgetService.RECIPE_EXTRA);
        if (passedRecipe != null) {
            viewModel.setSelectedRecipe(passedRecipe);
            if(savedInstanceState==null) {
                //loadRecipeDetailsFragment(stepListener);
                loadRecipeDetailsFragment();
            }
        }

    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // save selected recipe and step
        outState.putParcelable(SELECTED_RECIPE_KEY, viewModel.getSelectedRecipe().getValue());
        outState.putParcelable(SELECTED_STEP_KEY, viewModel.getSelectedStep().getValue());
    }


    /**
     * Implemented method - RecipeListFragment.RecipeListItemClickListener
     */
    public void onRecipeItemClicked() {
        /*
        OnFragmentItemClickListener stepListener = () -> {
            boolean isTwoPaneLayout = getResources().getBoolean(R.bool.twoPaneLayout);
            if(!isTwoPaneLayout) {
                loadStepDetailsFragment();
            }
        };
        loadRecipeDetailsFragment(stepListener);
        */
        loadRecipeDetailsFragment();
    }

    /**
     * Implemented method - RecipeDetailsFragment.RecipeDetailStepClickListener
     */
    public void onRecipeStepClicked() {
        boolean isTwoPaneLayout = getResources().getBoolean(R.bool.twoPaneLayout);
        if(!isTwoPaneLayout) {
            loadStepDetailsFragment();
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
        //intent.putExtra(RecipeWidgetService.RECIPE_EXTRA, recipe);
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
     */
    private void loadRecipeListFragment() {
        RecipeListFragment recipeListFragment = RecipeListFragment.newInstance();
        fragment = recipeListFragment;
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container, recipeListFragment)
                .commit();
    }

    /**
     * Load fragment to display ingredients and steps
     */
    private void loadRecipeDetailsFragment() {
        final String fragmentTag = "recipe-details";

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Fragment existingFragment = getSupportFragmentManager().findFragmentByTag(fragmentTag);

        if(existingFragment==null) {
            RecipeDetailsFragment stepListFragment = RecipeDetailsFragment.newInstance();
            fragment = stepListFragment;
            transaction
                    .replace(R.id.fragment_container, stepListFragment)
                    .addToBackStack(fragmentTag)
                    .commit();
        } else {
            fragment = existingFragment;
            RecipeDetailsFragment stepListFragment = (RecipeDetailsFragment) existingFragment;
            //stepListFragment.setClickListener(stepClickListener);
            transaction
                    .replace(R.id.fragment_container, stepListFragment)
                    .commit();
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
                    .replace(R.id.fragment_container, stepDetailsFragment)
                    .addToBackStack(fragmentTag)
                    .commit();
        } else {
            fragment = existingFragment;
            transaction
                    .replace(R.id.fragment_container, fragment)
                    .commit();
        }
    }
}
