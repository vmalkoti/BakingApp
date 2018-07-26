package com.example.malkoti.bakingapp.fragments;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.net.Uri;
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


/**
 * Fragment class
 * to show details of the selected Step object
 */
public class StepDetailsFragment extends Fragment {
    private static final String LOG_TAG = StepDetailsFragment.class.getSimpleName();

    private RecipeViewModel recipeViewModel;

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

        //TextView stepVideo = view.findViewById(R.id.step_video_url);
        TextView stepDesc = view.findViewById(R.id.step_description);

        //https://codelabs.developers.google.com/codelabs/exoplayer-intro/#2
        playerView = view.findViewById(R.id.step_video);

        recipeViewModel = ViewModelProviders.of(getActivity()).get(RecipeViewModel.class);
        recipeViewModel.getSelectedStep().observe(StepDetailsFragment.this, step -> {
            //stepVideo.setText(step.getVideoURL());
            stepDesc.setText(step.getDescription());
            if(step.getVideoURL()==null || step.getVideoURL().trim().equals("")) {
                playerView.setVisibility(View.GONE);
            }
        });
        return view;
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
        String videoUrl = recipeViewModel.getSelectedStep().getValue().getVideoURL();

        if(videoUrl != null && !videoUrl.trim().equals("")) {
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

}
