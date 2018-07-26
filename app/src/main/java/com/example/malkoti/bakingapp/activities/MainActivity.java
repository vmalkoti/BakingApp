package com.example.malkoti.bakingapp.activities;

import android.arch.lifecycle.ViewModelProviders;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.malkoti.bakingapp.R;
import com.example.malkoti.bakingapp.data.RecipeViewModel;
import com.example.malkoti.bakingapp.fragments.RecipeListFragment;
import com.example.malkoti.bakingapp.fragments.StepDetailsFragment;
import com.example.malkoti.bakingapp.fragments.RecipeDetailsFragment;


public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private RecipeViewModel viewModel;
    private TextView displayText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewModel = ViewModelProviders.of(MainActivity.this).get(RecipeViewModel.class);

        viewModel.getSelectedRecipe().observe(MainActivity.this, recipe -> {
            RecipeDetailsFragment stepListFragment = RecipeDetailsFragment.newInstance();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, stepListFragment)
                    .addToBackStack("recipe-detail")
                    .commit();
        });

        viewModel.getSelectedStep().observe(MainActivity.this, step -> {
            // load step details fragment
            StepDetailsFragment stepDetailsFragment = StepDetailsFragment.newInstance();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, stepDetailsFragment)
                    .addToBackStack("step_details")
                    .commit();
        });

        if(findViewById(R.id.fragment_container) != null) {
            if(savedInstanceState!=null) return;

            RecipeListFragment recipeListFragment = RecipeListFragment.newInstance(1);
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragment_container, recipeListFragment)
                    .commit();
        }
    }
}
