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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.Utils.ParseUtils;
import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.adapters.ScopeListAdapter;
import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.dialogs.SystemScopeSelectorDialogActivity;
import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.dialogs.TeamMemberSelectorDialogActivity;
import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.models.Control;
import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.models.Project;
import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.models.Scope;
import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.models.User;

import java.util.ArrayList;


public class AddControlActivity extends ActionBarActivity {

    public static final int REQUEST_TEAM_MEMBER_SELECTOR = 1154;
    public static final int REQUEST_SCOPE_SELECTOR = 5776;
    public static final int REQUEST_COMPANY_SCOPE_SELECTOR = 3249;
    public static final String REQUEST_CODE = "requestCode";


    ParseObject currentProjectObject;
    ArrayList<ParseObject> controlScopeList = new ArrayList<>();

    ListView scopeListView;
    ScopeListAdapter scopeListAdapter;
    Button defineScope;

    ScrollView formView;
    ProgressBar progressBar;
    EditText ownerTextView;
    Button addOwnerButton;
    ParseUser selectedOwnerObject;

    EditText controlNameView;
    EditText controlDescriptionView;
    EditText controlPopulationView;
    CheckBox isAutomaticCheckbox;
    Button saveButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_control);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);

        formView = (ScrollView) findViewById(R.id.add_control_form_view);
        progressBar = (ProgressBar) findViewById(R.id.add_control_progress_bar);
        controlNameView = (EditText) findViewById(R.id.add_control_name);
        controlDescriptionView = (EditText) findViewById(R.id.add_control_description);
        controlPopulationView = (EditText) findViewById(R.id.add_control_population);
        isAutomaticCheckbox = (CheckBox) findViewById(R.id.add_control_is_automatic_checkbox);
        scopeListView = (ListView) findViewById(R.id.add_control_scope_list);


        loadProjectObject();
        loadOwnerForm();
        loadScopeList();

        defineScope = (Button) findViewById(R.id.add_control_scope_button);
        defineScope.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SystemScopeSelectorDialogActivity.class);
                intent.putExtra(REQUEST_CODE, REQUEST_SCOPE_SELECTOR);
                startActivityForResult(intent, REQUEST_SCOPE_SELECTOR);
            }
        });

        saveButton = (Button) findViewById(R.id.add_control_save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFormValid())
                    attemptSavingControl();
            }
        });
    }

    private void loadProjectObject() {
        showProgress(true);
        final String currentProjectId = ParseUtils.getStringFromSession(getApplicationContext(), Project.KEY_PROJECT_ID);
        ParseQuery getCurrentProjectObject = new ParseQuery(Project.TABLE_PROJECT);
        getCurrentProjectObject.getInBackground(currentProjectId, new GetCallback() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                showProgress(false);
                if (e != null)
                    ParseUtils.handleParseException(getApplicationContext(), e);
            }

            @Override
            public void done(Object o, Throwable throwable) {
                showProgress(false);
                if (o != null) {
                    currentProjectObject = (ParseObject) o;
                }
            }
        });
    }

    private void loadOwnerForm() {
        ownerTextView = (EditText) findViewById(R.id.add_control_owner_view);
        ownerTextView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View view) {
                view.animate().setDuration(2000).alpha(0)
                        .withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                ownerTextView.setText("");
                                selectedOwnerObject = null;
                                view.setAlpha(1);
                            }
                        });
                return true;
            }
        });

        addOwnerButton = (Button) findViewById(R.id.add_control_owner_button);
        addOwnerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), TeamMemberSelectorDialogActivity.class);
                intent.putExtra(REQUEST_CODE, REQUEST_TEAM_MEMBER_SELECTOR);
                if (selectedOwnerObject != null)
                    intent.putExtra(TeamMemberSelectorDialogActivity.KEY_ID_TO_EXCLUDE, selectedOwnerObject.getObjectId());
                else
                    intent.putExtra(TeamMemberSelectorDialogActivity.KEY_ID_TO_EXCLUDE, "");
                startActivityForResult(intent, REQUEST_TEAM_MEMBER_SELECTOR);
            }
        });
    }

    private void loadScopeList() {

    }

    private boolean isFormValid() {
        String controlName = controlNameView.getText().toString();
        if (controlName.isEmpty()) {
            controlNameView.setError(getString(R.string.error_field_required));
            return false;
        }

        String controlDescription = controlDescriptionView.getText().toString();
        if (controlDescription.isEmpty()){
            controlNameView.setError(getString(R.string.error_field_required));
            return false;
        }

        String controlPopulation = controlPopulationView.getText().toString();
        if (controlPopulation.isEmpty()){
            controlPopulationView.setError(getString(R.string.error_field_required));
            return false;
        }

        if (selectedOwnerObject == null || selectedOwnerObject.getObjectId().equals("")) {
            ParseUtils.callErrorDialogWithMessage(getApplicationContext(), getString(R.string.add_control_no_users_selected_error_message));
            return false;
        }

        if (controlScopeList.isEmpty()) {
            ParseUtils.callErrorDialogWithMessage(getApplicationContext(), getString(R.string.add_control_no_scope_selected_error_message));
            return false;
        }

        return true;
    }

    private void attemptSavingControl() {
        showProgress(true);
        ParseObject newControl = new ParseObject(Control.TABLE_CONTROL);
        newControl.put(Control.KEY_CONTROL_NAME, controlNameView.getText().toString());
        newControl.put(Control.KEY_CONTROL_DESCRIPTION, controlDescriptionView.getText().toString());
        newControl.put(Control.KEY_CONTROL_POPULATION, controlPopulationView.getText().toString());
        newControl.put(Control.KEY_CONTROL_IS_AUTOMATIC, isAutomaticCheckbox.isChecked());
        newControl.put(Control.KEY_CONTROL_PROJECT, currentProjectObject);
        newControl.put(Control.KEY_CONTROL_OWNER, selectedOwnerObject);
        newControl.put(Control.KEY_CONTROL_SCOPE_LIST, controlScopeList);

        newControl.saveInBackground(new SaveCallback() {
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_TEAM_MEMBER_SELECTOR:
                if (data != null) {
                    String selectedItemId = data.getStringExtra(TeamMemberSelectorDialogActivity.KEY_SELECTED_ITEM_ID);
                    addMemberToView(selectedItemId);
                }
                break;

            case REQUEST_SCOPE_SELECTOR:
                if (resultCode == RESULT_OK && data != null) {
                    String newScopeId = data.getStringExtra(SystemScopeSelectorDialogActivity.KEY_SCOPE_ID);
                    addScopeToList(newScopeId);
                }
        }

    }

    private void addScopeToList(String newScopeId) {
        ParseQuery getNewScopeObject = ParseQuery.getQuery(Scope.TABLE_SCOPE);
        getNewScopeObject.include(Scope.KEY_COMPANY_LIST);
        getNewScopeObject.getInBackground(newScopeId, new GetCallback() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                if (e != null) {
                    ParseUtils.handleParseException(getApplicationContext(), e);
                }
            }

            @Override
            public void done(Object o, Throwable throwable) {
                if (o != null) {
                    controlScopeList.add((ParseObject) o);
                    scopeListAdapter = new ScopeListAdapter(getApplicationContext(), controlScopeList);
                    scopeListView.setAdapter(scopeListAdapter);
                }
            }
        });
    }

    private void addMemberToView(String memberId) {
        ParseQuery getMemberObject = ParseUser.getQuery();
        getMemberObject.getInBackground(memberId, new GetCallback() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                if (e != null) {
                    ParseUtils.handleParseException(getApplicationContext(), e);
                }
            }

            @Override
            public void done(Object o, Throwable throwable) {
                if (o != null) {
                    selectedOwnerObject = (ParseUser) o;
                    ownerTextView.setText(selectedOwnerObject.getString(User.KEY_USER_NAME));
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_control, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
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
}
