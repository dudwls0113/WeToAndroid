package com.ninano.weto.src.main.todo_group;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ninano.weto.R;
import com.ninano.weto.src.BaseFragment;

public class ToDoGroupFragment extends BaseFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_to_do_group, container, false);
    }

    @Override
    public void setComponentView(View v) {

    }
}
