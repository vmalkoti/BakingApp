package com.example.malkoti.bakingapp.fragments;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.malkoti.bakingapp.R;
import com.example.malkoti.bakingapp.adapters.IngredientsAdapter;
import com.example.malkoti.bakingapp.adapters.StepsAdapter;
import com.example.malkoti.bakingapp.data.RecipeViewModel;
import com.example.malkoti.bakingapp.model.Recipe;


/*
 * TODO: Use DataBinding to get views
 */
/**
 * Fragment class
 * to show details of the selected Recipe object
 */
public class RecipeDetailsFragment extends Fragment {
    private static final String LOG_TAG = "DEBUG_" + RecipeDetailsFragment.class.getSimpleName();

    private IngredientsAdapter ingredientsAdapter;
    private StepsAdapter stepsAdapter;
    private StepsAdapter.OnStepItemClickListener adapterItemListener;

    private RecipeViewModel recipeViewModel;
    private OnFragmentItemClickListener fragmentClickListener;

    private TextView recipeName;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RecipeDetailsFragment() { }

    @SuppressWarnings("unused")
    public static RecipeDetailsFragment newInstance(OnFragmentItemClickListener listener) {
        RecipeDetailsFragment fragment = new RecipeDetailsFragment();
        fragment.fragmentClickListener = listener;
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

        recipeName = view.findViewById(R.id.recipe_name_header);

        // Ingredients RecyclerView
        RecyclerView recipeIngredientsRecyclerView = view.findViewById(R.id.recipe_ingredients);
        LayoutManager recipeIngredientsLayoutManager = new LinearLayoutManager(context);
        recipeIngredientsRecyclerView.setLayoutManager(recipeIngredientsLayoutManager);
        ingredientsAdapter = new IngredientsAdapter();
        recipeIngredientsRecyclerView.setAdapter(ingredientsAdapter);

        // Steps RecyclerView
        RecyclerView recipeStepsRecyclerView = view.findViewById(R.id.recipe_steps);
        LayoutManager recipeStepsLayoutManager = new LinearLayoutManager(context);
        recipeStepsRecyclerView.setLayoutManager(recipeStepsLayoutManager);
        stepsAdapter = new StepsAdapter(adapterItemListener);
        recipeStepsRecyclerView.setAdapter(stepsAdapter);

        recipeViewModel = ViewModelProviders.of(getActivity()).get(RecipeViewModel.class);
        recipeViewModel.getSelectedRecipe().observe(RecipeDetailsFragment.this, recipe -> {
            loadRecipeDetails(recipe);
        });


        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setRetainInstance(true);
        // when fragment is recreated after a config change
        // observer doesn't fire onChanged because data hasn't changed
        // so check and load details here
        Recipe selectedRecipe = recipeViewModel.getSelectedRecipe().getValue();
        if(selectedRecipe != null) {
            //Log.d(LOG_TAG, "Loading recipe details in onActivityCreated");
            loadRecipeDetails(selectedRecipe);
        } else {
            //Log.d(LOG_TAG, "No recipe object yet available in in onActivityCreated");
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        adapterItemListener = (step) -> {
            recipeViewModel.setSelectedStep(step);
            fragmentClickListener.onClick();
        };

    }

    /**
     *
     * @param recipe
     */
    private void loadRecipeDetails(Recipe recipe) {
        recipeName.setText(recipe.getName());
        ingredientsAdapter.setData(recipe.getIngredients());
        stepsAdapter.setData(recipe.getSteps());
    }

}
