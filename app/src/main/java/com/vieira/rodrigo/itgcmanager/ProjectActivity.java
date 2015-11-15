package com.vieira.rodrigo.itgcmanager;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Intent;
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

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.Utils.ParseUtils;
import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.models.Project;

import java.util.ArrayList;
import java.util.List;


public class ProjectActivity extends ActionBarActivity {

    public static final String EDIT_MODE_FLAG = "EDIT_MODE_FLAG";
    public static final String EDIT_MODE_PROJECT_NAME = "EDIT_MODE_PROJECT_NAME";

    boolean editMode;
    String editModeProjectName;
    ArrayList<String> userProjectNameList = new ArrayList<>();

    EditText projectNameView;
    Button createProjectBtn;

    ProgressBar progressBar;
    TextView loadingMessage;
    RelativeLayout createProjectFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_project);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        editMode = getIntent().getBooleanExtra(EDIT_MODE_FLAG, false);
        editModeProjectName = getIntent().getStringExtra(EDIT_MODE_PROJECT_NAME);

        if (editMode) {
            actionBar.setTitle(getString(R.string.edit_project_title));
        }

        createProjectFormView = (RelativeLayout) findViewById(R.id.create_project_form_view);
        progressBar = (ProgressBar) findViewById(R.id.create_project_progress_bar);
        loadingMessage = (TextView) findViewById(R.id.create_project_loading_message);
        createProjectBtn = (Button) findViewById(R.id.create_project_button);
        projectNameView = (EditText) findViewById(R.id.create_project_name);
        if (editMode){
            projectNameView.setText(editModeProjectName);
            createProjectBtn.setText(getString(R.string.edit_project_button_text));
        }
        createProjectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String projectName = projectNameView.getText().toString();
                if (projectName.isEmpty()) {
                    projectNameView.setError(getString(R.string.error_field_required));

                } else if (userProjectNameList != null && userProjectNameList.contains(projectName)) {
                    projectNameView.setError(getString(R.string.create_project_not_available_name_message));
                } else {
                    projectNameView.setError(null);
                    if (editMode){
                        attemptEditProject(projectName);
                    } else {
                        attemptSaveProject(projectName);
                    }

                }
            }
        });

        loadListWIthUserProjects();
    }

    private void attemptEditProject(final String projectName) {
        showProgress(true, getString(R.string.loading_dialog_message_saving_project));
        String currentProjectId = ParseUtils.getStringFromSession(getApplicationContext(), ParseUtils.PREFS_CURRENT_PROJECT_ID);
        ParseQuery<ParseObject> getProject = ParseQuery.getQuery(Project.TABLE_PROJECT);
        getProject.getInBackground(currentProjectId, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject currentProjectParseObject, ParseException e) {
                if (e == null){
                    currentProjectParseObject.put(Project.KEY_PROJECT_NAME, projectName);
                    try {
                        currentProjectParseObject.save();
                        showProgress(false, "");
                        ParseUtils.saveStringToSession(getApplicationContext(), ParseUtils.PREFS_CURRENT_PROJECT_NAME, projectName);
                        Intent intent = new Intent(getApplicationContext(), ProjectDashboardActivity.class);
                        intent.putExtra(EDIT_MODE_FLAG, true);
                        startActivity(intent);
                        finish();
                    } catch (ParseException e1) {
                        ParseUtils.handleParseException(getApplicationContext(), e1);
                        finish();
                    }
                } else {
                    ParseUtils.handleParseException(getApplicationContext(), e);
                    finish();
                }
            }
        });
    }

    private void attemptSaveProject(String projectName) {
        showProgress(true, getString(R.string.loading_dialog_message_saving_project));
        final ParseObject newProjectObject = new ParseObject(Project.TABLE_PROJECT);
        newProjectObject.put(Project.KEY_PROJECT_NAME, projectName);
        newProjectObject.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    attemptSaveProjectUserRelationship(newProjectObject);
                } else {
                    showProgress(false, "");
                    handleParseException(e);
                }
            }
        });
    }

    private void attemptSaveProjectUserRelationship(final ParseObject newProjectObject) {
        ParseRelation<ParseObject> relation = newProjectObject.getRelation(Project.KEY_PROJECT_USER_RELATION);
        relation.add(ParseUser.getCurrentUser());

        newProjectObject.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                showProgress(false, "");
                if (e == null) {
                    ParseUtils.saveStringToSession(getApplicationContext(), ParseUtils.PREFS_CURRENT_PROJECT_ID, newProjectObject.getObjectId());
                    ParseUtils.saveStringToSession(getApplicationContext(), ParseUtils.PREFS_CURRENT_PROJECT_NAME, newProjectObject.getString(Project.KEY_PROJECT_NAME));
                    startActivity(new Intent(getApplicationContext(), ProjectDashboardActivity.class));
                    finish();
                } else {
                    handleParseException(e);
                }
            }
        });
    }

    private void loadListWIthUserProjects() {
        showProgress(true, getString(R.string.loading_dialog_message_loading_form));

        ParseQuery<ParseObject> query = ParseQuery.getQuery(Project.TABLE_PROJECT);
        query.whereEqualTo(Project.KEY_PROJECT_USER_RELATION, ParseUser.getCurrentUser());

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                showProgress(false, "");
                if (e == null) {
                    for (ParseObject project : list) {
                        userProjectNameList.add(project.getString(Project.KEY_PROJECT_NAME));
                    }
                    // If on edit mode, remove the name of the project from the verifying list
                    userProjectNameList.remove(editModeProjectName);
                } else {
                    userProjectNameList = new ArrayList<>();
                    ParseUtils.handleParseException(getApplicationContext(), e);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_create_project, menu);
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

    private void callErrorDialogWithMessage(String message) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ProjectActivity.this);
        alertDialog.setTitle(getString(R.string.add_control_error_fragment_title));
        alertDialog.setMessage(message);
        alertDialog.create().show();
    }

    private void handleParseException(ParseException exception) {
        int exceptionCode = exception.getCode();
        switch (exceptionCode) {
            case ParseException.CONNECTION_FAILED:
                callErrorDialogWithMessage(getString(R.string.connection_failed));
                break;

            case ParseException.TIMEOUT:
                callErrorDialogWithMessage(getString(R.string.connection_timeout));
                break;
        }
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show, String message) {
        loadingMessage.setText(message);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            createProjectFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            createProjectFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    createProjectFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            createProjectFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}
