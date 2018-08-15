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
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.LoopingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
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
    private static final String EXO_PLAYBACK_POSITION_KEY = "playbackPosition";
    private static final String EXO_PLAY_WHEN_READY_KEY = "playWhenReady";
    private static final String EXO_CURR_WINDOW_KEY = "currentWindow";

    private RecipeViewModel recipeViewModel;

    private TextView stepNum;
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
        return new StepDetailsFragment();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_step_details, container, false);

        stepNum = view.findViewById(R.id.step_num);
        stepDescription = view.findViewById(R.id.step_description);
        playerView = view.findViewById(R.id.step_video);
        nextStep = view.findViewById(R.id.next_step);
        prevStep = view.findViewById(R.id.prev_step);

        recipeViewModel = ViewModelProviders.of(getActivity()).get(RecipeViewModel.class);
        recipeViewModel.getSelectedStep().observe(StepDetailsFragment.this, step -> {
            loadStepDetails(step);
            prevStep.setEnabled(!isCurrentStepFirstStep());
            nextStep.setEnabled(!isCurrentStepLastStep());

            if(!getResources().getBoolean(R.bool.twoPaneLayout)) {
                setVideoPlayerSize(getResources().getConfiguration().orientation);
            }
        });

        nextStep.setOnClickListener(v -> loadNextStep());
        prevStep.setOnClickListener(v -> loadPrevStep());

        if(!getResources().getBoolean(R.bool.twoPaneLayout)) {
            setVideoPlayerSize(getResources().getConfiguration().orientation);
        }

        Log.d(LOG_TAG, "Saved instance state object is null : " + (savedInstanceState==null));

        hideStepNavButtons(getResources().getBoolean(R.bool.twoPaneLayout));

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setRetainInstance(true);
        if(savedInstanceState != null) {
            Log.d(LOG_TAG, "onActivityCreated : restoring state ");
            //Log.d(LOG_TAG, "onCreate : Set player state in onActivityCreated");
            playbackPosition = savedInstanceState.getLong(EXO_PLAYBACK_POSITION_KEY, 0);
            Log.d(LOG_TAG, "Position =" + playbackPosition);
            currentWindow = savedInstanceState.getInt(EXO_CURR_WINDOW_KEY, 0);
            playWhenReady = savedInstanceState.getBoolean(EXO_PLAY_WHEN_READY_KEY, false);
            //Log.d(LOG_TAG, "onCreate : Playback position received " + playbackPosition);
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
            //Log.d(LOG_TAG, "onStart called");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(Util.SDK_INT <= 23 || player == null) {
            initializePlayer();
            //Log.d(LOG_TAG, "onResume called");
        }
    }

    @Override
    public void onPause() {
        savePlaybackState();

        super.onPause();
        if(Util.SDK_INT <=23) {
            releasePlayer();
            Log.d(LOG_TAG, "onPause called");
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if(Util.SDK_INT > 23) {
            releasePlayer();
            Log.d(LOG_TAG, "onStop called");
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(EXO_PLAYBACK_POSITION_KEY, playbackPosition);
        outState.putInt(EXO_CURR_WINDOW_KEY, currentWindow);
        outState.putBoolean(EXO_PLAY_WHEN_READY_KEY, playWhenReady);
        Log.d(LOG_TAG, "onSaveInstanceState : Playback position saved " + playbackPosition);
    }



    /**
     * Method to initialize Exoplayer
     * with video URL from viewmodel
     */
    private void initializePlayer() {
        Recipe.Step selectedStep = recipeViewModel.getSelectedStep().getValue();

        if(selectedStep==null) return;

        String videoUrl = recipeViewModel.getSelectedStep().getValue().getVideoURL();

        //Log.d(LOG_TAG, "initializePlayer : Get PlayerView instance and options ");
        if(videoUrl != null && !videoUrl.trim().isEmpty()) {
            player = ExoPlayerFactory.newSimpleInstance(
                new DefaultRenderersFactory(getContext()),
                new DefaultTrackSelector(),
                new DefaultLoadControl());

            Uri uri = Uri.parse(videoUrl);
            //LoopingMediaSource mediaSource = new LoopingMediaSource(buildMediaSource(uri));
            player.prepare(buildMediaSource(uri), false, false);

            playerView.setPlayer(player);
            player.setPlayWhenReady(playWhenReady);
            player.seekTo(currentWindow, playbackPosition);

            player.addListener(new Player.EventListener() {
                @Override
                public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                    // if playback has ended
                    if(playbackState == Player.STATE_ENDED) {
                        // reset back to beginning and don't autoplay
                        playbackPosition = 0;
                        player.setPlayWhenReady(false);
                        player.seekTo(currentWindow, playbackPosition);
                    }
                }

                @Override
                public void onTimelineChanged(Timeline timeline, Object manifest, int reason) { }

                @Override
                public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) { }

                @Override
                public void onLoadingChanged(boolean isLoading) { }

                @Override
                public void onRepeatModeChanged(int repeatMode) { }

                @Override
                public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) { }

                @Override
                public void onPlayerError(ExoPlaybackException error) { }

                @Override
                public void onPositionDiscontinuity(int reason) { }

                @Override
                public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) { }

                @Override
                public void onSeekProcessed() { }
            });
            //Log.d(LOG_TAG, "initializePlayer : Playback position set " + playbackPosition);

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
            savePlaybackState();
            player.release();
            player = null;
        }
    }

    private void savePlaybackState() {
        if(player.getCurrentPosition() > 0) {
            playbackPosition = player.getCurrentPosition();
            currentWindow = player.getCurrentWindowIndex();
            playWhenReady = player.getPlayWhenReady();
        }
    }

    /**
     * Load details of step object into views
     * @param step Step object to read details
     */
    private void loadStepDetails(Recipe.Step step) {
        int stepId = step.getId();
        String stepNumber;
        if(stepId==0) {
            stepNumber = "Introduction";
        } else {
            stepNumber = "Step " + String.valueOf(stepId);
        }
        stepNum.setText(stepNumber);
        stepDescription.setText(step.getDescription());
        releasePlayer();
        if(step.getVideoURL()==null || step.getVideoURL().trim().equals("")) {
            playerView.setVisibility(View.GONE);
            releasePlayer();
        } else {
            playerView.setVisibility(View.VISIBLE);
            initializePlayer();
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
                String url = recipeViewModel.getSelectedStep().getValue().getVideoURL();
                if(url != null && !url.trim().equals("")) {
                    leftMargin = rightMargin = topMargin = 0;
                    params.height = ViewGroup.LayoutParams.MATCH_PARENT;
                    setTextViewVisibility(View.GONE);
                    ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
                    hideStepNavButtons(true);
                    break;
                }
            case Configuration.ORIENTATION_PORTRAIT:
                // fall through to default
            default:
                leftMargin = rightMargin = (int) (8*density);
                topMargin = (int) (16*density);
                params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                setTextViewVisibility(View.VISIBLE);
                ((AppCompatActivity) getActivity()).getSupportActionBar().show();
                hideStepNavButtons(false);
                break;
        }
        params.setMargins(leftMargin,topMargin,rightMargin,bottomMargin);
        playerView.setLayoutParams(params);
    }

    /**
     * Show or hide textviews
     * @param visibility Visibility mode for the textview (View.VISIBLE, View.GONE, View.INSIVIBLE)
     */
    private void setTextViewVisibility(int visibility) {
        stepNum.setVisibility(visibility);
        stepDescription.setVisibility(visibility);
    }

    /**
     * Get next Step object and update ViewModel
     */
    private void loadNextStep() {
        releasePlayer();

        List<Recipe.Step> steps = recipeViewModel.getSelectedRecipe().getValue().getSteps();
        Recipe.Step currentStep = recipeViewModel.getSelectedStep().getValue();
        int index = steps.indexOf(currentStep);
        recipeViewModel.setSelectedStep(steps.get(index+1));
    }

    /**
     * Get previous Step object and update ViewModel
     */
    private void loadPrevStep() {
        releasePlayer();

        List<Recipe.Step> steps = recipeViewModel.getSelectedRecipe().getValue().getSteps();
        Recipe.Step currentStep = recipeViewModel.getSelectedStep().getValue();
        int index = steps.indexOf(currentStep);
        //Log.d(LOG_TAG, "Current step=" + index + "; total=" + steps.size());
        recipeViewModel.setSelectedStep(steps.get(index-1));
    }

    /**
     * Check if current step is the last step of recipe
     * @return True if it is last step; else false
     */
    private boolean isCurrentStepLastStep() {
        List<Recipe.Step> steps = recipeViewModel.getSelectedRecipe().getValue().getSteps();
        Recipe.Step currentStep = recipeViewModel.getSelectedStep().getValue();
        int index = steps.indexOf(currentStep);
        //Log.d(LOG_TAG, "Current step index=" + index + "; total=" + steps.size());
        return (index == steps.size()-1);
    }

    /**
     * Check if current step is the first step of recipe
     * @return True if it is first step; else false
     */
    private boolean isCurrentStepFirstStep() {
        List<Recipe.Step> steps = recipeViewModel.getSelectedRecipe().getValue().getSteps();
        Recipe.Step currentStep = recipeViewModel.getSelectedStep().getValue();
        int index = steps.indexOf(currentStep);
        return (index == 0);
    }

    /**
     * Hide or show the Previous/Next buttons
     * @param hide Boolean value indicating whether the buttons should be hidden
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
