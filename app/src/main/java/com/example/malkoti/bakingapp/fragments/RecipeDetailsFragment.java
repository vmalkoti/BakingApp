package com.example.malkoti.bakingapp.fragments;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.malkoti.bakingapp.R;
import com.example.malkoti.bakingapp.adapters.IngredientsAdapter;
import com.example.malkoti.bakingapp.adapters.StepsAdapter;
import com.example.malkoti.bakingapp.data.RecipeViewModel;
import com.example.malkoti.bakingapp.model.Recipe;


/**
 * Fragment class
 * to show details of the selected Recipe object
 */
public class RecipeDetailsFragment extends Fragment {
    private static final String LOG_TAG = RecipeDetailsFragment.class.getSimpleName();

    private StepsAdapter.OnStepItemClickListener mListener;
    private RecipeViewModel recipeViewModel;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RecipeDetailsFragment() { }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static RecipeDetailsFragment newInstance() {
        RecipeDetailsFragment fragment = new RecipeDetailsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipe_details, container, false);

        Context context = view.getContext();

        // Ingredients RecyclerView
        RecyclerView recipeIngredientsRecyclerView = view.findViewById(R.id.recipe_ingredients);
        LayoutManager recipeIngredientsLayoutManager = new LinearLayoutManager(context);
        recipeIngredientsRecyclerView.setLayoutManager(recipeIngredientsLayoutManager);
        IngredientsAdapter ingredientsAdapter = new IngredientsAdapter();
        recipeIngredientsRecyclerView.setAdapter(ingredientsAdapter);

        // Steps RecyclerView
        RecyclerView recipeStepsRecyclerView = view.findViewById(R.id.recipe_steps);
        LayoutManager recipeStepsLayoutManager = new LinearLayoutManager(context);
        recipeStepsRecyclerView.setLayoutManager(recipeStepsLayoutManager);
        StepsAdapter stepsAdapter = new StepsAdapter(mListener);
        recipeStepsRecyclerView.setAdapter(stepsAdapter);

        /*
        ListView recipeIngredients = view.findViewById(R.id.recipe_ingredients);
        */

        recipeViewModel = ViewModelProviders.of(getActivity()).get(RecipeViewModel.class);
        recipeViewModel.getSelectedRecipe().observe(RecipeDetailsFragment.this, recipe -> {
            stepsAdapter.setData(recipe.getSteps());
            /*
            ArrayAdapter<Recipe.Ingredient> ingredientsAdapter =
                    new ArrayAdapter<>(context,
                        android.R.layout.simple_list_item_1, recipe.getIngredients());
            recipeIngredients.setAdapter(ingredientsAdapter);
            */
            ingredientsAdapter.setData(recipe.getIngredients());
        });
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        mListener = (step) -> {
            recipeViewModel.setSelectedStep(step);
        };

    }

}
