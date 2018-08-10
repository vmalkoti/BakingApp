package com.example.malkoti.bakingapp.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.malkoti.bakingapp.R;
import com.example.malkoti.bakingapp.model.Recipe;

import java.util.List;


public class RecipesAdapter extends RecyclerView.Adapter<RecipesAdapter.RecipeViewHolder> {
        private OnRecipeItemClickListener listener;
    private List<Recipe> recipes;

    public interface OnRecipeItemClickListener {
        void onItemClicked(Recipe recipe);
    }

    public RecipesAdapter(OnRecipeItemClickListener listener) {
        this.listener = listener;
    }


    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.recipe_item, parent, false);
        RecipeViewHolder viewHolder = new RecipeViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        Recipe recipe = recipes.get(position);
        holder.bindViewHolder(recipe);
    }

    @Override
    public int getItemCount() {
        return (this.recipes == null)? 0 : this.recipes.size();
    }

    /**
     * Change data in the adapter
     * @param recipes
     */
    public void setData(List<Recipe> recipes) {
        this.recipes = recipes;
        notifyDataSetChanged();
    }

    /**
     * ViewHolder class for Recipes
     */
    class RecipeViewHolder extends RecyclerView.ViewHolder {
        private TextView recipeName;

        public RecipeViewHolder(View itemView) {
            super(itemView);
            recipeName = itemView.findViewById(R.id.recipe_name_item);
        }

        public void bindViewHolder(Recipe recipe) {
            recipeName.setText(recipe.getName());
            recipeName.setOnClickListener((view) -> listener.onItemClicked(recipe));
        }
    }
}
