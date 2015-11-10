package com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.fragments;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.vieira.rodrigo.itgcmanager.ControlActivity;
import com.vieira.rodrigo.itgcmanager.ProjectDashboardActivity;
import com.vieira.rodrigo.itgcmanager.R;
import com.vieira.rodrigo.itgcmanager.TestActivity;
import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.Utils.ParseUtils;
import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.adapters.TestListAdapter;
import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.models.Control;
import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.models.Project;
import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.models.Test;

import java.util.ArrayList;
import java.util.List;

public class TestListFragment extends ListFragment {


    public TestListFragment() {
        // Required empty public constructor
    }

    private TestListAdapter adapter;
    private Context context;

    private ParseObject currentProjectObject;
    private ListView listView;
    private ProgressBar progressBar;
    private TextView emptyTextView;
    private ArrayList<ParseObject> testList = new ArrayList<>();


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        context = getActivity();
        loadCurrentProjectObject();
        loadTestList();
    }

    private void loadCurrentProjectObject() {
        showProgress(true);
        String currentProjectId = ParseUtils.getStringFromSession(getActivity(), ParseUtils.PREFS_CURRENT_PROJECT_ID);
        ParseQuery getCurrentProjectObject = new ParseQuery(Project.TABLE_PROJECT);
        try {
            currentProjectObject = getCurrentProjectObject.get(currentProjectId);
            showProgress(false);
        } catch(ParseException e) {
            showProgress(false);
            ParseUtils.handleParseException(context, e);
        }
    }

    private void loadTestList() {
        showProgress(true);
        ParseQuery<ParseObject> getProjectTests = ParseQuery.getQuery(Test.TABLE_TEST);
        getProjectTests.whereEqualTo(Test.KEY_TEST_PROJECT, currentProjectObject);
        getProjectTests.include(Test.KEY_TEST_CONTROL);
        getProjectTests.include(Test.KEY_TEST_PROJECT);
        getProjectTests.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null){
                    testList = (ArrayList) list;
                    adapter = new TestListAdapter(testList, getActivity());
                    setListAdapter(adapter);
                }
                else{
                    ParseUtils.handleParseException(getActivity(), e);
                }
                showProgress(false);
            }
        });
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        if (adapter != null) {
            String selectedTestId = testList.get(position).getObjectId();
            Intent intent = new Intent(getActivity(), TestActivity.class);
            intent.putExtra(TestActivity.MODE_FLAG, TestActivity.VIEW_MODE);
            intent.putExtra(Test.KEY_TEST_OBJECT_ID, selectedTestId);
            startActivity(intent);
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
        inflater.inflate(R.menu.menu_test_fragment, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.action_add_test);
        if (item != null) {
            item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    Intent intent = new Intent(getActivity(), TestActivity.class);
                    intent.putExtra(TestActivity.MODE_FLAG, TestActivity.ADD_MODE);
                    startActivity(intent);
                    return true;
                }
            });
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_test, container, false);
        listView = (ListView) view.findViewById(android.R.id.list);
        progressBar = (ProgressBar) view.findViewById(R.id.test_list_progress_bar);
        emptyTextView = (TextView) view.findViewById(R.id.test_list_empty_message);

        /*listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
                String title = getString(R.string.control_list_long_click_dialog_title);
                String message = getString(R.string.control_list_long_click_dialog_message);
                final String selectedControlName = testList.get(position).getString(Control.KEY_CONTROL_NAME);
                message = message.replace("XXX", selectedControlName);

                dialogBuilder.setTitle(title);
                dialogBuilder.setMessage(message);
                dialogBuilder.setPositiveButton(getString(R.string.confirmation_dialog_yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getActivity(), ControlActivity.class);
                        intent.putExtra(ControlActivity.MODE_FLAG, ControlActivity.EDIT_MODE);
                        intent.putExtra(Control.KEY_CONTROL_ID, testList.get(position).getObjectId());
                        startActivity(intent);
                    }
                });
                dialogBuilder.setNegativeButton(getString(R.string.confirmation_dialog_no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                dialogBuilder.create().show();

                return true;
            }
        });*/
        return view;
    }

    @Override
    public void onResume() {
        if (adapter != null) {
            testList = new ArrayList<>();
            loadTestList();
            adapter.notifyDataSetChanged();
        }

        super.onResume();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((ProjectDashboardActivity) activity).onSectionAttached(
                ProjectDashboardActivity.TESTS_SECTION);
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
            else if (testList.isEmpty())
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
