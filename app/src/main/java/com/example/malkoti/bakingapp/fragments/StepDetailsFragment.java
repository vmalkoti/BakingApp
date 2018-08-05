package com.example.malkoti.bakingapp.fragments;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    private boolean playWhenReady = true;
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

        recipeViewModel = ViewModelProviders.of(getActivity()).get(RecipeViewModel.class);
        recipeViewModel.getSelectedStep().observe(StepDetailsFragment.this, step -> loadStepDetails(step));
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setRetainInstance(true);
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
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(Util.SDK_INT <= 23 || player == null) {
            initializePlayer();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(Util.SDK_INT <=23) {
            releasePlayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if(Util.SDK_INT > 23) {
            releasePlayer();
        }
    }

    /**
     * Method to initialize Exoplayer
     * with video URL from viewmodel
     */
    private void initializePlayer() {
        Recipe.Step selectedStep = recipeViewModel.getSelectedStep().getValue();

        if(selectedStep==null) return;

        String videoUrl = recipeViewModel.getSelectedStep().getValue().getVideoURL();

        if(videoUrl != null && !videoUrl.trim().isEmpty()) {
            player = ExoPlayerFactory.newSimpleInstance(
                new DefaultRenderersFactory(getContext()),
                new DefaultTrackSelector(),
                new DefaultLoadControl());
            playerView.setPlayer(player);
            player.setPlayWhenReady(playWhenReady);
            player.seekTo(currentWindow, playbackPosition);

            Uri uri = Uri.parse(videoUrl);
            player.prepare(buildMediaSource(uri), true, false);
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
}
