package com.b3po.instaclone.instaposthome;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.b3po.instaclone.R;

public class PopularPosts extends Fragment {

    public PopularPosts() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static PopularPosts newInstance() {
        PopularPosts fragment = new PopularPosts();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_popular_posts, container, false);
    }
}