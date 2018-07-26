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

public class IngredientsAdapter extends RecyclerView.Adapter<IngredientsAdapter.IngredientViewHolder> {
    List<Recipe.Ingredient> ingredients;

    @NonNull
    @Override
    public IngredientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.ingredient_item, parent, false);
        IngredientViewHolder viewHolder = new IngredientViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull IngredientViewHolder holder, int position) {
        Recipe.Ingredient ingredient = ingredients.get(position);
        holder.bindViewHolder(ingredient);
    }

    @Override
    public int getItemCount() {
        return this.ingredients==null? 0: this.ingredients.size();
    }

    /**
     * Change data in the adapter
     * @param ingredients
     */
    public void setData(List<Recipe.Ingredient> ingredients) {
        this.ingredients = ingredients;
        notifyDataSetChanged();
    }

    /**
     * ViewHolder class for ingredient
     */
    public class IngredientViewHolder extends RecyclerView.ViewHolder {
        private TextView ingredientDetails;

        public IngredientViewHolder(View itemView) {
            super(itemView);
            ingredientDetails = itemView.findViewById(R.id.recipe_ingredient_item);
        }

        public void bindViewHolder(Recipe.Ingredient ingredient) {
            ingredientDetails.setText(ingredient.toString());
        }
    }
}
