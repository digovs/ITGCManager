package com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.fragments;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.parse.CountCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.vieira.rodrigo.itgcmanager.ProjectDashboardActivity;
import com.vieira.rodrigo.itgcmanager.R;
import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.Utils.ParseUtils;
import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.models.Control;
import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.models.Project;
import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.models.Test;
import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.models.User;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class MemberDetailsFragment extends Fragment {

    private ParseObject memberObject;
    private String memberId;
    private ParseObject projectObject;
    private TextView memberNameView;

    ProgressBar progressBar;
    TextView loadingMessage;
    LinearLayout linearLayout;

    private TextView numberOfControlsView;
    private TextView numberOfTestsView;
    private TextView numberOfTestsPendingView;
    private TextView numberOfTestsReturnedView;
    private TextView numberOfTestsReceivedView;
    private TextView numberOfTestsOnProgressView;
    private TextView numberOfTestsTestedView;
    private TextView numberOfTestsFinishedView;

    private String numberOfControlsLabel;
    private String numberOfTestsLabel;
    private String numberOfPendingTestsLabel;
    private String numberOfReturnedTestsLabel;
    private String numberOfReceivedTestsLabel;
    private String numberOfOnProgressTestsLabel;
    private String numberOfTestedTestsLabel;
    private String numberOfFinishedTestsLabel;

    public MemberDetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_member_details, container, false);

        progressBar = (ProgressBar) view.findViewById(R.id.member_details_progress_bar);
        loadingMessage = (TextView) view.findViewById(R.id.member_details_loading_message);
        linearLayout = (LinearLayout) view.findViewById(R.id.member_details_linear_layout);

        memberNameView = (TextView) view.findViewById(R.id.member_details_name);

        numberOfControlsLabel = getString(R.string.project_details_number_of_controls_label);
        numberOfTestsLabel = getString(R.string.project_details_number_of_tests_label);
        numberOfPendingTestsLabel = getString(R.string.project_details_number_of_tests_pending_label);
        numberOfReturnedTestsLabel = getString(R.string.project_details_number_of_tests_returned_label);
        numberOfReceivedTestsLabel = getString(R.string.project_details_number_of_tests_received_label);
        numberOfOnProgressTestsLabel = getString(R.string.project_details_number_of_tests_on_progress_label);
        numberOfTestedTestsLabel = getString(R.string.project_details_number_of_tests_tested_label);
        numberOfFinishedTestsLabel = getString(R.string.project_details_number_of_tests_finished_label);

        numberOfControlsView = (TextView) view.findViewById(R.id.member_details_number_of_controls);
        numberOfTestsView = (TextView) view.findViewById(R.id.member_details_number_of_tests);
        numberOfTestsPendingView = (TextView) view.findViewById(R.id.member_details_number_of_tests_pending);
        numberOfTestsReturnedView = (TextView) view.findViewById(R.id.member_details_number_of_tests_returned);
        numberOfTestsReceivedView = (TextView) view.findViewById(R.id.member_details_number_of_tests_received);
        numberOfTestsOnProgressView = (TextView) view.findViewById(R.id.member_details_number_of_tests_on_progress);
        numberOfTestsTestedView = (TextView) view.findViewById(R.id.member_details_number_of_tests_tested);
        numberOfTestsFinishedView = (TextView) view.findViewById(R.id.member_details_number_of_tests_finished);

        loadContent();

        return view;
    }

    private void loadContent() {
        loadMemberObject();
        loadProjectObject();
        loadNumberOfControls();
        loadTests();
    }

    private void loadMemberObject() {
        showProgress(true);
        ParseQuery<ParseUser> getCurrentMember = ParseUser.getQuery();
        try {
            memberObject = getCurrentMember.get(memberId);
            memberNameView.setText(memberObject.getString(User.KEY_USER_FULL_NAME));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("SetTextI18n")
    private void loadProjectObject() {
        showProgress(true);
        String projectId = ParseUtils.getStringFromSession(getActivity(), ParseUtils.PREFS_CURRENT_PROJECT_ID);
        ParseQuery<ParseObject> getProjectObject = ParseQuery.getQuery(Project.TABLE_PROJECT);
        try {
            projectObject = getProjectObject.get(projectId);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        showProgress(false);
    }

    private void loadNumberOfControls() {
        ParseQuery<ParseObject> getNumberOfControls = ParseQuery.getQuery(Control.TABLE_CONTROL);
        getNumberOfControls.whereEqualTo(Control.KEY_CONTROL_PROJECT, projectObject);
        getNumberOfControls.whereEqualTo(Control.KEY_CONTROL_MEMBER_RESPONSIBLE, memberObject);
        getNumberOfControls.countInBackground(new CountCallback() {
            @SuppressLint("SetTextI18n")
            @Override
            public void done(int i, ParseException e) {
                if (e != null)
                    numberOfControlsView.setText(numberOfControlsLabel + "0");
                else
                    numberOfControlsView.setText(numberOfControlsLabel + i);
            }
        });
    }

    private void loadTests() {
        ParseQuery<ParseObject> getTests = ParseQuery.getQuery(Test.TABLE_TEST);
        getTests.whereEqualTo(Test.KEY_TEST_PROJECT, projectObject);
        getTests.whereEqualTo(Test.KEY_TEST_MEMBER_RESPONSIBLE, memberObject);
        getTests.findInBackground(new FindCallback<ParseObject>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e != null) {
                    numberOfTestsView.setText(numberOfTestsLabel + 0);
                    numberOfTestsTestedView.setText(numberOfTestedTestsLabel + 0);
                    numberOfTestsPendingView.setText(numberOfPendingTestsLabel + 0);
                    numberOfTestsFinishedView.setText(numberOfFinishedTestsLabel + 0);
                    numberOfTestsOnProgressView.setText(numberOfOnProgressTestsLabel + 0);
                    numberOfTestsReturnedView.setText(numberOfReturnedTestsLabel + 0);
                    numberOfTestsReceivedView.setText(numberOfReceivedTestsLabel + 0);
                } else {
                    loadNumberOfTestsByStatus(list);
                }
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void loadNumberOfTestsByStatus(List<ParseObject> listOfTests) {
        int pending = 0;
        int received = 0;
        int returned = 0;
        int onProgress = 0;
        int tested = 0;
        int finished = 0;

        for (ParseObject testObject : listOfTests) {
            ParseObject testStatus = testObject.getParseObject(Test.KEY_TEST_STATUS);
            String statusId = testStatus.getObjectId();

            String STATUS_PENDING = "b7r0mMiU6W";
            String STATUS_RETURNED = "BsbmlUkpW8";
            String STATUS_RECEIVED = "bbpeaXnOWG";
            String STATUS_TESTED = "PZVCp92znl";
            String STATUS_ON_PROGRESS = "ZYJriLARa0";
            String STATUS_FINISHED = "yHecbwLCJL";

            if (statusId.equals(STATUS_FINISHED))
                finished++;
            else if (statusId.equals(STATUS_ON_PROGRESS))
                onProgress++;
            else if (statusId.equals(STATUS_PENDING))
                pending++;
            else if (statusId.equals(STATUS_RECEIVED))
                received++;
            else if (statusId.equals(STATUS_RETURNED))
                returned++;
            else if (statusId.equals(STATUS_TESTED))
                tested++;
        }

        numberOfTestsView.setText(numberOfTestsLabel + listOfTests.size());
        numberOfTestsTestedView.setText(numberOfTestedTestsLabel + tested);
        numberOfTestsPendingView.setText(numberOfPendingTestsLabel + pending);
        numberOfTestsFinishedView.setText(numberOfFinishedTestsLabel + finished);
        numberOfTestsOnProgressView.setText(numberOfOnProgressTestsLabel + onProgress);
        numberOfTestsReturnedView.setText(numberOfReturnedTestsLabel + returned);
        numberOfTestsReceivedView.setText(numberOfReceivedTestsLabel + received);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Bundle args = getArguments();
        if (args != null)
            memberId = getArguments().getString(ProjectDashboardActivity.KEY_MEMBER_ID, "");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().finish();
        }
        return true;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        if (isAdded()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
                int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

                linearLayout.setVisibility(show ? View.GONE : View.VISIBLE);
                linearLayout.animate().setDuration(shortAnimTime).alpha(
                        show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        linearLayout.setVisibility(show ? View.GONE : View.VISIBLE);
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
                loadingMessage.setVisibility(show ? View.VISIBLE : View.GONE);
                loadingMessage.animate().setDuration(shortAnimTime).alpha(
                        show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        loadingMessage.setVisibility(show ? View.VISIBLE : View.GONE);
                    }
                });
            } else {
                // The ViewPropertyAnimator APIs are not available, so simply show
                // and hide the relevant UI components.
                progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
                loadingMessage.setVisibility(show ? View.VISIBLE : View.GONE);
                linearLayout.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        }
    }
}