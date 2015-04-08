package com.vieira.rodrigo.itgcmanager;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
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

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.models.Project;


public class CreateProjectActivity extends ActionBarActivity {

    EditText projectNameView;
    Button createProjectBtn;

    ProgressBar progressBar;
    RelativeLayout createProjectFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_project);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        createProjectFormView = (RelativeLayout) findViewById(R.id.create_project_form_view);
        projectNameView = (EditText) findViewById(R.id.create_project_name);
        progressBar = (ProgressBar) findViewById(R.id.create_project_progress_bar);

        createProjectBtn = (Button) findViewById(R.id.create_project_button);
        createProjectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String projectName = projectNameView.getText().toString();
                if (!projectName.isEmpty())
                    attemptSaveProject(projectName);
                else{
                    projectNameView.setError(getString(R.string.error_field_required));
                }
            }
        });

    }

    private void attemptSaveProject(String projectName) {
        showProgress(true);
        final ParseObject project = new ParseObject(Project.TABLE_PROJECT);
        project.put(Project.KEY_PROJECT_NAME, projectName);
        project.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    linkUserToProject(ParseUser.getCurrentUser(), project);
                } else {
                    showProgress(false);
                    handleParseException(e);
                }
            }
        });
    }

    private void linkUserToProject(ParseUser user, ParseObject project) {
        String userId = user.getObjectId();
        String projectId = project.getObjectId();

        ParseObject projectUser = new ParseObject(Project.TABLE_PROJECT_USER);
        projectUser.put(Project.KEY_PROJECT_ID, projectId);
        projectUser.put(Project.KEY_USER_ID, userId);
        projectUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    showProgress(false);
                    // TODO IR PARA SEGUNDA PARTE DO FLUXO DE PLANNING
                } else {
                    handleParseException(e);
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
        return super.onOptionsItemSelected(item);
    }

    private void callErrorDialogWithMessage(String message) {
        Intent i = new Intent(getApplicationContext(), ErrorMessageDialogActivity.class);
        i.putExtra(ErrorMessageDialogActivity.KEY_MESSAGE_TEXT, message);
        startActivity(i);
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
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
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
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            createProjectFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}
