package com.japg.mastermoviles.opengl10;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.ogg.OggExtractor;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.LoopingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.RawResourceDataSource;

public class MusicPlayer {

    private SimpleExoPlayer mPlayer;
    private Handler mHandler;

    public MusicPlayer(Context context) {
        mPlayer = ExoPlayerFactory.newSimpleInstance(context);
        mHandler = new Handler(Looper.getMainLooper());

        DataSpec dataSpec = new DataSpec(RawResourceDataSource.buildRawResourceUri(R.raw.loop));
        final RawResourceDataSource rawResourceDataSource = new RawResourceDataSource(context);
        try {
            rawResourceDataSource.open(dataSpec);
        } catch (Exception e) {
            e.printStackTrace();
        }

        DataSource.Factory factory = new DataSource.Factory() {
            @Override
            public DataSource createDataSource() {
                return rawResourceDataSource;
            }
        };

        MediaSource audioSource = new ExtractorMediaSource(rawResourceDataSource.getUri(), factory, OggExtractor.FACTORY, null, null);
        LoopingMediaSource loopingMediaSource = new LoopingMediaSource(audioSource);

        mPlayer.prepare(loopingMediaSource);
    }

    public void play() {
        mPlayer.seekTo(0);
        mPlayer.setVolume(0);
        mPlayer.setPlayWhenReady(true);

        mHandler.postDelayed(mIncreaseRunnable, 10);
    }

    public void stop() {
        mPlayer.setPlayWhenReady(false);
        mHandler.removeCallbacks(mIncreaseRunnable);
    }

    private Runnable mIncreaseRunnable = new Runnable() {
        @Override
        public void run() {
            mPlayer.setVolume(mPlayer.getVolume() + 0.01f);
            if (mPlayer.getVolume() < 1f) {
                mHandler.postDelayed(mIncreaseRunnable, 10);
            }
        }
    };

}
