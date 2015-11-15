package com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.vieira.rodrigo.itgcmanager.R;
import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.Utils.ParseUtils;
import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.adapters.MemberListAdapter;
import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.models.Project;
import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.models.User;

import java.util.ArrayList;
import java.util.List;


public class AddMemberFragment extends ListFragment {

    public static final int ADD_MEMBER_ID = 0;

    private ArrayList<User> userList = new ArrayList<>();
    private ArrayList<User> alreadyAddedUserList = new ArrayList<>();
    private MemberListAdapter adapter;

    private EditText searchView;
    private ListView listView;
    private ProgressBar progressBar;
    private TextView loadingMessage;
    private TextView emptyResultMessage;

    private String selectedUserId;
    private String currentProjectId;

    public AddMemberFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_member, container, false);

        currentProjectId = ParseUtils.getStringFromSession(getActivity(), ParseUtils.PREFS_CURRENT_PROJECT_ID);

        listView = (ListView) view.findViewById(android.R.id.list);
        progressBar = (ProgressBar) view.findViewById(R.id.add_member_progress_bar);
        loadingMessage = (TextView) view.findViewById(R.id.add_member_loading_message);

        searchView = (EditText) view.findViewById(R.id.add_member_edit_text);
        emptyResultMessage = (TextView) view.findViewById(R.id.add_member_not_found_message);

        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String userName = searchView.getText().toString();
                if (userName.length() >= 3) {
                    searchForUser(userName);
                } else
                    resetList();
            }
        });

        loadAlreadyAddedUserList();
        return view;
    }

    private void loadAlreadyAddedUserList() {
        showProgress(true, getString(R.string.loading_dialog_message_loading_form));
        searchView.setVisibility(View.GONE);

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
                            showProgress(false, "");
                            searchView.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void done(Object o, Throwable throwable) {
                            showProgress(false, "");
                            searchView.setVisibility(View.VISIBLE);
                            ArrayList<ParseUser> result = (ArrayList) o;
                            for (ParseUser user : result) {
                                User tempUser = new User(user);
                                alreadyAddedUserList.add(tempUser);
                            }
                        }
                    });
                } else{
                    ParseUtils.handleParseException(getActivity(), e);
                }
            }
        });

    }

    private void searchForUser(String query) {
        emptyResultMessage.setVisibility(View.GONE);
        showProgress(false, getString(R.string.loading_dialog_message_searching));
        ParseQuery<ParseUser> userNameQuery = ParseUser.getQuery();
        userNameQuery.whereContains(User.KEY_USER_NAME, query);

        ParseQuery<ParseUser> fullNameQuery = ParseUser.getQuery();
        fullNameQuery.whereContains(User.KEY_USER_FULL_NAME, query);

        List<ParseQuery<ParseUser>> queries = new ArrayList<>();
        queries.add(userNameQuery);
        queries.add(fullNameQuery);

        ParseQuery<ParseUser> mainQuery = ParseQuery.or(queries);

        mainQuery.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> list, ParseException e) {
                showProgress(false, "");
                if (e == null) {
                    userList = new ArrayList<>();
                    for (ParseUser object : list) {
                        User tempUser = new User(object);
                        if (!alreadyAddedUserList.contains(tempUser))
                            userList.add(tempUser);
                    }
                    if (adapter != null) {
                        adapter.swapList(userList);
                    } else {
                        adapter = new MemberListAdapter(getActivity(), userList);
                        setListAdapter(adapter);
                    }
                    if (userList.isEmpty()) {
                        emptyResultMessage.setVisibility(View.VISIBLE);
                    } else
                        emptyResultMessage.setVisibility(View.GONE);
                } else
                    ParseUtils.handleParseException(getActivity(), e);
            }
        });
    }


    private void resetList() {
        if (adapter != null) {
            adapter.swapList(new ArrayList<User>());
        } else {
            adapter = new MemberListAdapter(getActivity(), new ArrayList<User>());
            setListAdapter(adapter);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        ActionBar actionBar = ((ActionBarActivity) getActivity()).getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        selectedUserId = userList.get(position).getId();
        String selectedUserName = userList.get(position).getUserName();
        String message = getString(R.string.confirmation_dialog_add_member_message);
        message = message.replace("XXX", selectedUserName);

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        dialogBuilder.setMessage(message);
        dialogBuilder.setPositiveButton(getString(R.string.confirmation_dialog_yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                attemptSaveMemberToProject();
            }
        });
        dialogBuilder.setNegativeButton(getString(R.string.confirmation_dialog_no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialogBuilder.create().show();
    }

    private void attemptSaveMemberToProject() {
        searchView.setVisibility(View.GONE);
        showProgress(true, getString(R.string.loading_dialog_message_saving_member_to_project));
        if (selectedUserId != null) {
            final ParseObject projectObject = ParseObject.createWithoutData(Project.TABLE_PROJECT, currentProjectId);

            ParseQuery<ParseUser> query = ParseUser.getQuery();
            query.getInBackground(selectedUserId, new GetCallback<ParseUser>() {
                @Override
                public void done(ParseUser parseUser, ParseException e) {
                    searchView.setVisibility(View.VISIBLE);
                    if (e == null){
                        ParseRelation<ParseObject> relation = projectObject.getRelation(Project.KEY_PROJECT_USER_RELATION);
                        relation.add(parseUser);

                        showProgress(false, "");
                        try {
                            projectObject.save();
                            Toast.makeText(getActivity(), getString(R.string.dialog_message_saved_successfully), Toast.LENGTH_LONG).show();
                            User tempUser = new User(parseUser);
                            alreadyAddedUserList.add(tempUser);
                            resetList();
                        } catch (Exception exception) {
                            String message = exception.getMessage();
                            Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                        }
                    } else {
                        showProgress(false, "");
                        ParseUtils.handleParseException(getActivity(), e);
                    }
                }
            });
        }
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
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().finish();
        }
        return true;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show, String message) {
        loadingMessage.setText(message);
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
            listView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}
