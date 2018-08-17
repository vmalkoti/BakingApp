package com.example.malkoti.bakingapp.fragments;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
    private static final String LOG_TAG = "DEBUG_" + RecipeListFragment.class.getSimpleName();

    private RecipesAdapter.OnRecipeItemClickListener adapterItemListener;

    private RecipeViewModel recipeViewModel;

    /**
     * Interface that must be implemented by the activity
     * to provide click event handler
     */
    public interface RecipeListItemClickListener {
        void onRecipeItemClicked();
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RecipeListFragment() { }

    @SuppressWarnings("unused")
    public static RecipeListFragment newInstance() {
        RecipeListFragment fragment = new RecipeListFragment();
        return fragment;
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

            int gridColumnCount = getResources().getInteger(R.integer.gridColumns);
            if (gridColumnCount <= 1) {
                layoutManager = new LinearLayoutManager(context);
            } else {
                layoutManager = new GridLayoutManager(context, gridColumnCount);
            }
            recyclerView.setLayoutManager(layoutManager);
            RecipesAdapter adapter = new RecipesAdapter(adapterItemListener);
            recyclerView.setAdapter(adapter);

            recipeViewModel = ViewModelProviders.of(getActivity()).get(RecipeViewModel.class);
            recipeViewModel.getAllRecipes().observe(RecipeListFragment.this, adapter::setData);
        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        adapterItemListener = (recipe) -> {
            recipeViewModel.setSelectedRecipe(recipe);
            FragmentActivity activity = getActivity();
            if(activity instanceof RecipeListItemClickListener) {
                ((RecipeListItemClickListener) activity).onRecipeItemClicked();
            }
        };

    }

}
