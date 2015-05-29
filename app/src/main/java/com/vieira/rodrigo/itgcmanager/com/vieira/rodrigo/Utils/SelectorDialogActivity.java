package com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.Utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
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
import com.vieira.rodrigo.itgcmanager.R;
import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.adapters.SelectorListAdapter;
import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.models.Control;
import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.models.Project;

import java.util.ArrayList;
import java.util.List;

public class SelectorDialogActivity extends ListActivity {

    public static final String KEY_REQUEST_CODE = "requestCode";
    public static final int REQUEST_PROJECT_MEMBER_LIST = 1;
    public static final int REQUEST_PROJECT_SYSTEM_LIST = 2;
    public static final int REQUEST_PROJECT_COMPANY_LIST = 3;
    public static final int REQUEST_CONTROL_SYSTEM_LIST = 4;
    public static final int REQUEST_CONTROL_COMPANY_LIST = 5;

    public static final String KEY_SELECTED_ITEM_ID = "selectedItemId";
    public static final String KEY_LIST_OF_IDS_TO_EXCLUDE = "IdsToExclude";

    private String currentProjectId;
    private String currentControlId;
    private int requestCode;
    private ArrayList<ParseObject> dataList = new ArrayList<>();
    private ArrayList<String> listOfIdsToExclude = new ArrayList<>();
    private SelectorListAdapter adapter;
    private ListView listView;
    private ProgressBar progressBar;
    private TextView emptyTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selector_dialog);

        requestCode = getIntent().getIntExtra(KEY_REQUEST_CODE, 0);
        listOfIdsToExclude = getIntent().getStringArrayListExtra(KEY_LIST_OF_IDS_TO_EXCLUDE);

        listView = (ListView) findViewById(android.R.id.list);
        progressBar = (ProgressBar) findViewById(R.id.selector_dialog_progress_bar);
        emptyTextView = (TextView) findViewById(R.id.selector_dialog_no_results_message);

        if (requestCode == REQUEST_PROJECT_MEMBER_LIST || requestCode == REQUEST_PROJECT_SYSTEM_LIST || requestCode == REQUEST_PROJECT_COMPANY_LIST)
            loadProjectDataList(requestCode);
        else if (requestCode == REQUEST_CONTROL_SYSTEM_LIST || requestCode == REQUEST_CONTROL_COMPANY_LIST)
            loadControlDataList(requestCode);
    }

    private void loadProjectDataList(final int requestCode) {
        currentProjectId = ParseUtils.getStringFromSession(getApplicationContext(), Project.KEY_PROJECT_ID);
        showProgress(true);

        ParseQuery<ParseObject> getProject = ParseQuery.getQuery(Project.TABLE_PROJECT);
        if (requestCode == REQUEST_PROJECT_SYSTEM_LIST)
            getProject.include(Project.KEY_SYSTEM_SCOPE_LIST);
        if (requestCode == REQUEST_PROJECT_COMPANY_LIST)
            getProject.include(Project.KEY_COMPANY_SCOPE_LIST);

        getProject.getInBackground(currentProjectId, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject projectObject, ParseException e) {
                if (e == null) {
                    switch (requestCode) {
                        case REQUEST_PROJECT_MEMBER_LIST:
                            loadProjectTeamMemberList(projectObject);
                            break;

                        case REQUEST_PROJECT_SYSTEM_LIST:
                            loadProjectSystemList(projectObject);
                            break;

                        case REQUEST_PROJECT_COMPANY_LIST:
                            loadProjectCompanyList(projectObject);
                            break;
                    }
                } else {
                    ParseUtils.handleParseException(getApplicationContext(), e);
                }
            }
        });
    }

    private void loadProjectTeamMemberList(ParseObject projectObject) {
        ParseRelation relation = projectObject.getRelation(Project.KEY_PROJECT_USER_RELATION);
        ParseQuery query = relation.getQuery();
        query.findInBackground(new FindCallback() {
            @Override
            public void done(List list, ParseException e) {
                showProgress(false);
                if (e != null)
                    ParseUtils.handleParseException(getApplicationContext(), e);
            }

            @Override
            public void done(Object o, Throwable throwable) {
                showProgress(false);
                ArrayList<ParseUser> result = (ArrayList) o;
                if (result.isEmpty()) {
                    setEmptyText(true);
                } else {
                    setEmptyText(false);
                    for (ParseUser userObject : result) {
                        if (!listOfIdsToExclude.contains(userObject.getObjectId()))
                            dataList.add(userObject);
                    }
                    if (dataList.isEmpty())
                        setEmptyText(true);
                    adapter = new SelectorListAdapter(getApplicationContext(), dataList, REQUEST_PROJECT_MEMBER_LIST);
                    setListAdapter(adapter);
                }
            }
        });

    }

    private void loadProjectSystemList(ParseObject projectObject) {
        showProgress(false);
        ArrayList<ParseObject> systemList = (ArrayList) projectObject.getList(Project.KEY_SYSTEM_SCOPE_LIST);
        for (ParseObject system : systemList) {
            if (!listOfIdsToExclude.contains(system.getObjectId())){
                dataList.add(system);
            }
        }
        adapter = new SelectorListAdapter(getApplicationContext(), dataList, REQUEST_PROJECT_SYSTEM_LIST);
        setListAdapter(adapter);
        if (dataList.isEmpty())
            setEmptyText(true);
        else
            setEmptyText(false);
    }

    private void loadProjectCompanyList(ParseObject projectObject) {
        showProgress(false);
        ArrayList<ParseObject> companyList = (ArrayList) projectObject.getList(Project.KEY_COMPANY_SCOPE_LIST);
        for (ParseObject company : companyList) {
            if (!listOfIdsToExclude.contains(company.getObjectId())){
                dataList.add(company);
            }
        }
        adapter = new SelectorListAdapter(getApplicationContext(), dataList, REQUEST_PROJECT_COMPANY_LIST);
        setListAdapter(adapter);
        if (dataList.isEmpty())
            setEmptyText(true);
        else
            setEmptyText(false);
    }

    private void loadControlDataList(final int requestCode) {
        showProgress(true);
        currentControlId = getIntent().getStringExtra(Control.KEY_CONTROL_ID);
        ParseQuery<ParseObject> getControl = ParseQuery.getQuery(Control.TABLE_CONTROL);
        getControl.getInBackground(currentControlId, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject controlObject, ParseException e) {
                if (e == null) {
                    switch (requestCode) {
                        case REQUEST_CONTROL_SYSTEM_LIST:
                            loadControlSystemList(controlObject);
                            break;

                        case REQUEST_CONTROL_COMPANY_LIST:
                            loadControlCompanyList(controlObject);
                            break;
                    }
                } else {
                    ParseUtils.handleParseException(getApplicationContext(), e);
                }
            }
        });
    }

    private void loadControlSystemList(ParseObject controlObject) {
        ParseRelation relation = controlObject.getRelation(Control.KEY_CONTROL_SYSTEM_RELATION);
        ParseQuery getControlSystemList = relation.getQuery();
        getControlSystemList.findInBackground(new FindCallback() {
            @Override
            public void done(List list, ParseException e) {
                showProgress(false);
                if (e != null)
                    ParseUtils.handleParseException(getApplicationContext(), e);
            }

            @Override
            public void done(Object result, Throwable throwable) {
                showProgress(false);
                ArrayList<ParseObject> systemList = (ArrayList) result;

                if (systemList.isEmpty())
                    setEmptyText(true);
                else {
                    setEmptyText(false);
                    for (ParseObject systemObject : systemList) {
                        if (!listOfIdsToExclude.contains(systemObject.getObjectId()))
                            dataList.add(systemObject);
                    }
                    if (dataList.isEmpty())
                        setEmptyText(true);
                    adapter = new SelectorListAdapter(getApplicationContext(), dataList, REQUEST_CONTROL_SYSTEM_LIST);
                    setListAdapter(adapter);
                }
            }
        });
    }

    private void loadControlCompanyList(ParseObject controlObject) {
        ParseRelation relation = controlObject.getRelation(Control.KEY_CONTROL_COMPANY_RELATION);
        ParseQuery getControlCompanyList = relation.getQuery();
        getControlCompanyList.findInBackground(new FindCallback() {
            @Override
            public void done(List list, ParseException e) {
                showProgress(false);
                if (e != null)
                    ParseUtils.handleParseException(getApplicationContext(), e);
            }

            @Override
            public void done(Object result, Throwable throwable) {
                showProgress(false);
                ArrayList<ParseObject> companyList = (ArrayList) result;

                if (companyList.isEmpty())
                    setEmptyText(true);
                else {
                    setEmptyText(false);
                    for (ParseObject companyObject : companyList) {
                        if (!listOfIdsToExclude.contains(companyObject.getObjectId()))
                            dataList.add(companyObject);
                    }
                    if (dataList.isEmpty())
                        setEmptyText(true);
                    adapter = new SelectorListAdapter(getApplicationContext(), dataList, REQUEST_CONTROL_COMPANY_LIST);
                    setListAdapter(adapter);
                }
            }
        });
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Intent intent = new Intent();
        intent.putExtra(KEY_SELECTED_ITEM_ID, dataList.get(position).getObjectId());
        setResult(requestCode, intent);
        finish();
    }

    public void setEmptyText(boolean show) {
        emptyTextView.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        if (emptyTextView != null && show)
            setEmptyText(false);
        else if (dataList.isEmpty())
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
