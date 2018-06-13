package com.b3po.instaclone.instaposthome;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.b3po.instaclone.R;

public class InstagramPost extends Fragment {

    public InstagramPost() {
        // Required empty public constructor
    }

    public static InstagramPost newInstance() {
        InstagramPost fragment = new InstagramPost();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_instagram_post, container, false);
    }
}
