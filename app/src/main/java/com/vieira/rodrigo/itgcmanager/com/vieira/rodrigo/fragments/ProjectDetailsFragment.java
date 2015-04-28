package com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.fragments;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vieira.rodrigo.itgcmanager.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProjectDetailsFragment extends Fragment {


    public ProjectDetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_project_details, container, false);
    }


}
