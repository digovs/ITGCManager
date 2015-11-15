package com.vieira.rodrigo.itgcmanager;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;


public class ResetPasswordActivity extends ActionBarActivity {

    EditText emailView;
    String email;

    LinearLayout resetPasswordFormView;
    ProgressBar progressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        resetPasswordFormView = (LinearLayout) findViewById(R.id.reset_password_form);
        progressView = (ProgressBar) findViewById(R.id.reset_password_progress_bar);

        emailView = (EditText) findViewById(R.id.reset_password_form_email);


        Button resetBtn = (Button) findViewById(R.id.reset_password_button);
        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = emailView.getText().toString();
                if (!email.isEmpty()) {
                    showProgress(true);
                    ParseUser.requestPasswordResetInBackground(email, new RequestPasswordResetCallback() {
                        @Override
                        public void done(ParseException e) {
                            showProgress(false);
                            if (e == null) {
                                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(ResetPasswordActivity.this);
                                dialogBuilder.setMessage(getString(R.string.reset_password_successful));
                                dialogBuilder.setNeutralButton(R.string.add_control_error_fragment_ok_button, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                                dialogBuilder.create().show();
                            } else {
                                handleParseException(e);
                            }
                        }
                    });
                } else {
                    emailView.setError(getString(R.string.error_field_required));
                }
            }
        });

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

            case ParseException.EMAIL_NOT_FOUND:
                callErrorDialogWithMessage(getString(R.string.reset_password_email_not_found));
                break;
        }
    }

    private void callErrorDialogWithMessage(String message) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ResetPasswordActivity.this);
        alertDialog.setTitle(getString(R.string.add_control_error_fragment_title));
        alertDialog.setMessage(message);
        alertDialog.create().show();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            resetPasswordFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            resetPasswordFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    resetPasswordFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            progressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    progressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            resetPasswordFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_reset_password, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
