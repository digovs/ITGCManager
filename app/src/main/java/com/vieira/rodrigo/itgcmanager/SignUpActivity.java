package com.vieira.rodrigo.itgcmanager;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.models.User;


/**
 * A login screen that offers login via email/password.
 */
public class SignUpActivity extends Activity {

    // UI references.

    private EditText fullNameView;
    private EditText emailView;
    private EditText userNameView;
    private EditText passwordView;
    private EditText confirmPasswordView;
    private View progressView;
    private TextView loadingMessage;
    private View signUpFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Set up the login form.
        fullNameView = (EditText) findViewById(R.id.sign_up_full_name);
        emailView = (EditText) findViewById(R.id.sign_up_email);

        userNameView = (EditText) findViewById(R.id.sign_up_user_name);
        passwordView = (EditText) findViewById(R.id.sign_up_password);
        confirmPasswordView = (EditText) findViewById(R.id.sign_up_confirm_password);

        Button signUpButton = (Button) findViewById(R.id.sign_up_button);
        signUpButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegister();
            }
        });

        signUpFormView = findViewById(R.id.sign_up_form);
        progressView = findViewById(R.id.sign_up_progress);
        loadingMessage = (TextView) findViewById(R.id.sign_up_loading_message);
    }

    public void attemptRegister() {
        // Reset errors.
        fullNameView.setError(null);
        emailView.setError(null);

        userNameView.setError(null);
        passwordView.setError(null);
        confirmPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String fullName = fullNameView.getText().toString();
        String email = emailView.getText().toString();

        String userName = userNameView.getText().toString();
        String password = passwordView.getText().toString();
        String confirmPassword = confirmPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (!password.equals(confirmPassword)) {
            confirmPasswordView.setError(getString(R.string.error_not_matching_passwords));
            focusView = confirmPasswordView;
            cancel = true;
        }

        if (password.isEmpty() && !isPasswordValid(password)) {
            passwordView.setError(getString(R.string.error_invalid_password));
            focusView = passwordView;
            cancel = true;
        }

        if (userName.isEmpty()) {
            userNameView.setError(getString(R.string.error_field_required));
            focusView = userNameView;
            cancel = true;
        }

        if (TextUtils.isEmpty(email)) {
            emailView.setError(getString(R.string.error_field_required));
            focusView = emailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            emailView.setError(getString(R.string.error_invalid_email));
            focusView = emailView;
            cancel = true;
        }

        if (fullName.isEmpty()) {
            fullNameView.setError(getString(R.string.error_field_required));
            focusView = fullNameView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgress(true);
            registerUserToParse(fullName, email, userName, password);
        }
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() >= 6;
    }

    private void registerUserToParse(String fullName, String email, String userName, String password) {
        ParseUser user = new ParseUser();
        user.setUsername(userName);
        user.setEmail(email);
        user.setPassword(password);

        user.put(User.KEY_USER_FULL_NAME, fullName);

        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                showProgress(false);
                if (e == null){
                    startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                    finish();
                }
                else {
                    handleParseException(e);
                }
            }
        });
    }

    private void handleParseException(ParseException exception) {
        int exceptionCode = exception.getCode();
        switch (exceptionCode) {
            case ParseException.EMAIL_TAKEN:
                emailView.setError(getString(R.string.error_unavailable_email));
                emailView.requestFocus();
                break;

            case ParseException.USERNAME_TAKEN:
                userNameView.setError(getString(R.string.error_unavailable_user_name));
                userNameView.requestFocus();
                break;
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            signUpFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            signUpFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    signUpFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            loadingMessage.setVisibility(show ? View.VISIBLE : View.GONE);
            signUpFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

}



