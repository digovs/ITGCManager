package com.vieira.rodrigo.itgcmanager;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.Utils.ParseUtils;
import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.adapters.MemberListAdapter;
import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.dialogs.ConfirmationDialogFragment;
import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.models.Control;
import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.models.Project;
import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.models.Scope;
import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.models.User;

import java.util.ArrayList;
import java.util.List;


public class AddControlActivityStepDefineResponsible extends ActionBarActivity implements ConfirmationDialogFragment.OnConfirmDialogListener{

    private static final String KEY_CONTROL_NAME = "controlName";
    private static final String KEY_SELECTED_MEMBER_NAME = "selectedMember";


    Control newControl;
    ArrayList<User> memberList = new ArrayList<>();
    MemberListAdapter memberListAdapter;
    ParseObject currentProjectObject;

    String controlName;
    ParseUser selectedMemberObject;

    RelativeLayout formView;
    ProgressBar progressBar;

    TextView emptyText;
    EditText filterView;

    ListView memberListView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_control_step_define_responsible);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);

        newControl = (Control) getIntent().getSerializableExtra(Control.KEY_NEW_CONTROL);
        controlName = newControl.getName();

        formView = (RelativeLayout) findViewById(R.id.add_control_define_responsible_form_view);
        progressBar = (ProgressBar) findViewById(R.id.add_control_define_responsible_progress_bar);
        emptyText = (TextView) findViewById(R.id.add_control_define_responsible_empty_text);

        filterView = (EditText) findViewById(R.id.add_control_define_responsible_filter_view);
        memberListView = (ListView) findViewById(R.id.add_control_define_responsible_member_list_view);

        loadControlScopeObjectList();
        loadMemberList();

        memberListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ConfirmationDialogFragment confirmationDialogFragment = new ConfirmationDialogFragment();
                Bundle args = new Bundle();
                selectedMemberObject = memberList.get(position).getParseUser();
                String message = getString(R.string.add_control_define_responsible_confirmation_dialog_message)
                        .replace("X_USER", selectedMemberObject.getString(User.KEY_USER_NAME))
                        .replace("Y_CONTROL", controlName);
                args.putString(ConfirmationDialogFragment.KEY_MESSAGE, message);
                confirmationDialogFragment.setArguments(args);
                confirmationDialogFragment.show(getSupportFragmentManager(), "confirm");
            }
        });
    }

    private void loadControlScopeObjectList() {
        showProgress(true);
        for (String scopeId : newControl.getControlScopeIdList()) {
            ParseQuery<ParseObject> getScope = ParseQuery.getQuery(Scope.TABLE_SCOPE);
            try {
                newControl.addControlScopeObject(getScope.get(scopeId));
            } catch (ParseException e) {
                ParseUtils.handleParseException(getApplicationContext(), e);
            }
        }
        showProgress(false);
    }

    private void loadMemberList() {
        showProgress(true);
        String currentProjectId = ParseUtils.getStringFromSession(getApplicationContext(), ParseUtils.PREFS_CURRENT_PROJECT_ID);
        ParseQuery<ParseObject> getCurrentProjectObject = ParseQuery.getQuery(Project.TABLE_PROJECT);
        getCurrentProjectObject.getInBackground(currentProjectId, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject projectObject, ParseException e) {
                if (e == null) {
                    currentProjectObject = projectObject;
                    ParseRelation<ParseUser> projectUserRelation = projectObject.getRelation(Project.KEY_PROJECT_USER_RELATION);
                    ParseQuery<ParseUser> getProjectUserList = projectUserRelation.getQuery();
                    getProjectUserList.findInBackground(new FindCallback<ParseUser>() {
                        @Override
                        public void done(List<ParseUser> resultList, ParseException e) {
                            showProgress(false);
                            if (e == null) {
                                ArrayList<ParseUser> memberResultList = (ArrayList) resultList;
                                memberResultList.remove(ParseUser.getCurrentUser());
                                for (ParseUser user : memberResultList) {
                                    User tempUser = new User(user);
                                    memberList.add(tempUser);
                                }
                                memberListAdapter = new MemberListAdapter(getApplicationContext(), memberList);
                                memberListView.setAdapter(memberListAdapter);
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

    private void attemptSavingControl() {
        showProgress(true);
        ParseObject newParseObjectControl = new ParseObject(Control.TABLE_CONTROL);
        newParseObjectControl.put(Control.KEY_CONTROL_NAME, newControl.getName());
        newParseObjectControl.put(Control.KEY_CONTROL_DESCRIPTION, newControl.getDescription());
        newParseObjectControl.put(Control.KEY_CONTROL_OWNER, newControl.getOwner());
        newParseObjectControl.put(Control.KEY_CONTROL_POPULATION, newControl.getPopulation());
        newParseObjectControl.put(Control.KEY_CONTROL_RISK_CLASS, newControl.getRiskClassification());
        newParseObjectControl.put(Control.KEY_CONTROL_TYPE, newControl.getType());
        newParseObjectControl.put(Control.KEY_CONTROL_FREQUENCY, newControl.getFrequency());
        newParseObjectControl.put(Control.KEY_CONTROL_NATURE, newControl.getNature());
        newParseObjectControl.put(Control.KEY_CONTROL_PROJECT, currentProjectObject);
        newParseObjectControl.put(Control.KEY_CONTROL_SCOPE_LIST, newControl.getControlScopeList());
        newParseObjectControl.put(Control.KEY_CONTROL_MEMBER_RESPONSIBLE, selectedMemberObject);

        newParseObjectControl.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                showProgress(false);
                if (e == null) {
                    Toast.makeText(getApplicationContext(), "SALVO COM SUCESSO!", Toast.LENGTH_LONG).show();
                } else
                    ParseUtils.handleParseException(getApplicationContext(), e);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            formView.setVisibility(show ? View.GONE : View.VISIBLE);
            formView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    formView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            formView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public void OnYesButtonClicked() {
        attemptSavingControl();
    }

    @Override
    public void OnNoButtonClicked() {

    }
}

