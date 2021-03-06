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

public class StepsAdapter extends RecyclerView.Adapter<StepsAdapter.StepsViewHolder> {
    private OnStepItemClickListener listener;
    private List<Recipe.Step> steps;

    public interface OnStepItemClickListener {
        void onItemClicked(Recipe.Step step);
    }

    public StepsAdapter(OnStepItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public StepsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.step_item, parent, false);

        return new StepsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StepsViewHolder holder, int position) {
        Recipe.Step step = steps.get(position);
        holder.bindViewHolder(step);
    }

    @Override
    public int getItemCount() {
        return this.steps==null? 0: this.steps.size();
    }

    public void setData(List<Recipe.Step> steps) {
        this.steps = steps;
        notifyDataSetChanged();
    }


    /**
     * ViewHolder class for list of steps
     */
    class StepsViewHolder extends RecyclerView.ViewHolder {
        private TextView stepName;

        StepsViewHolder(View itemView) {
            super(itemView);
            stepName = itemView.findViewById(R.id.step_name);
        }

        void bindViewHolder(Recipe.Step step) {
            int stepId = step.getId();
            String stepText = step.getShortDescription();
            if(stepId > 0) {
                 stepText = String.valueOf(stepId) + ". " + stepText;
            }
            stepName.setText(stepText);
            stepName.setOnClickListener((view) -> listener.onItemClicked(step));
        }
    }
}
