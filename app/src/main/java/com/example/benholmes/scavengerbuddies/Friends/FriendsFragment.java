package com.example.benholmes.scavengerbuddies.Friends;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.benholmes.scavengerbuddies.App.ScavengerFragment;
import com.example.benholmes.scavengerbuddies.R;

/**
 * Created by benholmes on 5/7/18.
 */

public class FriendsFragment extends ScavengerFragment{

    private View view;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_friends, container, false);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
