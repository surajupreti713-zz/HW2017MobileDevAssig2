package com.suraj.itunessearch;

import android.content.Context;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ItunesMusicSource {

    String mSearchTerm;
    //private EditText mSearchText;

    public interface MusicListener {
        void onMusicResponse(List<Music> musicList);
    }

    private static ItunesMusicSource sItunesMusicSourceInstance;

    private Context mContext;
    private RequestQueue mRequestQueue;

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
    }

    public void getMusicItems(MusicListener musicListener) {
        final MusicListener musicListenerInternal = musicListener;

        String url = "https://itunes.apple.com/search?term=beyonce&entity=musicTrack";

        //String url = "https://itunes.apple.com/search?term=" + mSearchTerm + "&entity=musicTrack";
        JsonObjectRequest jsonObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.i("aa", "Hello there!!!!!!!!!!!!!!!!!!!");
                            List<Music> musicList = new ArrayList<Music>();
                            // Get the map of articles, keyed by article id.
                            Log.i("man", "Suraj is a good guy");
                            List<JSONObject> articlesObj = (List<JSONObject>) response.getJSONObject("results");
                            Log.i("ss", articlesObj.toString());
                            /*Iterator<String> it = articlesObj.keys();
                            while (it.hasNext()) {
                                String key = it.next();
                                JSONObject articleObject = articlesObj.getJSONObject(key);
                                Music music = new Music(articleObject);
                                musicList.add(music);
                            }*/
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
    }

}
