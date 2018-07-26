package com.example.malkoti.bakingapp.fragments;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.malkoti.bakingapp.R;
import com.example.malkoti.bakingapp.activities.MainActivity;
import com.example.malkoti.bakingapp.adapters.RecipesAdapter;
import com.example.malkoti.bakingapp.data.RecipeViewModel;
import com.example.malkoti.bakingapp.model.Recipe;

import java.util.List;


/**
 * A fragment representing a list of Items.
 */
public class RecipeListFragment extends Fragment {
    private static final String LOG_TAG = RecipeListFragment.class.getSimpleName();

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 2;
    private RecipesAdapter.OnRecipeItemClickListener mListener;

    private RecipeViewModel recipeViewModel;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RecipeListFragment() { }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static RecipeListFragment newInstance(int columnCount) {
        RecipeListFragment fragment = new RecipeListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipe_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            RecyclerView.LayoutManager layoutManager;
            Log.d(LOG_TAG, "Number of columns " + mColumnCount);
            if (mColumnCount <= 1) {
                //recyclerView.setLayoutManager(new LinearLayoutManager(context));
                layoutManager = new LinearLayoutManager(context);
            } else {
                //recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
                layoutManager = new GridLayoutManager(context, mColumnCount);
            }
            recyclerView.setLayoutManager(layoutManager);
            RecipesAdapter adapter = new RecipesAdapter(mListener);
            recyclerView.setAdapter(adapter);

            recipeViewModel = ViewModelProviders.of(getActivity()).get(RecipeViewModel.class);
            recipeViewModel.getAllRecipes().observe(RecipeListFragment.this, recipes -> {
                adapter.setData(recipes);
            });
        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mListener = (recipe) -> {
            recipeViewModel.setSelectedRecipe(recipe);
        };

    }

}
