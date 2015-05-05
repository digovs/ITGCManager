package com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.fragments;


import android.app.Activity;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vieira.rodrigo.itgcmanager.ProjectDashboardActivity;
import com.vieira.rodrigo.itgcmanager.R;
import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.Utils.ParseUtils;
import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.models.Project;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProjectDetailsFragment extends Fragment {

    private String currentProjectname;
    private TextView projectNameView;

    public ProjectDetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_project_details, container, false);

        currentProjectname = ParseUtils.getStringFromSession(getActivity(), Project.KEY_PROJECT_NAME);

        projectNameView = (TextView) view.findViewById(R.id.project_details_name);
        projectNameView.setText(currentProjectname);

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((ProjectDashboardActivity) activity).onSectionAttached(
                ProjectDashboardActivity.HOME_SECTION);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
