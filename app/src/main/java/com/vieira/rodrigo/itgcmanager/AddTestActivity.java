package com.vieira.rodrigo.itgcmanager;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.SaveCallback;
import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.Utils.ParseUtils;
import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.Utils.SelectorDialogActivity;
import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.models.Company;
import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.models.Control;
import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.models.SystemApp;
import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.models.Test;

import java.util.ArrayList;


public class AddTestActivity extends ActionBarActivity implements DatePickerDialog.OnDateSetListener{

    ProgressBar progressBar;
    ScrollView formView;

    EditText descriptionView;
    EditText scheduledDateView;
    EditText coverageDateView;
    String testStatus;

    String currentControlId;
    ParseObject currentControlObject;

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

    Button saveTestButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_test);

        currentControlId = getIntent().getStringExtra(Control.KEY_CONTROL_ID);

        formView = (ScrollView) findViewById(R.id.add_test_form_view);
        progressBar = (ProgressBar) findViewById(R.id.add_test_progress_bar);
        descriptionView = (EditText) findViewById(R.id.add_test_description);
        scheduledDateView = (EditText) findViewById(R.id.add_test_scheduled_date);
        coverageDateView = (EditText) findViewById(R.id.add_test_coverage_date);

        saveTestButton = (Button) findViewById(R.id.add_test_save_button);
        saveTestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFormValid())
                    attemptSavingTest();
            }
        });

        loadControlObject();
        loadCompanyForm();
        loadSystemForm();
    }

    private void loadControlObject() {
        showProgress(true);
        ParseQuery getCurrentControlObject = new ParseQuery(Control.TABLE_CONTROL);
        getCurrentControlObject.getInBackground(currentControlId, new GetCallback() {
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
                    currentControlObject = (ParseObject) o;
                }
            }
        });
    }

    private void loadCompanyForm() {
        companyListView = (ListView) findViewById(R.id.add_test_company_list);
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

        addCompanyButton = (Button) findViewById(R.id.add_test_company_button);
        addCompanyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SelectorDialogActivity.class);
                intent.putExtra(Control.KEY_CONTROL_ID, currentControlId);
                intent.putExtra(SelectorDialogActivity.KEY_REQUEST_CODE, SelectorDialogActivity.REQUEST_CONTROL_COMPANY_LIST);
                ArrayList<String> listOfIdsToExclude = new ArrayList<>();
                for (ParseObject companyObject : selectedCompanyObjectList) {
                    listOfIdsToExclude.add(companyObject.getObjectId());
                }
                intent.putStringArrayListExtra(SelectorDialogActivity.KEY_LIST_OF_IDS_TO_EXCLUDE, listOfIdsToExclude);
                startActivityForResult(intent, SelectorDialogActivity.REQUEST_CONTROL_COMPANY_LIST);
            }
        });
    }

    private void loadSystemForm() {
        systemListView = (ListView) findViewById(R.id.add_test_system_list);
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

        addSystemButton = (Button) findViewById(R.id.add_test_system_button);
        addSystemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SelectorDialogActivity.class);
                intent.putExtra(Control.KEY_CONTROL_ID, currentControlId);
                intent.putExtra(SelectorDialogActivity.KEY_REQUEST_CODE, SelectorDialogActivity.REQUEST_CONTROL_SYSTEM_LIST);
                ArrayList<String> listOfIdsToExclude = new ArrayList<>();
                for (ParseObject systemObject : selectedSystemObjectList) {
                    listOfIdsToExclude.add(systemObject.getObjectId());
                }
                intent.putStringArrayListExtra(SelectorDialogActivity.KEY_LIST_OF_IDS_TO_EXCLUDE, listOfIdsToExclude);
                startActivityForResult(intent, SelectorDialogActivity.REQUEST_CONTROL_SYSTEM_LIST);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_test, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

    }

    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        switch (view.getId()) {
            case R.id.add_test_status_radio_scheduled:
                if (checked)
                    testStatus = Test.SCHEDULED;
                break;

            case R.id.add_test_status_radio_on_progress:
                if (checked)
                    testStatus = Test.ON_PROGRESS;
                break;

            case R.id.add_test_status_radio_done:
                if (checked)
                    testStatus = Test.DONE;
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null) {
            String selectedItemId = data.getStringExtra(SelectorDialogActivity.KEY_SELECTED_ITEM_ID);
            switch (requestCode) {
                case SelectorDialogActivity.REQUEST_CONTROL_COMPANY_LIST:
                    addCompanyToList(selectedItemId);
                    break;

                case SelectorDialogActivity.REQUEST_CONTROL_SYSTEM_LIST:
                    addSystemToList(selectedItemId);
            }
        }
    }

    private void addCompanyToList(String companyId) {
        ParseQuery getCompanyObject = ParseQuery.getQuery(Company.TABLE_COMPANY);
        getCompanyObject.getInBackground(companyId, new GetCallback() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                if (e != null) {
                    ParseUtils.handleParseException(getApplicationContext(), e);
                }
            }

            @Override
            public void done(Object o, Throwable throwable) {
                if (o != null) {
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

    private boolean isFormValid() {
        String description = descriptionView.getText().toString();
        if (description.isEmpty()) {
            descriptionView.setError(getString(R.string.error_field_required));
            return false;
        }

        String scheduledDate = scheduledDateView.getText().toString();
        if (scheduledDate.isEmpty()) {
            scheduledDateView.setError(getString(R.string.error_field_required));
            return false;
        }

        String coverageDate = coverageDateView.getText().toString();
        if (coverageDate.isEmpty()) {
            coverageDateView.setError(getString(R.string.error_field_required));
            return false;
        }
        if (testStatus.isEmpty())
            return false;

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

    private void attemptSavingTest() {
        showProgress(true);
        ParseObject newTest = new ParseObject(Test.TABLE_TEST);
        newTest.put(Test.KEY_TEST_DESCRIPTION, descriptionView.getText().toString());
        newTest.put(Test.KEY_TEST_SCHEDULED_DATE, scheduledDateView.getText().toString());
        newTest.put(Test.KEY_TEST_COVERAGE_DATE, coverageDateView.getText().toString());
        newTest.put(Test.KEY_TEST_STATUS, testStatus);

        ParseRelation<ParseObject> companyRelation = newTest.getRelation(Test.KEY_TEST_COMPANY_RELATION);
        for (ParseObject companyObject : selectedCompanyObjectList){
            companyRelation.add(companyObject);
        }

        ParseRelation<ParseObject> systemRelation = newTest.getRelation(Test.KEY_TEST_SYSTEM_RELATION);
        for (ParseObject systemObject : selectedSystemObjectList) {
            systemRelation.add(systemObject);
        }

        newTest.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Toast.makeText(getApplicationContext(), "SALVO COM SUCESSO!", Toast.LENGTH_LONG).show();
                }
                else {
                    ParseUtils.handleParseException(getApplicationContext(), e);
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
