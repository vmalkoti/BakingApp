package com.example.malkoti.bakingapp.fragments;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.malkoti.bakingapp.R;
import com.example.malkoti.bakingapp.data.RecipeViewModel;
import com.example.malkoti.bakingapp.model.Recipe;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.util.List;


/*
 * TODO: Use DataBinding to get views
 */
/**
 * Fragment class
 * to show details of the selected Step object
 *
 * Referred to Google codelabs example for ExoPlayer:
 * https://codelabs.developers.google.com/codelabs/exoplayer-intro/#2
 */
public class StepDetailsFragment extends Fragment {
    private static final String LOG_TAG = "DEBUG_" + StepDetailsFragment.class.getSimpleName();

    private RecipeViewModel recipeViewModel;

    private TextView stepVideoUrl;
    private TextView stepDescription;
    private PlayerView playerView;
    private SimpleExoPlayer player;
    private Button nextStep;
    private Button prevStep;

    private boolean playWhenReady = false;
    private int currentWindow = 0;
    private long playbackPosition = 0;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public StepDetailsFragment() { }

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

        stepVideoUrl = view.findViewById(R.id.step_video_url);
        stepDescription = view.findViewById(R.id.step_description);
        playerView = view.findViewById(R.id.step_video);
        nextStep = view.findViewById(R.id.next_step);
        prevStep = view.findViewById(R.id.prev_step);

        recipeViewModel = ViewModelProviders.of(getActivity()).get(RecipeViewModel.class);
        recipeViewModel.getSelectedStep().observe(StepDetailsFragment.this, step -> {
            loadStepDetails(step);
            prevStep.setEnabled(!isCurrentStepFirstStep());
            nextStep.setEnabled(!isCurrentStepLastStep());

        });

        nextStep.setOnClickListener(v -> loadNextStep());
        prevStep.setOnClickListener(v -> loadPrevStep());

        if(!getResources().getBoolean(R.bool.twoPaneLayout)) {
            setVideoPlayerSize(getResources().getConfiguration().orientation);
        }

        hideStepNavButtons(getResources().getBoolean(R.bool.twoPaneLayout));

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setRetainInstance(true);
        if(savedInstanceState != null) {
            Log.d(LOG_TAG, "Set player state");
            playbackPosition = savedInstanceState.getLong("playbackPosition", 0);
            Log.d(LOG_TAG, "Playback position received " + playbackPosition);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }


    @Override
    public void onStart() {
        super.onStart();
        if(Util.SDK_INT > 23) {
            initializePlayer();
            Log.d(LOG_TAG, "Called onStart");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(Util.SDK_INT <= 23 || player == null) {
            initializePlayer();
            Log.d(LOG_TAG, "Called onResume");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(Util.SDK_INT <=23) {
            releasePlayer();
            Log.d(LOG_TAG, "Called onPause");
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if(Util.SDK_INT > 23) {
            releasePlayer();
            Log.d(LOG_TAG, "Called onStop");
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong("playbackPosition", playbackPosition);
    }



    /**
     * Method to initialize Exoplayer
     * with video URL from viewmodel
     */
    private void initializePlayer() {
        Recipe.Step selectedStep = recipeViewModel.getSelectedStep().getValue();

        if(selectedStep==null) return;

        String videoUrl = recipeViewModel.getSelectedStep().getValue().getVideoURL();

        Log.d(LOG_TAG, "Get PlayerView instance and options ");
        if(videoUrl != null && !videoUrl.trim().isEmpty()) {
            player = ExoPlayerFactory.newSimpleInstance(
                new DefaultRenderersFactory(getContext()),
                new DefaultTrackSelector(),
                new DefaultLoadControl());
            playerView.setPlayer(player);
            player.setPlayWhenReady(playWhenReady);
            player.seekTo(currentWindow, playbackPosition);
            Log.d(LOG_TAG, "Playback position set " + playbackPosition);

            Uri uri = Uri.parse(videoUrl);
            player.prepare(buildMediaSource(uri), false, false);
        }
    }

    /**
     * Get a mediaResource for a given Uri
     * @param uri Uri of the media resouce
     * @return MediaResource object for passed Uri
     */
    private MediaSource buildMediaSource(Uri uri) {
        String userAgent = "exoplayer-codelab";
        return new ExtractorMediaSource
                .Factory(new DefaultHttpDataSourceFactory(userAgent))
                .createMediaSource(uri);
    }

    /**
     * To release ExoPlayer instance
     */
    private void releasePlayer() {
        if(player != null) {
            playbackPosition = player.getCurrentPosition();
            currentWindow = player.getCurrentWindowIndex();
            playWhenReady = player.getPlayWhenReady();
            player.release();
            player = null;
        }
    }

    /**
     * Load details of step object into views
     * @param step Step object to read details
     */
    private void loadStepDetails(Recipe.Step step) {
        //Log.d(LOG_TAG, "Loaded new step " + step.getId());
        stepVideoUrl.setText(step.getVideoURL());
        stepDescription.setText(step.getDescription());
        releasePlayer();
        if(step.getVideoURL()==null || step.getVideoURL().trim().equals("")) {
            playerView.setVisibility(View.GONE);
        } else {
            playerView.setVisibility(View.VISIBLE);
            initializePlayer();
            //Log.d(LOG_TAG, "Video  " + step.getVideoURL());
        }
    }

    /**
     * Change size of ExoPlayer View
     * @param screenOrientation Orientation of the screen
     */
    private void setVideoPlayerSize(int screenOrientation) {
        ConstraintLayout.LayoutParams params =
                (ConstraintLayout.LayoutParams) playerView.getLayoutParams();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;

        float density = getResources().getDisplayMetrics().density;
        int leftMargin, rightMargin, topMargin, bottomMargin = 0;

        //Log.d(LOG_TAG, "Changing player view size");
        switch (screenOrientation) {
            case Configuration.ORIENTATION_LANDSCAPE:
                leftMargin = rightMargin = topMargin = 0;
                params.height = ViewGroup.LayoutParams.MATCH_PARENT;
                setTextViewVisibility(View.GONE);
                ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
                break;
            case Configuration.ORIENTATION_PORTRAIT:
                // fall through to default
            default:
                leftMargin = rightMargin = (int) (8*density);
                topMargin = (int) (16*density);
                params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                setTextViewVisibility(View.VISIBLE);
                ((AppCompatActivity) getActivity()).getSupportActionBar().show();
                break;
        }
        params.setMargins(leftMargin,topMargin,rightMargin,bottomMargin);
        playerView.setLayoutParams(params);
    }

    /**
     * Show or hide textviews
     * @param visibility
     */
    private void setTextViewVisibility(int visibility) {
        stepVideoUrl.setVisibility(visibility);
        stepDescription.setVisibility(visibility);
    }

    /**
     *
     */
    private void loadNextStep() {
        releasePlayer();

        List<Recipe.Step> steps = recipeViewModel.getSelectedRecipe().getValue().getSteps();
        Recipe.Step currentStep = recipeViewModel.getSelectedStep().getValue();
        int index = steps.indexOf(currentStep);
        recipeViewModel.setSelectedStep(steps.get(index+1));
    }

    /**
     *
     */
    private void loadPrevStep() {
        releasePlayer();

        List<Recipe.Step> steps = recipeViewModel.getSelectedRecipe().getValue().getSteps();
        Recipe.Step currentStep = recipeViewModel.getSelectedStep().getValue();
        int index = steps.indexOf(currentStep);
        recipeViewModel.setSelectedStep(steps.get(index-1));
    }

    /**
     *
     * @return
     */
    private boolean isCurrentStepLastStep() {
        List<Recipe.Step> steps = recipeViewModel.getSelectedRecipe().getValue().getSteps();
        Recipe.Step currentStep = recipeViewModel.getSelectedStep().getValue();
        int index = steps.indexOf(currentStep);
        return (index >= steps.size()-1);
    }

    /**
     *
     * @return
     */
    private boolean isCurrentStepFirstStep() {
        List<Recipe.Step> steps = recipeViewModel.getSelectedRecipe().getValue().getSteps();
        Recipe.Step currentStep = recipeViewModel.getSelectedStep().getValue();
        int index = steps.indexOf(currentStep);
        return (index == 0);
    }

    /**
     *
     * @param hide
     */
    private void hideStepNavButtons(boolean hide) {
        if(hide) {
            prevStep.setVisibility(View.INVISIBLE);
            nextStep.setVisibility(View.INVISIBLE);
        } else {
            prevStep.setVisibility(View.VISIBLE);
            prevStep.setVisibility(View.VISIBLE);
        }

    }
}
