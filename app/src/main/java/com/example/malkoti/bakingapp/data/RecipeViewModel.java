package com.example.malkoti.bakingapp.data;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.example.malkoti.bakingapp.model.Recipe;
import com.example.malkoti.bakingapp.network.NetworkUtils;

import java.util.List;

public class RecipeViewModel extends AndroidViewModel {
    private LiveData<List<Recipe>> recipes;
    private MutableLiveData<Recipe> selectedRecipe = new MutableLiveData<>();
    private MutableLiveData<Recipe.Step> selectedStep = new MutableLiveData<>();

    public RecipeViewModel(@NonNull Application application) {
        super(application);
        this.recipes = NetworkUtils.getRecipes();
    }

    public LiveData<List<Recipe>> getAllRecipes() {
        return recipes;
    }

    public MutableLiveData<Recipe> getSelectedRecipe() {
        return selectedRecipe;
    }

    public MutableLiveData<Recipe.Step> getSelectedStep() {
        return selectedStep;
    }

    /* Setters for Activity/Fragments to access */
    public void setSelectedRecipe(Recipe recipe) {
        this.selectedRecipe.setValue(recipe);
    }

    public void setSelectedStep(Recipe.Step step) {
        this.selectedStep.setValue(step);
    }
}
