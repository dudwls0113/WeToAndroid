package com.ninano.weto.src.tutorial;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ninano.weto.R;
import com.ninano.weto.src.BaseFragment;
import com.ninano.weto.src.tutorial.models.TutorialData;

public class TutorialFragment extends BaseFragment {

    private String title, subTitle;
    private int image;

    private ImageView mImageView;

    public TutorialFragment() {
        // Required empty public constructor
    }


    public static TutorialFragment newInstance(TutorialData tutorialData) {
        TutorialFragment fragment = new TutorialFragment();
        Bundle args = new Bundle();
        args.putInt("image", tutorialData.getGuideImage());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            image = getArguments().getInt("image");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tutorial, container, false);
        setComponentView(view);
        return view;
}

    @Override
    public void setComponentView(View v) {
        mImageView = v.findViewById(R.id.tutorial_fragment_iv_image);
        mImageView.setImageResource(image);
    }
}