package com.suraj.itunessearch;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set the fragment into the container.
        FragmentManager fm = getSupportFragmentManager();
        Fragment existingFragment = fm.findFragmentById(R.id.container);
        if (existingFragment == null) {
            Fragment musicListFragment = new MusicListFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.container, musicListFragment).commit();
        }
    }
}
