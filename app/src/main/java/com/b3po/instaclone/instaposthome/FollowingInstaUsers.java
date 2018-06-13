package com.b3po.instaclone.instaposthome;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.b3po.instaclone.R;

public class FollowingInstaUsers extends Fragment {

    public FollowingInstaUsers() {
        // Required empty public constructor
    }

    public static FollowingInstaUsers newInstance() {
        FollowingInstaUsers fragment = new FollowingInstaUsers();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_following_insta_users, container, false);
    }
}