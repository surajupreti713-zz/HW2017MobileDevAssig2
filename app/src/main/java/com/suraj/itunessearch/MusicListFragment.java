package com.suraj.itunessearch;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.suraj.itunessearch.R.id.parent;
import android.media.AudioManager;
import android.media.MediaPlayer;

public class MusicListFragment extends Fragment {
    private ListView mListView;

    private MusicAdapter mMusicAdapter;
    private MediaPlayer mMediaPlayer;
    private String mCurrentlyPlayingUrl;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private NetworkImageView mImageView;

    private TextView mTrackNameView;
    private TextView mArtistNameView;
    private TextView mCollectionNameView;

    private List<Music> mMusic;

    private String searchText;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        searchText = getArguments().getString("data");
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_musiclist, container, false);

        ItunesMusicSource.get(getContext()).getMusicItems(new ItunesMusicSource.MusicListener() {

            @Override
            public void onMusicResponse(List<Music> musicList) {
                mMusic = musicList;
                // Stop the spinner and update the list view.
                mMusicAdapter.setItems(musicList);
            }
        }, searchText);

        // Set up the SwipeRefreshLayout to reload when swiped.
        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swiperefresh);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshMusic();
            }
        });

        mListView = (ListView) v.findViewById(R.id.list_view);
        mMusicAdapter = new MusicAdapter(getActivity());
        mListView.setAdapter(mMusicAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){


            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long l) {
                Music music = (Music) parent.getAdapter().getItem(position);
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "This track is good");
                shareIntent.putExtra(Intent.EXTRA_TEXT, music.getmTrackViewUrl());
                startActivity(Intent.createChooser(shareIntent, "Share link using"));
            }
        });

        // If there is content to display, show it, otherwise refresh content.
        if (mMusic != null) {
            mMusicAdapter.setItems(mMusic);
        }
        else {
            mSwipeRefreshLayout.setRefreshing(true);
            refreshMusic();
        }

        return v;
    }

    private void clickedAudioURL(String url) {
        if (mMediaPlayer.isPlaying()) {
            if (mCurrentlyPlayingUrl.equals(url)) {
                mMediaPlayer.stop();
                mMusicAdapter.notifyDataSetChanged();
                return;
            }
        }

        mCurrentlyPlayingUrl = url;
        try {
            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(url);
            mMediaPlayer.prepareAsync(); // might take long! (for buffering, etc)
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                    mMusicAdapter.notifyDataSetChanged();
                }
            });
            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mMusicAdapter.notifyDataSetChanged();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void refreshMusic() {
        ItunesMusicSource.get(getContext()).getMusicItems(new ItunesMusicSource.MusicListener() {
            @Override
            public void onMusicResponse(List<Music> musicList) {
                mMusic = musicList;
                // Stop the spinner and update the list view.
                mSwipeRefreshLayout.setRefreshing(false);
                mMusicAdapter.setItems(musicList);
            }
        }, searchText);
    }


    private class MusicAdapter extends BaseAdapter{
        private Context mContext;
        private LayoutInflater mInflater;
        private List<Music> mDataSource;

        public MusicAdapter(Context context) {
            mContext = context;
            mDataSource = new ArrayList<>();
            mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public void setItems(List<Music> musicList) {
            mDataSource.clear();
            mDataSource.addAll(musicList);
            notifyDataSetChanged();
        }
        @Override
        public int getCount() {
            return mDataSource.size();
        }

        @Override
        public Object getItem(int position) {
            return mDataSource.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            final Music music = mDataSource.get(position);
            View rowView = mInflater.inflate(R.layout.list_item_music, parent, false);

            mTrackNameView = (TextView) rowView.findViewById(R.id.trackNameView);
            mTrackNameView.setText(music.getmTrackName());

            mArtistNameView = (TextView) rowView.findViewById(R.id.artistNameView);
            mArtistNameView.setText(music.getmArtistName());

            mCollectionNameView = (TextView) rowView.findViewById(R.id.collectionNameView);
            mCollectionNameView.setText(music.getmCollectionName());

            mImageView = (NetworkImageView) rowView.findViewById(R.id.networkImageView);
            ImageLoader loader = ItunesMusicSource.get(getContext()).getImageLoader();
            mImageView.setImageUrl(music.getmArtworkUrl60(), loader);


            final ImageButton playButton = (ImageButton) rowView.findViewById(R.id.play_button);
            boolean isPlaying = mMediaPlayer.isPlaying() && mCurrentlyPlayingUrl.equals(music.getmPreviewUrl());

            if (isPlaying) {
                playButton.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_media_pause));
            }
            else {
                playButton.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_media_play));
            }

            playButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i("aas", "it woooooorkssssss!!!!!");
                    clickedAudioURL(music.getmPreviewUrl());
                }
            });
            return rowView;
        }
    }
}
