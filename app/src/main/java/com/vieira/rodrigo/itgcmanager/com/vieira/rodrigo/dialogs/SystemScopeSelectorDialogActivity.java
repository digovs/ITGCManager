package com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.dialogs;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;
import com.vieira.rodrigo.itgcmanager.AddControlActivity;
import com.vieira.rodrigo.itgcmanager.R;
import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.Utils.ParseUtils;
import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.adapters.SelectorListAdapter;
import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.models.Company;
import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.models.Project;
import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.models.Scope;
import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.models.SystemApp;

import java.util.ArrayList;

public class SystemScopeSelectorDialogActivity extends FragmentActivity implements SelectorDialogFragment.SelectorDialogListener{

    public static final String KEY_SELECTED_SYSTEM_ID = "selectedSystemId";
    public static final String KEY_LIST_OF_IDS_TO_EXCLUDE = "IdsToExclude";
    public static final String KEY_SCOPE_ID = "scopeId";


    private String currentProjectId;
    private int requestCode;
    private ArrayList<ParseObject> systemList = new ArrayList<>();
    private ArrayList<String> listOfIdsToExclude = new ArrayList<>();
    private SelectorListAdapter adapter;
    private ListView listView;
    private ProgressBar progressBar;
    private TextView emptyTextView;
    private ArrayList<ParseObject> companyObjectList = new ArrayList<>();
    private ArrayList<ParseObject> selectedCompanyList = new ArrayList<>();
    private ParseObject selectedSystem;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_selector_dialog);

        requestCode = getIntent().getIntExtra(AddControlActivity.REQUEST_CODE, 0);
        listOfIdsToExclude = getIntent().getStringArrayListExtra(KEY_LIST_OF_IDS_TO_EXCLUDE);

        progressBar = (ProgressBar) findViewById(R.id.system_selector_dialog_progress_bar);
        emptyTextView = (TextView) findViewById(R.id.system_selector_dialog_no_results_message);
        listView = (ListView) findViewById(R.id.system_selector_dialog_list);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedSystem = systemList.get(position);
                String systemName = selectedSystem.getString(SystemApp.KEY_SYSTEM_NAME);
                SelectorDialogFragment selectorFragment = new SelectorDialogFragment();
                Bundle args = new Bundle();
                args.putString(SelectorDialogFragment.KEY_TITLE_ARGS, systemName);

                CharSequence[] items = new CharSequence[companyObjectList.size()];
                for (int i = 0; i < companyObjectList.size(); i++) {
                    items[i] = companyObjectList.get(i).getString(Company.KEY_COMPANY_NAME);
                }
                args.putCharSequenceArray(SelectorDialogFragment.KEY_ITEM_LIST_ARGS, items);

                selectorFragment.setArguments(args);
                selectorFragment.show(getSupportFragmentManager(), "TAG");
            }
        });

        loadDataList();
    }

    private void loadDataList() {
        currentProjectId = ParseUtils.getStringFromSession(getApplicationContext(), Project.KEY_PROJECT_ID);
        showProgress(true);

        ParseQuery<ParseObject> getProject = ParseQuery.getQuery(Project.TABLE_PROJECT);
        getProject.include(Project.KEY_SYSTEM_SCOPE_LIST);
        getProject.include(Project.KEY_COMPANY_SCOPE_LIST);
        getProject.getInBackground(currentProjectId, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject projectObject, ParseException e) {
                if (e == null) {
                    loadProjectSystemList(projectObject);
                    loadProjectCompanyList(projectObject);
                } else {
                    ParseUtils.handleParseException(getApplicationContext(), e);
                }
            }
        });
    }

    private void loadProjectSystemList(ParseObject projectObject) {
        showProgress(false);
        ArrayList<ParseObject> systemList = (ArrayList) projectObject.getList(Project.KEY_SYSTEM_SCOPE_LIST);
        if (systemList == null)
            systemList = new ArrayList<>();
        if (listOfIdsToExclude == null) {
            this.systemList = systemList;
        } else {
            for (ParseObject system : systemList) {
                if (!listOfIdsToExclude.contains(system.getObjectId())) {
                    this.systemList.add(system);
                }
            }
        }
        adapter = new SelectorListAdapter(getApplicationContext(), this.systemList, requestCode);
        listView.setAdapter(adapter);
        if (this.systemList.isEmpty())
            setEmptyText(true);
        else
            setEmptyText(false);
    }

    private void loadProjectCompanyList(ParseObject projectObject) {
        showProgress(false);
        ArrayList<ParseObject> companyList = (ArrayList) projectObject.getList(Project.KEY_COMPANY_SCOPE_LIST);
        if (companyList == null)
            companyList = new ArrayList<>();
        companyObjectList = companyList;
    }

    @Override
    public void onSaveButtonClicked(boolean[] selectedItems) {
        showProgress(true);
        for (int i = 0; i < selectedItems.length; i++) {
            if (selectedItems[i] == true)
                selectedCompanyList.add(companyObjectList.get(i));
        }
        attemptSavingControlScope();
    }

    private void attemptSavingControlScope() {
        final ParseObject newScope = new ParseObject(Scope.TABLE_SCOPE);
        newScope.put(Scope.KEY_SYSTEM, selectedSystem);
        newScope.put(Scope.KEY_COMPANY_LIST, selectedCompanyList);

        newScope.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Intent intent = new Intent();
                    intent.putExtra(KEY_SCOPE_ID, newScope.getObjectId());
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    ParseUtils.handleParseException(getApplicationContext(), e);
                    setResult(RESULT_CANCELED);
                    finish();
                }

            }
        });
    }

    @Override
    public void onCancelButtonClicked() {
        setResult(RESULT_CANCELED);
        finish();
    }

    public void setEmptyText(boolean show) {
        emptyTextView.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        if (emptyTextView != null && show)
            setEmptyText(false);
        else if (systemList.isEmpty())
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
