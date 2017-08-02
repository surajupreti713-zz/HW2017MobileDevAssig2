package com.suraj.itunessearch;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.LruCache;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ItunesMusicSource {
    String mSearchTerm;

    public interface MusicListener {
        void onMusicResponse(List<Music> musicList);
    }

    private final static int IMAGE_CACHE_COUNT = 100;
    private static ItunesMusicSource sItunesMusicSourceInstance;

    private Context mContext;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;

    public static ItunesMusicSource get(Context context) {
        if (sItunesMusicSourceInstance == null) {
            sItunesMusicSourceInstance = new ItunesMusicSource(context);
        }
        return sItunesMusicSourceInstance;
    }

    private ItunesMusicSource(Context context) {
        mContext = context.getApplicationContext();
        mRequestQueue = Volley.newRequestQueue(mContext);
        //mSearchTerm = searchTerm.getText().toString();

        mImageLoader = new ImageLoader(mRequestQueue, new ImageLoader.ImageCache() {
            private final LruCache<String, Bitmap> mCache = new LruCache<>(IMAGE_CACHE_COUNT);
            public void putBitmap(String url, Bitmap bitmap) {
                mCache.put(url, bitmap);
            }
            public Bitmap getBitmap(String url) {
                return mCache.get(url);
            }
        });
    }

    public void getMusicItems(MusicListener musicListener, String searchTerm) {
        final MusicListener musicListenerInternal = musicListener;
        String url = "https://itunes.apple.com/search?term=" + searchTerm + "&entity=musicTrack";
        JsonObjectRequest jsonObjRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            List<Music> musicList = new ArrayList<Music>();
                            JSONArray resultArray = response.getJSONArray("results");
                            Log.i("ss", resultArray.toString());
                            for(int i = 0; i < resultArray.length(); i++) {
                                JSONObject musicObject = resultArray.getJSONObject(i);
                                Music music = new Music(musicObject);
                                musicList.add(music);
                            }
                            musicListenerInternal.onMusicResponse(musicList);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            musicListenerInternal.onMusicResponse(null);
                            Toast.makeText(mContext, "Could not get articles.", Toast.LENGTH_SHORT);
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        musicListenerInternal.onMusicResponse(null);
                        Toast.makeText(mContext, "Could not get articles.", Toast.LENGTH_SHORT);
                    }
                });
        mRequestQueue.add(jsonObjRequest);
    }

    public ImageLoader getImageLoader() {
        return mImageLoader;
    }
}
