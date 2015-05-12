package com.vieira.rodrigo.itgcmanager;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;
import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.Utils.ParseUtils;
import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.models.*;

import java.util.ArrayList;


public class AddCompanyScopeActivity extends ActionBarActivity {

    private String currentProjectId;
    private ParseObject currentProjectObject;
    private ArrayList<ParseObject> alreadyAddedCompanyList = new ArrayList<>();

    private RelativeLayout companyScopeFormView;
    private EditText companyScopeNameView;
    private Button saveButton;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_company_scope);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        currentProjectId = ParseUtils.getStringFromSession(getApplicationContext(), Project.KEY_PROJECT_ID);

        companyScopeFormView = (RelativeLayout) findViewById(R.id.add_company_scope_form_view);
        companyScopeNameView = (EditText) findViewById(R.id.add_company_scope_name);
        progressBar = (ProgressBar) findViewById(R.id.add_company_scope_progress_bar);
        saveButton = (Button) findViewById(R.id.add_company_scope_button);

        loadAlreadyAddedCompanyList();

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgress(true);
                String companyName = companyScopeNameView.getText().toString();
                if (companyName.isEmpty()){
                    companyScopeNameView.setError(getString(R.string.error_field_required));
                } else if (!isNameAvailable(companyName)){
                    companyScopeNameView.setError(getString(R.string.add_company_duplicated_name_error));
                } else {
                    attemptSaveCompany(companyName);
                }
                showProgress(false);
            }
        });
    }

    private void loadAlreadyAddedCompanyList() {
        showProgress(true);
        ParseQuery<ParseObject> getProject = ParseQuery.getQuery(Project.TABLE_PROJECT);
        getProject.include(Project.KEY_COMPANY_SCOPE_LIST);
        getProject.getInBackground(currentProjectId, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                showProgress(false);
                if (e == null) {
                    currentProjectObject = parseObject;
                    alreadyAddedCompanyList = (ArrayList) parseObject.getList(Project.KEY_COMPANY_SCOPE_LIST);
                } else {
                    ParseUtils.handleParseException(getApplicationContext(), e);
                }
            }
        });
    }

    private boolean isNameAvailable(String name) {
        if (alreadyAddedCompanyList == null)
            return true;
        for (ParseObject companyObject : alreadyAddedCompanyList) {
            if (companyObject.getString(Company.KEY_COMPANY_NAME).equals(name))
                return false;
        }
        return true;
    }

    private void attemptSaveCompany(String name) {
        showProgress(true);
        final ParseObject newCompany = new ParseObject(Company.TABLE_COMPANY);
        newCompany.put(Company.KEY_COMPANY_NAME, name);

        newCompany.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    if (alreadyAddedCompanyList == null)
                        alreadyAddedCompanyList = new ArrayList<ParseObject>();
                    alreadyAddedCompanyList.add(newCompany);

                    currentProjectObject.put(Project.KEY_COMPANY_SCOPE_LIST, alreadyAddedCompanyList);
                    currentProjectObject.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            showProgress(false);
                            if (e == null) {
                                Toast.makeText(getApplicationContext(), "Saved successfuly!", Toast.LENGTH_LONG).show();
                            } else {
                                ParseUtils.handleParseException(getApplicationContext(), e);
                            }
                        }
                    });
                } else {
                    showProgress(false);
                    ParseUtils.handleParseException(getApplicationContext(), e);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_company_scope, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            companyScopeFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            companyScopeFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    companyScopeFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            companyScopeFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}
