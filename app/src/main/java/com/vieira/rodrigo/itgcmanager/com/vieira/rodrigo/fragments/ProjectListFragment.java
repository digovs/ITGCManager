package com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.vieira.rodrigo.itgcmanager.CreateProjectActivity;
import com.vieira.rodrigo.itgcmanager.ProjectDashboardActivity;
import com.vieira.rodrigo.itgcmanager.R;
import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.Utils.ParseUtils;
import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.adapters.ProjectListAdapter;
import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.models.Project;

import java.util.ArrayList;
import java.util.List;

public class ProjectListFragment extends ListFragment{

    private ProjectListAdapter adapter;

    private ListView listView;
    private ProgressBar progressBar;
    private TextView emptyTextView;
    private ArrayList<Project> projectList = new ArrayList<>();
    private Activity myActivity;

    public ProjectListFragment() {
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        myActivity = getActivity();
        loadProjectList();
    }

    private void loadProjectList() {
        showProgress(true);

        ParseQuery<ParseObject> query = ParseQuery.getQuery(Project.TABLE_PROJECT);
        query.whereEqualTo(Project.KEY_PROJECT_USER_RELATION, ParseUser.getCurrentUser());

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                showProgress(false);
                if (e == null) {
                    for (ParseObject project : list) {
                        Project tempProject = new Project(project);
                        projectList.add(tempProject);
                    }
                    adapter = new ProjectListAdapter(myActivity, projectList);
                    setListAdapter(adapter);
                    if (projectList.isEmpty())
                        setEmptyText(true);
                    else
                        setEmptyText(false);
                } else {
                    projectList = new ArrayList<>();
                    ParseUtils.handleParseException(myActivity, e);
                }
            }
        });
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        if (adapter != null) {
            ParseUtils.saveStringToSession(getActivity(), Project.KEY_PROJECT_ID, projectList.get(position).getId());
            ParseUtils.saveStringToSession(getActivity(), Project.KEY_PROJECT_NAME, projectList.get(position).getName());
            startActivity(new Intent(getActivity(), ProjectDashboardActivity.class));
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_project_fragment, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.action_create_project);
        if (item != null) {
            item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                startActivity(new Intent(getActivity(), CreateProjectActivity.class));
                return true;
                }
            });
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_project, container, false);
        listView = (ListView) view.findViewById(android.R.id.list);
        progressBar = (ProgressBar) view.findViewById(R.id.project_list_progress_bar);
        emptyTextView = (TextView) view.findViewById(R.id.project_list_empty_message);
        return view;
    }

    @Override
    public void onResume() {
        if (adapter != null) {
            projectList = new ArrayList<>();
            loadProjectList();
            adapter.notifyDataSetChanged();
        }

        super.onResume();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void setEmptyText(boolean show) {
        emptyTextView.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        if (isAdded()) {
            if (emptyTextView != null && show)
                setEmptyText(false);
            else if (projectList.isEmpty())
                setEmptyText(true);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
                int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

                listView.setVisibility(show ? View.GONE : View.VISIBLE);
                listView.animate().setDuration(shortAnimTime).alpha(
                        show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        listView.setVisibility(show ? View.GONE : View.VISIBLE);
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
                listView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        }
    }

}
