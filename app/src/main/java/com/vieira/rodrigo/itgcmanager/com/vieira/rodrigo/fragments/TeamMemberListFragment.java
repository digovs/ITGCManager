package com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.fragments;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
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
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.vieira.rodrigo.itgcmanager.AddMemberActivity;
import com.vieira.rodrigo.itgcmanager.CreateProjectActivity;
import com.vieira.rodrigo.itgcmanager.ProjectDashboardActivity;
import com.vieira.rodrigo.itgcmanager.R;
import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.Utils.ParseUtils;
import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.adapters.MemberListAdapter;
import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.adapters.ProjectListAdapter;
import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.models.Project;
import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.models.User;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class TeamMemberListFragment extends ListFragment {


    private MemberListAdapter adapter;
    private String currentProjectId;

    private ListView listView;
    private ProgressBar progressBar;
    private TextView emptyTextView;
    private ArrayList<User> members = new ArrayList<>();

    public TeamMemberListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        ActionBar actionBar = ((ActionBarActivity) getActivity()).getSupportActionBar();
        actionBar.setTitle(getString(R.string.team_member_fragment_title));

        View view = inflater.inflate(R.layout.fragment_team_member_list, container, false);
        listView = (ListView) view.findViewById(android.R.id.list);
        progressBar = (ProgressBar) view.findViewById(R.id.team_member_list_progress_bar);
        emptyTextView = (TextView) view.findViewById(R.id.team_member_list_empty_message);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_team_member_fragment, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.action_add_member);
        if (item != null) {
            item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    startActivity(new Intent(getActivity(), AddMemberActivity.class));
                    return true;
                }
            });
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        currentProjectId = ParseUtils.getStringFromSession(getActivity(), Project.KEY_PROJECT_ID);
        loadTeamMemberList();
    }

    private void loadTeamMemberList() {
        showProgress(true);

        ParseQuery<ParseObject> query = ParseQuery.getQuery(Project.TABLE_PROJECT);
        query.getInBackground(currentProjectId, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject projectObject, ParseException e) {
                if (e == null) {
                    ParseRelation relation = projectObject.getRelation(Project.KEY_PROJECT_USER_RELATION);
                    ParseQuery query = relation.getQuery();
                    query.findInBackground(new FindCallback() {
                        @Override
                        public void done(List list, ParseException e) {

                        }

                        @Override
                        public void done(Object o, Throwable throwable) {
                            showProgress(false);
                            ArrayList<ParseUser> result = (ArrayList) o;
                            if (result.isEmpty()) {
                                setEmptyText(true);
                            } else {
                                for (ParseUser user : result) {
                                    User tempUser = new User(user);
                                    members.add(tempUser);
                                }

                                adapter = new MemberListAdapter(getActivity(), members);
                                setListAdapter(adapter);
                            }
                        }
                    });
                } else {
                    ParseUtils.handleParseException(getActivity(), e);
                }
            }
        });
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        if (adapter != null) {
            ParseUtils.saveStringToSession(getActivity(), User.KEY_SELECTED_USER_ID, members.get(position).getId());
            ParseUtils.saveStringToSession(getActivity(), User.KEY_SELECTED_USER_NAME, members.get(position).getUserName());
            // TODO dashboard de atividades de usuarios
            startActivity(new Intent(getActivity(), ProjectDashboardActivity.class));
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((ProjectDashboardActivity) activity).onSectionAttached(
                ProjectDashboardActivity.TEAM_SECTION);
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
            if (emptyTextView != null)
                setEmptyText(show);

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
