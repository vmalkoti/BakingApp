package com.example.malkoti.bakingapp.fragments;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.malkoti.bakingapp.R;
import com.example.malkoti.bakingapp.adapters.RecipesAdapter;
import com.example.malkoti.bakingapp.data.RecipeViewModel;


/**
 * A fragment representing a list of Items.
 */
public class RecipeListFragment extends Fragment {
    private static final String LOG_TAG = RecipeListFragment.class.getSimpleName();

    private static final String ARG_COLUMN_COUNT = "column-count";
    private int gridColumnCount;
    private RecipesAdapter.OnRecipeItemClickListener adapterItemListener;

    private RecipeViewModel recipeViewModel;
    private OnFragmentItemClickListener fragmentClickListener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RecipeListFragment() { }

    @SuppressWarnings("unused")
    public static RecipeListFragment newInstance(OnFragmentItemClickListener listener) {
        RecipeListFragment fragment = new RecipeListFragment();
        fragment.fragmentClickListener = listener;
        return fragment;
    }

    public void setClickListener(OnFragmentItemClickListener listener) {
        this.fragmentClickListener = listener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipe_list, container, false);

        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            RecyclerView.LayoutManager layoutManager;
            Log.d(LOG_TAG, "Number of columns " + gridColumnCount);

            gridColumnCount = getResources().getInteger(R.integer.gridColumns);
            if (gridColumnCount <= 1) {
                layoutManager = new LinearLayoutManager(context);
            } else {
                layoutManager = new GridLayoutManager(context, gridColumnCount);
            }
            recyclerView.setLayoutManager(layoutManager);
            RecipesAdapter adapter = new RecipesAdapter(adapterItemListener);
            recyclerView.setAdapter(adapter);

            recipeViewModel = ViewModelProviders.of(getActivity()).get(RecipeViewModel.class);
            recipeViewModel.getAllRecipes().observe(RecipeListFragment.this, recipes -> adapter.setData(recipes));
        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        adapterItemListener = (recipe) -> {
            Log.d(LOG_TAG, "Clicked on recipe " + recipe.getName());
            recipeViewModel.setSelectedRecipe(recipe);
            fragmentClickListener.onClick();
        };

    }

}
