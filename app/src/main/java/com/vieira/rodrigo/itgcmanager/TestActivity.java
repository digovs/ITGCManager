package com.vieira.rodrigo.itgcmanager;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;
import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.Utils.ParseUtils;
import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.fragments.TestDetailsFragment;
import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.models.Company;
import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.models.Control;
import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.models.Project;
import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.models.SystemApp;
import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.models.Test;


public class TestActivity extends ActionBarActivity implements ActionBar.TabListener, TestDetailsFragment.OnFragmentInteractionListener {

    public static final int TAB_DETAILS = 0;
    public static final int TAB_SYSTEM = 1;
    public static final int TAB_COMPANY = 2;
    public static final int TAB_MEMBER = 3;
    public static final int TAB_EXCEPTIONS = 4;

    public static final int ADD_MODE = 0;
    public static final int EDIT_MODE = 1;
    public static final int VIEW_MODE = 2;

    ParseObject currentControlObject;
    ParseObject currentProjectObject;

    private ArrayList<ParseObject> statusObjectList;
    private ArrayList<ParseObject> systemScopeObjectList;
    private ArrayList<ParseObject> companyScopeObjectList;
    private ArrayList<ParseObject> memberObjectList;
    private ArrayList<ParseObject> typeObjectList;
    private ArrayList<ParseObject> projectControlObjectList;

    private ArrayList<String> statusDescriptionList;
    private ArrayList<String> systemScopeNameList;
    private ArrayList<String> companyScopeNameList;
    private ArrayList<String> memberNameList;
    private ArrayList<String> typeDescriptionList;
    private ArrayList<String> projectControlScopeNameList;

    public static final String MODE_FLAG = "mode_flag";
    int mode;

    private Test activityCurrentTest;

    SectionsPagerAdapter mSectionsPagerAdapter;

    ViewPager mViewPager;
    ActionBar actionBar;
    private ParseObject activityCurrentTestObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        // Set up the action bar.
        actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.test_pager);

        mode = getIntent().getIntExtra(MODE_FLAG, ADD_MODE);
        switch (mode) {
            case ADD_MODE:
                actionBar.setTitle(getString(R.string.test_activity_add_test_title));
                activityCurrentTest = new Test();
                loadTestContents();
                break;

            case EDIT_MODE:
                loadTestContents();
                loadActivityCurrentTest(getIntent().getStringExtra(Test.KEY_TEST_ID));
                break;

            case VIEW_MODE:
                loadTestContents();
                loadCurrentControlObject();
                loadActivityCurrentTest(getIntent().getStringExtra(Test.KEY_TEST_ID));
                break;
        }

        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }
    }

    private void loadTestContents() {
        loadCurrentProjectObject();
        loadDetailsSpinnerContents();
    }

    private void loadCurrentProjectObject() {
        String currentProjectId = ParseUtils.getStringFromSession(getApplicationContext(), ParseUtils.PREFS_CURRENT_PROJECT_ID);
        ParseQuery<ParseObject> getCurrentProjectQuery = ParseQuery.getQuery(Project.TABLE_PROJECT);
        getCurrentProjectQuery.include(Project.KEY_SYSTEM_SCOPE_LIST)
                .include(Project.KEY_COMPANY_SCOPE_LIST);
        try {
            currentProjectObject = getCurrentProjectQuery.get(currentProjectId);

            systemScopeObjectList = (ArrayList) currentProjectObject.getList(Project.KEY_SYSTEM_SCOPE_LIST);
            systemScopeNameList = castParseObjectListToStringList(systemScopeObjectList, SystemApp.KEY_SYSTEM_NAME);

            companyScopeObjectList = (ArrayList) currentProjectObject.getList(Project.KEY_COMPANY_SCOPE_LIST);
            companyScopeNameList = castParseObjectListToStringList(companyScopeObjectList, Company.KEY_COMPANY_NAME);
        } catch (ParseException e) {
            ParseUtils.handleParseException(TestActivity.this, e);
        }
    }

    private void loadCurrentControlObject() {
        String currentControlId = getIntent().getStringExtra(Control.KEY_CONTROL_ID);
        ParseQuery<ParseObject> getCurrentControlQuery = ParseQuery.getQuery(Control.TABLE_CONTROL);
        try {
            currentControlObject = getCurrentControlQuery.get(currentControlId);
            actionBar.setTitle(currentControlObject.getString(Control.KEY_CONTROL_NAME));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void loadDetailsSpinnerContents() {
        ParseQuery<ParseObject> getStatusDescription = ParseQuery.getQuery(Test.TABLE_TEST_STATUS);
        ParseQuery<ParseObject> getTypeDescription = ParseQuery.getQuery(Test.TABLE_TEST_TYPE);
        ParseQuery<ParseObject> getCurrentProjectControlScope = ParseQuery.getQuery(Control.TABLE_CONTROL);
        getCurrentProjectControlScope.whereEqualTo(Control.KEY_CONTROL_PROJECT, currentProjectObject);
        try {
            statusObjectList = (ArrayList) getStatusDescription.find();
            statusDescriptionList = castParseObjectListToStringList(statusObjectList, Test.KEY_GENERIC_DESCRIPTION);
            statusDescriptionList.add(0, getString(R.string.test_activity_details_status_label));

            typeObjectList = (ArrayList) getTypeDescription.find();
            typeDescriptionList = castParseObjectListToStringList(typeObjectList, Test.KEY_GENERIC_DESCRIPTION);
            typeDescriptionList.add(0, getString(R.string.test_activity_details_type_label));

            projectControlObjectList = (ArrayList<ParseObject>) getCurrentProjectControlScope.find();
            projectControlScopeNameList = castParseObjectListToStringList(projectControlObjectList, Control.KEY_CONTROL_NAME);
            projectControlScopeNameList.add(0, getString(R.string.test_activity_details_control_label));
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    private void loadActivityCurrentTest(String testId) {
        ProgressDialog progressDialog = new ProgressDialog(TestActivity.this);
        progressDialog.setMessage(getString(R.string.loading_dialog_message_loading_test));
        progressDialog.setCancelable(false);
        progressDialog.show();

        ParseQuery getCurrentTest = ParseQuery.getQuery(Test.TABLE_TEST);
        getCurrentTest.include(Test.KEY_TEST_PROJECT)
                .include(Test.KEY_TEST_CONTROL)
                .include(Test.KEY_TEST_SYSTEM_SCOPE)
                .include(Test.KEY_TEST_COMPANY_SCOPE)
                .include(Test.KEY_TEST_MEMBER_RESPONSIBLE)
                .include(Test.KEY_TEST_STATUS)
                .include(Test.KEY_TEST_TYPE);

        try {
            activityCurrentTestObject = getCurrentTest.get(testId);
            activityCurrentTest = new Test(activityCurrentTestObject);
            actionBar.setTitle(activityCurrentTest.getName());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        progressDialog.dismiss();
    }

    private ArrayList<String> castParseObjectListToStringList(ArrayList<ParseObject> parseObjectList, String tableField) {
        ArrayList<String> output = new ArrayList<>();
        for (ParseObject obj : parseObjectList){
            String desc = obj.getString(tableField);
            output.add(desc);
        }
        return output;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_test_activity, menu);
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
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void saveTestName(String name) {
        if (name != null)
            activityCurrentTest.setName(name);
    }

    @Override
    public void saveTestDescription(String description) {
        if (description != null)
            activityCurrentTest.setDescription(description);
    }

    @Override
    public void onSaveButtonClicked() {
        if (testIsValid() && currentProjectObject != null) {
            final ProgressDialog progressDialog = ProgressDialog.show(TestActivity.this, "", getString(R.string.loading_dialog_message_saving_test));
            progressDialog.setCancelable(false);

            ParseObject parseObjectTest;
            if (mode == EDIT_MODE)
                parseObjectTest = activityCurrentTestObject;
            else
                parseObjectTest = new ParseObject(Test.TABLE_TEST);

            parseObjectTest.put(Test.KEY_TEST_NAME, activityCurrentTest.getName());
            parseObjectTest.put(Test.KEY_TEST_DESCRIPTION, activityCurrentTest.getDescription());
            parseObjectTest.put(Test.KEY_TEST_POPULATION, activityCurrentTest.getPopulation());
            parseObjectTest.put(Test.KEY_TEST_SAMPLE, activityCurrentTest.getSample());
            Date coverageDate = new Date(activityCurrentTest.getCoverageDate().getTimeInMillis());
            parseObjectTest.put(Test.KEY_TEST_COVERAGE_DATE, coverageDate);

            parseObjectTest.put(Test.KEY_TEST_PROJECT, currentProjectObject);
            parseObjectTest.put(Test.KEY_TEST_CONTROL, activityCurrentTest.getControlObject());
            parseObjectTest.put(Test.KEY_TEST_TYPE, activityCurrentTest.getTypeObject());
            parseObjectTest.put(Test.KEY_TEST_STATUS, activityCurrentTest.getStatusObject());

            parseObjectTest.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    progressDialog.dismiss();
                    if (e == null) {
                        Toast successToast = Toast.makeText(TestActivity.this, R.string.dialog_message_saved_successfully, Toast.LENGTH_LONG);
                        successToast.setGravity(Gravity.CENTER, 0, 0);
                        successToast.show();
                        Intent intent = new Intent(TestActivity.this, ProjectDashboardActivity.class);
                        intent.putExtra(ProjectDashboardActivity.KEY_COMING_FROM_ACTIVITY, ProjectDashboardActivity.COMING_FROM_CREATE_TEST);
                        startActivity(intent);
                        finish();
                    } else
                        ParseUtils.handleParseException(getApplicationContext(), e);
                }
            });
            progressDialog.dismiss();
        }
    }

    private boolean testIsValid() {
        int count = 0;
        String fieldsRequired = "";

        if (activityCurrentTest.getSample() > activityCurrentTest.getPopulation()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(TestActivity.this);
            builder.setTitle(R.string.add_control_error_fragment_title)
                    .setMessage(getString(R.string.test_field_error_sample_bigger_then_population))
                    .setNeutralButton(R.string.add_control_error_fragment_ok_button, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    }).create().show();
            return false;
        }

        if (activityCurrentTest.getName() == null || activityCurrentTest.getName().isEmpty()) {
            count++;
            fieldsRequired = fieldsRequired.concat(getString(R.string.test_field_name_required));
        }

        if (activityCurrentTest.getDescription() == null || activityCurrentTest.getDescription().isEmpty()) {
            count++;
            fieldsRequired = fieldsRequired.concat(getString(R.string.test_field_description_required));
        }

        if (activityCurrentTest.getPopulation() <= 0) {
            count++;
            fieldsRequired = fieldsRequired.concat(getString(R.string.test_field_population_required));
        }

        if (activityCurrentTest.getSample() <= 0) {
            count++;
            fieldsRequired = fieldsRequired.concat(getString(R.string.test_field_sample_required));
        }

        if (activityCurrentTest.getControlObject() == null) {
            count++;
            fieldsRequired = fieldsRequired.concat(getString(R.string.test_field_control_required));
        }

        if (activityCurrentTest.getTypeObject() == null) {
            count++;
            fieldsRequired = fieldsRequired.concat(getString(R.string.test_field_type_required));
        }

        if (activityCurrentTest.getStatusObject() == null) {
            count++;
            fieldsRequired = fieldsRequired.concat(getString(R.string.test_field_status_required));
        }


        if (count == 0) {
            return true;
        } else {
            String errorMessage = "";
            if (count > 1)
                errorMessage = getString(R.string.control_invalid_error_message_plural);
            else if (count == 1)
                errorMessage = getString(R.string.control_invalid_error_message_single);

            String finalMessage = errorMessage.concat(fieldsRequired);
            AlertDialog.Builder builder = new AlertDialog.Builder(TestActivity.this);
            builder.setTitle(R.string.add_control_error_fragment_title)
                    .setMessage(finalMessage)
                    .setNeutralButton(R.string.add_control_error_fragment_ok_button, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    }).create().show();
            return false;
        }
    }


    @Override
    public void saveTestType(String type) {
        if (type != null)
            activityCurrentTest.setTypeObject(typeObjectList.get(typeDescriptionList.indexOf(type) - 1));
    }

    @Override
    public void saveTestPopulation(String population) {
        if (population != null) {
            int pop = Integer.parseInt(population);
            activityCurrentTest.setPopulation(pop);
        }
    }

    @Override
    public void saveTestSample(String sample) {
        if (sample != null) {
            int sam = Integer.parseInt(sample);
            activityCurrentTest.setSample(sam);
        }
    }

    @Override
    public void saveCoverageDate(int year, int monthOfYear, int dayOfMonth) {
        GregorianCalendar coverageDate = new GregorianCalendar(year, monthOfYear, dayOfMonth);
        activityCurrentTest.setCoverageDate(coverageDate);
    }

    @Override
    public void saveTestControl(String control) {
        if (control != null)
            activityCurrentTest.setControlObject(projectControlObjectList.get(projectControlScopeNameList.indexOf(control) - 1));
    }

    @Override
    public void saveTestStatus(String status) {
        if (status != null)
            activityCurrentTest.setStatusObject(statusObjectList.get(statusDescriptionList.indexOf(status) - 1));
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case TAB_DETAILS:
                    TestDetailsFragment detailsFragment = new TestDetailsFragment();
                    Bundle detailsArgs = new Bundle();
                    detailsArgs.putInt(MODE_FLAG, mode);
                    detailsArgs.putStringArrayList(TestDetailsFragment.STATUS_LIST_CONTENT, statusDescriptionList);
                    detailsArgs.putStringArrayList(TestDetailsFragment.TYPE_LIST_CONTENT, typeDescriptionList);
                    detailsArgs.putStringArrayList(TestDetailsFragment.CONTROL_LIST_CONTENT, projectControlScopeNameList);
                    detailsArgs.putString(TestDetailsFragment.DETAILS_ARGS_NAME, activityCurrentTest.getName());
                    detailsArgs.putString(TestDetailsFragment.DETAILS_ARGS_DESCRIPTION, activityCurrentTest.getDescription());
                    detailsArgs.putString(TestDetailsFragment.DETAILS_ARGS_POPULATION, String.valueOf(activityCurrentTest.getPopulation()));
                    detailsArgs.putString(TestDetailsFragment.DETAILS_ARGS_SAMPLE, String.valueOf(activityCurrentTest.getSample()));
                    detailsArgs.putLong(TestDetailsFragment.DETAILS_ARGS_COVERAGE_DATE, activityCurrentTest.getCoverageDate().getTimeInMillis());

                    detailsArgs.putString(TestDetailsFragment.DETAILS_ARGS_TYPE, activityCurrentTest.getTypeObject().getString(Test.KEY_GENERIC_DESCRIPTION));
                    detailsArgs.putString(TestDetailsFragment.DETAILS_ARGS_STATUS, activityCurrentTest.getStatusObject().getString(Test.KEY_GENERIC_DESCRIPTION));
                    detailsArgs.putString(TestDetailsFragment.DETAILS_ARGS_CONTROL, activityCurrentTest.getControlObject().getString(Control.KEY_CONTROL_NAME));
                    detailsFragment.setArguments(detailsArgs);
                    return detailsFragment;

                case TAB_SYSTEM:
                    break;

                case TAB_COMPANY:
                    break;

                case TAB_MEMBER:
                    break;

                case TAB_EXCEPTIONS:
                    break;
            }
            return null;
        }

        @Override
        public int getCount() {
            return 1;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case TAB_DETAILS:
                    return getString(R.string.test_activity_section_details).toUpperCase(l);
                case TAB_SYSTEM:
                    return getString(R.string.test_activity_section_system_scope).toUpperCase(l);
                case TAB_COMPANY:
                    return getString(R.string.test_activity_section_company_scope).toUpperCase(l);
                case TAB_MEMBER:
                    return getString(R.string.test_activity_section_member_responsible).toUpperCase(l);
                case TAB_EXCEPTIONS:
                    return getString(R.string.test_activity_section_exceptions).toUpperCase(l);
            }
            return null;
        }
    }

}