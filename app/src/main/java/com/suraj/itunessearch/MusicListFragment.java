package com.suraj.itunessearch;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static com.suraj.itunessearch.R.id.parent;

public class MusicListFragment extends Fragment {

    private ListView mListView;
    private MusicAdapter mMusicAdapter;

    private TextView mTrackNameView;
    private TextView mArtistNameView;

    private List<Music> mMusic;

    private EditText searchText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_musiclist, container, false);

        ItunesMusicSource.get(getContext()).getMusicItems(new ItunesMusicSource.MusicListener() {

            @Override
            public void onMusicResponse(List<Music> musicList) {
                //Log.i("aa", "Hello there!!!!!!!!!!!!!!!!!!!");
                mMusic = musicList;
                // Stop the spinner and update the list view.
                mMusicAdapter.setItems(musicList);
            }
        });

        mListView = (ListView) v.findViewById(R.id.list_view);
        mMusicAdapter = new MusicAdapter(getActivity());
        mListView.setAdapter(mMusicAdapter);

        /*mListView.setOnClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Music music = (Music) parent.getAdapter().getItem(position);
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(mu))
            }
        });*/

        ItunesMusicSource.get(getContext()).getMusicItems(new ItunesMusicSource.MusicListener() {

            @Override
            public void onMusicResponse(List<Music> musicList) {
                mMusic = musicList;
                // Stop the spinner and update the list view.
                mMusicAdapter.setItems(musicList);
            }
        });

        return v;
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
            return rowView;
        }
    }
}
