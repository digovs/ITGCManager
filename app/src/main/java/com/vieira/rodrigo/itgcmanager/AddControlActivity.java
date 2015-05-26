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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.Utils.ParseUtils;
import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.Utils.SelectorDialogActivity;
import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.models.Company;
import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.models.Control;
import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.models.SystemApp;
import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.models.User;

import java.util.ArrayList;


public class AddControlActivity extends ActionBarActivity {

    ListView companyListView;
    Button addCompanyButton;
    ArrayAdapter companyListAdapter;

    ArrayList<ParseObject> selectedCompanyObjectList = new ArrayList<>();
    ArrayList<String> selectedCompanyNameList = new ArrayList<>();

    ListView systemListView;
    Button addSystemButton;
    ArrayAdapter systemListAdapter;

    ArrayList<ParseObject> selectedSystemObjectList = new ArrayList<>();
    ArrayList<String> selectedSystemNameList = new ArrayList<>();

    ScrollView formView;
    ProgressBar progressBar;
    TextView ownerTextView;
    Button addOwnerButton;
    ParseUser selectedOwnerObject;

    EditText controlNameView;
    EditText controlDescriptionView;
    CheckBox isAutomaticCheckbox;
    Button saveButton;
    Button saveAndContinueButton;

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
        isAutomaticCheckbox = (CheckBox) findViewById(R.id.add_control_is_automatic_checkbox);

        loadOwnerForm();
        loadCompanyForm();
        loadSystemForm();


        saveButton = (Button) findViewById(R.id.add_control_save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateForm())
                    attemptSavingControl();
            }
        });
    }

    private void loadOwnerForm() {
        ownerTextView = (TextView) findViewById(R.id.add_control_owner_view);
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
                Intent intent = new Intent(getApplicationContext(), SelectorDialogActivity.class);
                intent.putExtra(SelectorDialogActivity.KEY_REQUEST_CODE, SelectorDialogActivity.REQUEST_MEMBER_LIST);
                ArrayList<String> listOfIdsToExclude = new ArrayList<>();
                if (selectedOwnerObject != null)
                    listOfIdsToExclude.add(selectedOwnerObject.getObjectId());
                intent.putStringArrayListExtra(SelectorDialogActivity.KEY_LIST_OF_IDS_TO_EXCLUDE, listOfIdsToExclude);
                startActivityForResult(intent, SelectorDialogActivity.REQUEST_MEMBER_LIST);
            }
        });
    }

    private void loadCompanyForm() {
        companyListView = (ListView) findViewById(R.id.add_control_company_list);
        companyListAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_activated_1, selectedCompanyNameList);
        companyListView.setAdapter(companyListAdapter);
        companyListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, final View view, final int position, long id) {
                final String item = (String) parent.getItemAtPosition(position);
                view.animate().setDuration(2000).alpha(0)
                        .withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                selectedCompanyObjectList.remove(position);
                                selectedCompanyNameList.remove(item);
                                companyListAdapter.notifyDataSetChanged();
                                view.setAlpha(1);
                            }
                        });
                return true;
            }
        });

        addCompanyButton = (Button) findViewById(R.id.add_control_company_button);
        addCompanyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SelectorDialogActivity.class);
                intent.putExtra(SelectorDialogActivity.KEY_REQUEST_CODE, SelectorDialogActivity.REQUEST_COMPANY_LIST);
                ArrayList<String> listOfIdsToExclude = new ArrayList<>();
                for (ParseObject companyObject : selectedCompanyObjectList){
                    listOfIdsToExclude.add(companyObject.getObjectId());
                }
                intent.putStringArrayListExtra(SelectorDialogActivity.KEY_LIST_OF_IDS_TO_EXCLUDE, listOfIdsToExclude);
                startActivityForResult(intent, SelectorDialogActivity.REQUEST_COMPANY_LIST);
            }
        });
    }

    private void loadSystemForm() {
        systemListView = (ListView) findViewById(R.id.add_control_system_list);
        systemListAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_activated_1, selectedSystemNameList);
        systemListView.setAdapter(systemListAdapter);
        systemListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, final View view, final int position, long id) {
                final String item = (String) parent.getItemAtPosition(position);
                view.animate().setDuration(2000).alpha(0)
                        .withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                selectedSystemObjectList.remove(position);
                                selectedSystemNameList.remove(item);
                                systemListAdapter.notifyDataSetChanged();
                                view.setAlpha(1);
                            }
                        });
                return true;
            }
        });

        addSystemButton = (Button) findViewById(R.id.add_control_system_button);
        addSystemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SelectorDialogActivity.class);
                intent.putExtra(SelectorDialogActivity.KEY_REQUEST_CODE, SelectorDialogActivity.REQUEST_SYSTEM_LIST);
                ArrayList<String> listOfIdsToExclude = new ArrayList<>();
                for (ParseObject systemObject : selectedSystemObjectList){
                    listOfIdsToExclude.add(systemObject.getObjectId());
                }
                intent.putStringArrayListExtra(SelectorDialogActivity.KEY_LIST_OF_IDS_TO_EXCLUDE, listOfIdsToExclude);
                startActivityForResult(intent, SelectorDialogActivity.REQUEST_SYSTEM_LIST);
            }
        });
    }

    private boolean validateForm() {
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

        boolean isAutomatic = isAutomaticCheckbox.isChecked();

        if (selectedOwnerObject == null || selectedOwnerObject.getObjectId().equals("")) {
            ParseUtils.callErrorDialogWithMessage(getApplicationContext(), getString(R.string.add_control_no_users_selected_error_message));
            return false;
        }

        if (selectedCompanyObjectList == null || selectedCompanyObjectList.isEmpty()) {
            ParseUtils.callErrorDialogWithMessage(getApplicationContext(), getString(R.string.add_control_no_companies_selected_error_message));
            return false;
        }

        if (selectedSystemObjectList == null || selectedSystemObjectList.isEmpty()){
            ParseUtils.callErrorDialogWithMessage(getApplicationContext(), getString(R.string.add_control_no_systems_selected_error_message));
            return false;
        }

        return true;
    }

    private void attemptSavingControl() {
        showProgress(true);
        ParseObject newControl = new ParseObject(Control.TABLE_CONTROL);
        newControl.put(Control.KEY_CONTROL_NAME, controlNameView.getText().toString());
        newControl.put(Control.KEY_CONTROL_DESCRIPTION, controlDescriptionView.getText().toString());
        newControl.put(Control.KEY_CONTROL_IS_AUTOMATIC, isAutomaticCheckbox.isChecked());
        newControl.put(Control.KEY_CONTROL_OWNER, selectedOwnerObject);
        ParseRelation<ParseObject> companyListRelation = newControl.getRelation(Control.KEY_CONTROL_COMPANY_LIST);
        for (ParseObject companyObject : selectedCompanyObjectList) {
            companyListRelation.add(companyObject);
        }
        ParseRelation<ParseObject> systemListRelation = newControl.getRelation(Control.KEY_CONTROL_SYSTEM_LIST);
        for (ParseObject systemObject : selectedSystemObjectList) {
            systemListRelation.add(systemObject);
        }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null) {
            String selectedItemId = data.getStringExtra(SelectorDialogActivity.KEY_SELECTED_ITEM_ID);
            switch (requestCode) {
                case SelectorDialogActivity.REQUEST_COMPANY_LIST:
                    addCompanyToList(selectedItemId);
                    break;

                case SelectorDialogActivity.REQUEST_MEMBER_LIST:
                    addMemberToView(selectedItemId);
                    break;

                case SelectorDialogActivity.REQUEST_SYSTEM_LIST:
                    addSystemToList(selectedItemId);
            }
        }
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
                    ParseUser resultMemberObject = (ParseUser) o;
                    selectedOwnerObject = resultMemberObject;
                    ownerTextView.setText(selectedOwnerObject.getString(User.KEY_USER_NAME));
                }
            }
        });
    }

    private void addCompanyToList(String companyId) {
        ParseQuery getCompanyObject = ParseQuery.getQuery(Company.TABLE_COMPANY);
        getCompanyObject.getInBackground(companyId, new GetCallback() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                if (e != null){
                    ParseUtils.handleParseException(getApplicationContext(), e);
                }
            }

            @Override
            public void done(Object o, Throwable throwable) {
                if (o != null){
                    ParseObject resultCompanyObject = (ParseObject) o;
                    selectedCompanyObjectList.add(resultCompanyObject);
                    selectedCompanyNameList.add(resultCompanyObject.getString(Company.KEY_COMPANY_NAME));
                    companyListAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void addSystemToList(String systemId) {
        ParseQuery getSystemObject = ParseQuery.getQuery(SystemApp.TABLE_SYSTEM);
        getSystemObject.getInBackground(systemId, new GetCallback() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                if (e != null){
                    ParseUtils.handleParseException(getApplicationContext(), e);
                }
            }

            @Override
            public void done(Object o, Throwable throwable) {
                if (o != null){
                    ParseObject resultSystemObject = (ParseObject) o;
                    selectedSystemObjectList.add(resultSystemObject);
                    selectedSystemNameList.add(resultSystemObject.getString(SystemApp.KEY_SYSTEM_NAME));
                    systemListAdapter.notifyDataSetChanged();
                }
            }
        });
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
