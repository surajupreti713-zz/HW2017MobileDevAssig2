package com.suraj.itunessearch;

import org.json.JSONException;
import org.json.JSONObject;

public class Music {
    private String mKind;
    private String mTrackName;
    private String mArtistName;
    private String mCollectionName;
    private String mPreviewUrl;
    private String mArtworkUrl60;
    private String mTrackViewUrl;

    public Music(JSONObject resultsObj) {
        try {
            mKind = resultsObj.getString("kind");
            mTrackName = resultsObj.getString("trackName");
            mArtistName = resultsObj.getString("artistName");
            mCollectionName = resultsObj.getString("collectionName");
            mPreviewUrl = resultsObj.getString("previewUrl");
            mArtworkUrl60 = resultsObj.getString("artworkUrl60");
            mTrackViewUrl = resultsObj.getString("trackViewUrl");

        }catch(JSONException e) {
            e.printStackTrace();
        }
    }

    public String getmKind() {
        return mKind;
    }

    public String getmTrackName() {
        return mTrackName;
    }

    public String getmArtistName() {
        return mArtistName;
    }

    public String getmCollectionName() {
        return mCollectionName;
    }

    public String getmPreviewUrl() {
        return mPreviewUrl;
    }

    public String getmArtworkUrl60() {
        return mArtworkUrl60;
    }

    public String getmTrackViewUrl() {
        return mTrackViewUrl;
    }
}
