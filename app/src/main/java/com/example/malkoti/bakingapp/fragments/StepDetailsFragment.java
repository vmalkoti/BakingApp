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
import android.widget.TextView;

import com.example.malkoti.bakingapp.R;
import com.example.malkoti.bakingapp.adapters.StepsAdapter;
import com.example.malkoti.bakingapp.data.RecipeViewModel;


/**
 * Fragment class
 * to show details of the selected Step object
 */
public class StepDetailsFragment extends Fragment {
    private static final String LOG_TAG = StepDetailsFragment.class.getSimpleName();

    private RecipeViewModel recipeViewModel;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public StepDetailsFragment() { }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static StepDetailsFragment newInstance() {
        StepDetailsFragment fragment = new StepDetailsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_step_details, container, false);

        TextView stepVideo = view.findViewById(R.id.step_video_url);
        TextView stepDesc = view.findViewById(R.id.step_description);

        recipeViewModel = ViewModelProviders.of(getActivity()).get(RecipeViewModel.class);
        recipeViewModel.getSelectedStep().observe(StepDetailsFragment.this, step -> {
            stepVideo.setText(step.getVideoURL());
            stepDesc.setText(step.getDescription());
        });
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

}
