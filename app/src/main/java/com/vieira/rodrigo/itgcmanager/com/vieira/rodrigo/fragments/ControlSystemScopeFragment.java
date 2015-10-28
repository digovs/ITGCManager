package com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.vieira.rodrigo.itgcmanager.R;
import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.Utils.ParseUtils;
import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.models.Control;
import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.models.Project;
import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.models.SystemApp;

import java.util.ArrayList;


public class ControlSystemScopeFragment extends Fragment {

    ArrayList<CharSequence> projectSystemCharSequenceList;
    private ArrayList projectSystemObjectList;
    private ArrayList selectedSystemObjects = new ArrayList();

    Control currentControl;
    ListView systemListView;
    RelativeLayout formView;
    ArrayAdapter adapter;
    ProgressBar progressBar;

    TextView emptyText;


    Button saveButton;
    private OnFragmentInteractionListener mListener;

    public ControlSystemScopeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_control_system_scope, container, false);

        formView = (RelativeLayout) rootView.findViewById(R.id.control_system_multichoice_relative_layout);
        systemListView = (ListView) rootView.findViewById(R.id.control_system_multichoice_list);
        progressBar = (ProgressBar) rootView.findViewById(R.id.control_system_multichoice_progress_bar);
        emptyText = (TextView) rootView.findViewById(R.id.control_system_multichoice_empty_message);

        loadProjectSystemList();
        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_multiple_choice, projectSystemCharSequenceList);

        systemListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        systemListView.setAdapter(adapter);

        saveButton = (Button) rootView.findViewById(R.id.control_system_save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (systemListView.getCheckedItemCount() > 0) {
                    SparseBooleanArray checked = systemListView.getCheckedItemPositions();
                    for (int i = 0; i < checked.size(); i++) {
                        int position = checked.keyAt(i);
                        if (checked.valueAt(i) == true)
                            selectedSystemObjects.add(projectSystemObjectList.get(position));
                    }
                    mListener.addSelectedSystemsToActivityControl(selectedSystemObjects);
                    mListener.onSaveButtonClicked();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle(getString(R.string.add_control_error_fragment_title))
                            .setMessage(R.string.add_control_scope_error_fragment_message)
                            .setNeutralButton(R.string.add_control_error_fragment_ok_button, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                }
                            });
                }
            }
        });

        return rootView;
    }

    private void loadProjectSystemList() {
        showProgress(true);
        String currentProjectId = ParseUtils.getStringFromSession(getActivity(), ParseUtils.PREFS_CURRENT_PROJECT_ID);
        ParseQuery<ParseObject> getProjectCompanies = ParseQuery.getQuery(Project.TABLE_PROJECT);
        getProjectCompanies.include(Project.KEY_SYSTEM_SCOPE_LIST);
        try {
            ParseObject project = getProjectCompanies.get(currentProjectId);
            projectSystemObjectList = (ArrayList) project.getList(Project.KEY_SYSTEM_SCOPE_LIST);
            projectSystemCharSequenceList = castObjectListToCharSequenceList(projectSystemObjectList);
            showProgress(false);
        } catch (ParseException e) {
            ParseUtils.handleParseException(getActivity(), e);
            showProgress(false);
        }
    }

    private ArrayList<CharSequence> castObjectListToCharSequenceList(ArrayList<ParseObject> systemObjectList) {
        if (systemObjectList != null) {
            ArrayList<CharSequence> resultList = new ArrayList<>();
            for (ParseObject obj : systemObjectList) {
                resultList.add(obj.getString(SystemApp.KEY_SYSTEM_NAME));
            }
            return resultList;
        } else
            return new ArrayList<>();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            if (projectSystemObjectList == null || projectSystemObjectList.isEmpty())
                emptyText.setVisibility(View.VISIBLE);
            else
                emptyText.setVisibility(View.GONE);

            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            formView.setVisibility(show ? View.GONE : View.VISIBLE);
            formView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    formView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            progressBar.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            formView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {

        void onSaveButtonClicked();
        void addSelectedSystemsToActivityControl(ArrayList<ParseObject> selectedSystemItems);
    }

}
