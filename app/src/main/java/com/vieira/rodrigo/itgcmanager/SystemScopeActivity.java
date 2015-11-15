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
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;
import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.Utils.ParseUtils;
import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.models.*;

import java.util.ArrayList;


public class SystemScopeActivity extends ActionBarActivity {

    public static final String EDIT_MODE_FLAG = "EDIT_MODE_FLAG";
    public static final String EDIT_MODE_SYSTEM_NAME = "EDIT_MODE_SYSTEM_NAME";
    public static final String EDIT_MODE_SYSTEM_OBJECT_ID = "EDIT_MODE_SYSTEM_OBJECT_ID";

    boolean editMode;
    String editModeSystemName;
    String editModeSystemObjectId;

    private String currentProjectId;
    private ParseObject currentProjectObject;
    private ArrayList<ParseObject> alreadyAddedSystemList = new ArrayList<>();

    private RelativeLayout systemScopeFormView;
    private EditText systemScopeNameView;
    private Button saveButton;
    private ProgressBar progressBar;
    private TextView loadingMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_system_scope);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        editMode = getIntent().getBooleanExtra(EDIT_MODE_FLAG, false);
        editModeSystemName = getIntent().getStringExtra(EDIT_MODE_SYSTEM_NAME);
        editModeSystemObjectId = getIntent().getStringExtra(EDIT_MODE_SYSTEM_OBJECT_ID);

        if (editMode) {
            actionBar.setTitle(R.string.edit_system_title);
        }

        currentProjectId = ParseUtils.getStringFromSession(getApplicationContext(), ParseUtils.PREFS_CURRENT_PROJECT_ID);

        systemScopeFormView = (RelativeLayout) findViewById(R.id.add_system_scope_form_view);
        systemScopeNameView = (EditText) findViewById(R.id.add_system_scope_name);
        progressBar = (ProgressBar) findViewById(R.id.add_system_scope_progress_bar);
        loadingMessage = (TextView) findViewById(R.id.add_system_scope_loading_message);
        saveButton = (Button) findViewById(R.id.add_system_scope_button);

        if (editMode) {
            systemScopeNameView.setText(editModeSystemName);
        }

        loadAlreadyAddedSystemList();

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showProgress(true);
                String systemName = systemScopeNameView.getText().toString();
                if (systemName.isEmpty()){
                    systemScopeNameView.setError(getString(R.string.error_field_required));
                } else if (!isNameAvailable(systemName)){
                    systemScopeNameView.setError(getString(R.string.add_system_duplicated_name_error));
                } else {
                    if (editMode) {
                        attemptEditSystem(systemName);
                    } else {
                        attemptSaveSystem(systemName);
                    }
                }
                showProgress(false);
            }
        });
    }

    private void attemptEditSystem(final String systemName) {
        showProgress(true);
        ParseQuery<ParseObject> getCurrentSystem = ParseQuery.getQuery(SystemApp.TABLE_SYSTEM);
        getCurrentSystem.getInBackground(editModeSystemObjectId, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject systemParseObject, ParseException e) {
                if (e == null) {
                    systemParseObject.put(SystemApp.KEY_SYSTEM_NAME, systemName);
                    try {
                        systemParseObject.save();
                        showProgress(false);
                        finish();
                    } catch (ParseException e1) {
                        ParseUtils.handleParseException(getApplicationContext(), e1);
                    }
                } else {
                    ParseUtils.handleParseException(getApplicationContext(), e);
                }
            }
        });
    }

    private void loadAlreadyAddedSystemList() {
        showProgress(true);
        ParseQuery<ParseObject> getProject = ParseQuery.getQuery(Project.TABLE_PROJECT);
        getProject.include(Project.KEY_SYSTEM_SCOPE_LIST);
        getProject.getInBackground(currentProjectId, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                showProgress(false);
                if (e == null) {
                    currentProjectObject = parseObject;
                    alreadyAddedSystemList = (ArrayList) parseObject.getList(Project.KEY_SYSTEM_SCOPE_LIST);
                } else {
                    ParseUtils.handleParseException(getApplicationContext(), e);
                }
            }
        });
    }

    private boolean isNameAvailable(String name) {
        if (alreadyAddedSystemList == null)
            return true;
        for (ParseObject systemObject : alreadyAddedSystemList) {
            if (systemObject.getString(SystemApp.KEY_SYSTEM_NAME).equals(name) && !systemObject.getString(Company.KEY_COMPANY_NAME).equals(editModeSystemName))
                return false;
        }
        return true;
    }

    private void attemptSaveSystem(String name) {
        showProgress(true);
        final ParseObject newSystem = new ParseObject(SystemApp.TABLE_SYSTEM);
        newSystem.put(SystemApp.KEY_SYSTEM_NAME, name);

        newSystem.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    if (alreadyAddedSystemList == null)
                        alreadyAddedSystemList = new ArrayList<ParseObject>();
                    alreadyAddedSystemList.add(newSystem);

                    currentProjectObject.put(Project.KEY_SYSTEM_SCOPE_LIST, alreadyAddedSystemList);
                    currentProjectObject.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            showProgress(false);
                            if (e == null){
                                Toast.makeText(getApplicationContext(), getString(R.string.dialog_message_saved_successfully), Toast.LENGTH_LONG).show();
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
        getMenuInflater().inflate(R.menu.menu_add_system_scope, menu);
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

            systemScopeFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            systemScopeFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    systemScopeFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            systemScopeFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}
