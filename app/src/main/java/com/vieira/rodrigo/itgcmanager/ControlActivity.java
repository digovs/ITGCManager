package com.vieira.rodrigo.itgcmanager;

import java.util.ArrayList;
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
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.Utils.ParseUtils;
import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.fragments.ControlCompanyScopeTabFragment;
import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.fragments.ControlDetailsTabFragment;
import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.fragments.ControlMemberResponsibleTabFragment;
import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.fragments.ControlSystemScopeTabFragment;
import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.models.Company;
import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.models.Control;
import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.models.Project;
import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.models.SystemApp;


public class ControlActivity extends ActionBarActivity implements ActionBar.TabListener,
        ControlDetailsTabFragment.OnFragmentInteractionListener, ControlSystemScopeTabFragment.OnFragmentInteractionListener,
        ControlCompanyScopeTabFragment.OnFragmentInteractionListener, ControlMemberResponsibleTabFragment.OnFragmentInteractionListener {

    private static final int TAB_DETAILS = 0;
    private static final int TAB_SYSTEM_SCOPE = 1;
    private static final int TAB_COMPANY_SCOPE = 2;
    private static final int TAB_RESPONSIBLE = 3;

    public static final String MODE_FLAG = "MODE_FLAG";

    public static final int ADD_MODE = 0;
    public static final int EDIT_MODE = 1;
    public static final int VIEW_MODE = 2;

    private int mode;
    ActionBar actionBar;

    public static Control activityCurrentControl;
    public static ParseObject activityCurrentControlObject;


    RelativeLayout relativeLayout;

    SectionsPagerAdapter mSectionsPagerAdapter;

    ViewPager viewPager;

    ParseObject currentProjectObject;
    private ArrayList<ParseObject> frequencyObjectList;
    private ArrayList<ParseObject> natureObjectList;
    private ArrayList<ParseObject> typeObjectList;
    private ArrayList<ParseObject> riskObjectList;
    private ArrayList<ParseObject> systemObjectList;
    private ArrayList<ParseObject> companyObjectList;
    private ArrayList<ParseUser> memberObjectList;

    private ArrayList<String> frequencyDescriptionList;
    private ArrayList<String> natureDescriptionList;
    private ArrayList<String> typeDescriptionList;
    private ArrayList<String> riskDescriptionList;
    private ArrayList<String> systemNameList;
    private ArrayList<String> companyNameList;
    private ArrayList<String> memberNameList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_or_view_control);

        // Set up the action bar.
        actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayHomeAsUpEnabled(true);

        relativeLayout = (RelativeLayout) findViewById(R.id.control_tab_relative_layout);


        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        viewPager = (ViewPager) findViewById(R.id.pager);

        // set Activity mode (Add, Edit or View)
        mode = getIntent().getIntExtra(MODE_FLAG, ADD_MODE);
        switch (mode) {
            case ADD_MODE:
                activityCurrentControl = new Control();
                loadControlContents();
                break;

            case EDIT_MODE:
                loadControlContents();
                loadActivityCurrentControl(getIntent().getStringExtra(Control.KEY_CONTROL_ID));
                break;

            case VIEW_MODE:
                loadActivityCurrentControl(getIntent().getStringExtra(Control.KEY_CONTROL_ID));
                break;
        }

        viewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.

        viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
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

    private void loadControlContents() {
        ProgressDialog progressDialog = new ProgressDialog(ControlActivity.this);
        progressDialog.show();
        loadCurrentProjectObject();

        progressDialog.setMessage(getString(R.string.loading_dialog_message_loading_details));
        loadDetailsSpinnerContents();

        progressDialog.setMessage(getString(R.string.loading_dialog_message_loading_system_list));
        loadProjectSystemListContents();

        progressDialog.setMessage(getString(R.string.loading_dialog_message_loading_company_list));
        loadProjectCompanyListContents();

        progressDialog.setMessage(getString(R.string.loading_dialog_message_loading_member_list));
        loadMemberListContents();

        progressDialog.dismiss();
    }

    private void loadActivityCurrentControl(String controlId) {
        relativeLayout.setAlpha(0.5f);
        ProgressDialog progressDialog = new ProgressDialog(ControlActivity.this);
        progressDialog.setMessage(getString(R.string.loading_dialog_message_loading_control));
        progressDialog.setCancelable(false);
        progressDialog.show();

        ParseQuery getCurrentControl = ParseQuery.getQuery(Control.TABLE_CONTROL);
        getCurrentControl.include(Control.KEY_CONTROL_COMPANY_SCOPE)
                .include(Control.KEY_CONTROL_SYSTEM_SCOPE)
                .include(Control.KEY_CONTROL_RISK)
                .include(Control.KEY_CONTROL_FREQUENCY)
                .include(Control.KEY_CONTROL_TYPE)
                .include(Control.KEY_CONTROL_NATURE)
                .include(Control.KEY_CONTROL_MEMBER_RESPONSIBLE);
        try {
            activityCurrentControlObject = getCurrentControl.get(controlId);
            activityCurrentControl = new Control(activityCurrentControlObject);
            actionBar.setTitle(activityCurrentControl.getName());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        progressDialog.dismiss();
        relativeLayout.setAlpha(1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_edit_or_view_control, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onSaveButtonClicked() {
        if (controlIsValid() && currentProjectObject != null) {
            final ProgressDialog progressDialog = ProgressDialog.show(ControlActivity.this, "",getString(R.string.loading_dialog_message_saving_control), true, false);
            progressDialog.setCancelable(false);

            ParseObject parseObjectControl;
            if (mode == EDIT_MODE)
                parseObjectControl = activityCurrentControlObject;
            else
                parseObjectControl = new ParseObject(Control.TABLE_CONTROL);

            parseObjectControl.put(Control.KEY_CONTROL_NAME, activityCurrentControl.getName());
            parseObjectControl.put(Control.KEY_CONTROL_DESCRIPTION, activityCurrentControl.getDescription());
            parseObjectControl.put(Control.KEY_CONTROL_OWNER, activityCurrentControl.getOwner());
            parseObjectControl.put(Control.KEY_CONTROL_POPULATION, activityCurrentControl.getPopulation());
            parseObjectControl.put(Control.KEY_CONTROL_RISK, activityCurrentControl.getRiskClassificationObject());
            parseObjectControl.put(Control.KEY_CONTROL_TYPE, activityCurrentControl.getTypeObject());
            parseObjectControl.put(Control.KEY_CONTROL_FREQUENCY, activityCurrentControl.getFrequencyObject());
            parseObjectControl.put(Control.KEY_CONTROL_NATURE, activityCurrentControl.getNatureObject());
            parseObjectControl.put(Control.KEY_CONTROL_SYSTEM_SCOPE, activityCurrentControl.getSystemScopeList());
            parseObjectControl.put(Control.KEY_CONTROL_COMPANY_SCOPE, activityCurrentControl.getCompanyScopeList());
            parseObjectControl.put(Control.KEY_CONTROL_PROJECT, currentProjectObject);
            parseObjectControl.put(Control.KEY_CONTROL_MEMBER_RESPONSIBLE, activityCurrentControl.getMemberResponsibleObject());

            parseObjectControl.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    progressDialog.dismiss();
                    if (e == null) {
                        Toast successToast = Toast.makeText(ControlActivity.this, R.string.dialog_message_saved_successfully, Toast.LENGTH_LONG);
                        successToast.setGravity(Gravity.CENTER, 0, 0);
                        successToast.show();
                        Intent intent = new Intent(ControlActivity.this, ProjectDashboardActivity.class);
                        intent.putExtra(ProjectDashboardActivity.KEY_COMING_FROM_ACTIVITY, ProjectDashboardActivity.COMING_FROM_CREATE_CONTROL);
                        startActivity(intent);
                        finish();
                    } else
                        ParseUtils.handleParseException(getApplicationContext(), e);
                }
            });
            progressDialog.dismiss();
        }
    }

    private void loadCurrentProjectObject() {
        String currentProjectId = ParseUtils.getStringFromSession(getApplicationContext(), ParseUtils.PREFS_CURRENT_PROJECT_ID);
        ParseQuery<ParseObject> getCurrentProjectQuery = ParseQuery.getQuery(Project.TABLE_PROJECT);
        getCurrentProjectQuery.include(Project.KEY_SYSTEM_SCOPE_LIST)
                .include(Project.KEY_COMPANY_SCOPE_LIST);
        try {
            currentProjectObject = getCurrentProjectQuery.get(currentProjectId);
        } catch (ParseException e) {
            ParseUtils.handleParseException(ControlActivity.this, e);
        }
    }

    private void loadDetailsSpinnerContents() {
        ParseQuery<ParseObject> getFrequencyDescription = ParseQuery.getQuery(Control.TABLE_CONTROL_FREQUENCY);
        ParseQuery<ParseObject> getNatureDescription = ParseQuery.getQuery(Control.TABLE_CONTROL_NATURE);
        ParseQuery<ParseObject> getRiskDescription = ParseQuery.getQuery(Control.TABLE_CONTROL_RISK);
        ParseQuery<ParseObject> getTypeDescription = ParseQuery.getQuery(Control.TABLE_CONTROL_TYPE);
        try {
            frequencyObjectList = (ArrayList<ParseObject>) getFrequencyDescription.find();
            frequencyDescriptionList = castParseObjectListToStringList(frequencyObjectList, Control.KEY_CONTROL_GENERIC_DESCRIPTION);
            frequencyDescriptionList.add(0, getString(R.string.add_control_details_frequency_label));

            natureObjectList = (ArrayList<ParseObject>) getNatureDescription.find();
            natureDescriptionList = castParseObjectListToStringList(natureObjectList, Control.KEY_CONTROL_GENERIC_DESCRIPTION);
            natureDescriptionList.add(0, getString(R.string.add_control_details_nature_label));

            riskObjectList = (ArrayList<ParseObject>) getRiskDescription.find();
            riskDescriptionList = castParseObjectListToStringList(riskObjectList, Control.KEY_CONTROL_GENERIC_DESCRIPTION);
            riskDescriptionList.add(0, getString(R.string.add_control_details_risk_classification_label));

            typeObjectList = (ArrayList<ParseObject>) getTypeDescription.find();
            typeDescriptionList = castParseObjectListToStringList(typeObjectList, Control.KEY_CONTROL_GENERIC_DESCRIPTION);
            typeDescriptionList.add(0, getString(R.string.add_control_details_type_label));
        } catch (ParseException e) {
            ParseUtils.handleParseException(ControlActivity.this, e);
        }
    }

    private void loadProjectSystemListContents() {
        systemObjectList = (ArrayList) currentProjectObject.getList(Project.KEY_SYSTEM_SCOPE_LIST);
        systemNameList = castParseObjectListToStringList(systemObjectList, SystemApp.KEY_SYSTEM_NAME);
    }

    private void loadProjectCompanyListContents() {
        companyObjectList = (ArrayList) currentProjectObject.getList(Project.KEY_COMPANY_SCOPE_LIST);
        companyNameList = castParseObjectListToStringList(companyObjectList, Company.KEY_COMPANY_NAME);
    }

    private void loadMemberListContents() {
        ParseRelation<ParseUser> projectUserRelation = currentProjectObject.getRelation(Project.KEY_PROJECT_USER_RELATION);
        ParseQuery<ParseUser> getProjectUserList = projectUserRelation.getQuery();
        try {
            memberObjectList = (ArrayList) getProjectUserList.find();
            if (memberObjectList != null) {
                memberNameList = castParseUserListToStringList(memberObjectList);
            } else
                memberNameList = new ArrayList();
        } catch (ParseException e) {
            ParseUtils.handleParseException(ControlActivity.this, e);
        }
    }

    private ArrayList<String> castParseObjectListToStringList(ArrayList<ParseObject> parseObjectList, String tableField) {
        ArrayList<String> output = new ArrayList<>();
        for (ParseObject obj : parseObjectList){
            String desc = obj.getString(tableField);
            output.add(desc);
        }
        return output;
    }

    private ArrayList<String> castParseUserListToStringList(ArrayList<ParseUser> parseObjectList) {
        ArrayList<String> output = new ArrayList<>();
        for (ParseUser user : parseObjectList){
            String userName = user.getUsername();
            output.add(userName);
        }
        return output;
    }

    private boolean controlIsValid() {
        ProgressDialog progressDialog = ProgressDialog.show(ControlActivity.this, "",getString(R.string.loading_dialog_message_validating_information), true, false);
        progressDialog.setCancelable(false);

        int count = 0;

        String fieldsRequired = "";
        if (activityCurrentControl.getFrequencyObject() == null) {
            count++;
            fieldsRequired = fieldsRequired.concat(getString(R.string.field_frequency_required));
        }

        if (activityCurrentControl.getNatureObject() == null) {
            count++;
            if (count > 1)
                fieldsRequired = fieldsRequired.concat(", ");
            fieldsRequired = fieldsRequired.concat(getString(R.string.field_nature_required));
        }

        if (activityCurrentControl.getTypeObject() == null){
            count++;
            if (count > 1)
                fieldsRequired = fieldsRequired.concat(", ");
            fieldsRequired = fieldsRequired.concat(getString(R.string.field_type_required));
        }

        if (activityCurrentControl.getRiskClassificationObject() == null) {
            count++;
            if (count > 1)
                fieldsRequired = fieldsRequired.concat(", ");
            fieldsRequired = fieldsRequired.concat(getString(R.string.field_risk_classification_required));
        }

        if (activityCurrentControl.getName().isEmpty()) {
            count++;
            if (count > 1)
                fieldsRequired = fieldsRequired.concat(", ");
            fieldsRequired = fieldsRequired.concat(getString(R.string.field_name_required));
        }

        if (activityCurrentControl.getDescription().isEmpty()) {
            count++;
            if (count > 1)
                fieldsRequired = fieldsRequired.concat(", ");
            fieldsRequired = fieldsRequired.concat(getString(R.string.field_description_required));
        }

        if (activityCurrentControl.getPopulation().isEmpty()) {
            count++;
            if (count > 1)
                fieldsRequired = fieldsRequired.concat(", ");
            fieldsRequired = fieldsRequired.concat(getString(R.string.field_population_required));
        }

        if (activityCurrentControl.getOwner().isEmpty()) {
            count++;
            if (count > 1)
                fieldsRequired = fieldsRequired.concat(", ");
            fieldsRequired = fieldsRequired.concat(getString(R.string.field_owner_required));
        }

        if (activityCurrentControl.getSystemScopeList() == null ||
                activityCurrentControl.getSystemScopeList().isEmpty()) {
            count++;
            if (count > 1)
                fieldsRequired = fieldsRequired.concat(", ");
            fieldsRequired = fieldsRequired.concat(getString(R.string.field_system_scope_required));
        }

        if (activityCurrentControl.getCompanyScopeList() == null ||
                activityCurrentControl.getCompanyScopeList().isEmpty()) {
            count++;
            if (count > 1)
                fieldsRequired = fieldsRequired.concat(", ");
            fieldsRequired = fieldsRequired.concat(getString(R.string.field_company_scope_required));
        }

        if (activityCurrentControl.getMemberResponsibleObject() == null) {
            count++;
            if (count > 1)
                fieldsRequired = fieldsRequired.concat(", ");
            fieldsRequired = fieldsRequired.concat(getString(R.string.field_member_responsible_required));
        }

        progressDialog.dismiss();

        if (count == 0)
            return true;
        else {
            String errorMessage = "";
            if (count > 1)
                errorMessage = getString(R.string.control_invalid_error_message_plural);
            else if (count == 1)
                errorMessage = getString(R.string.control_invalid_error_message_single);

            String finalMessage = errorMessage.concat(fieldsRequired);
            AlertDialog.Builder builder = new AlertDialog.Builder(ControlActivity.this);
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
    public void saveSelectedMemberResponsibleToActivityControl(String selectedMemberResponsibleName) {
        if (selectedMemberResponsibleName != null)
            activityCurrentControl.setMemberResponsible(memberObjectList.get(memberNameList.indexOf(selectedMemberResponsibleName)));
    }

    @Override
    public void saveNameToActivityControl(String name) {
        if (name != null)
            activityCurrentControl.setName(name);
    }

    @Override
    public void saveDescriptionToActivityControl(String description) {
        if (description != null)
            activityCurrentControl.setDescription(description);
    }

    @Override
    public void savePopulationToActivityControl(String population) {
        if (population != null)
            activityCurrentControl.setPopulation(population);
    }

    @Override
    public void saveOwnerToActivityControl(String owner) {
        if (owner != null)
            activityCurrentControl.setOwner(owner);
    }

    @Override
    public void saveRiskToActivityControl(String riskDescription) {
        if (riskDescription != null)
            activityCurrentControl.setRiskClassificationObject(riskObjectList.get(riskDescriptionList.indexOf(riskDescription)));
    }

    @Override
    public void saveTypeToActivityControl(String typeDescription) {
        if (typeDescription != null)
            activityCurrentControl.setTypeObject(typeObjectList.get(typeDescriptionList.indexOf(typeDescription)));
    }

    @Override
    public void saveFrequencyToActivityControl(String frequencyDescription) {
        if (frequencyDescription != null)
            activityCurrentControl.setFrequencyObject(frequencyObjectList.get(frequencyDescriptionList.indexOf(frequencyDescription)));
    }

    @Override
    public void saveNatureToActivityControl(String natureDescription) {
        if (natureDescription != null)
            activityCurrentControl.setNatureObject(natureObjectList.get(natureDescriptionList.indexOf(natureDescription)));
    }

    @Override
    public void saveSelectedCompaniesToActivityControl(ArrayList<String> selectedCompanyItems) {
        if (selectedCompanyItems != null) {
            ArrayList<ParseObject> tempSelectedCompanyObjectList = new ArrayList<>();
            for (String companyName : selectedCompanyItems) {
                int position = companyNameList.indexOf(companyName);
                tempSelectedCompanyObjectList.add(companyObjectList.get(position));
            }
            activityCurrentControl.setCompanyList(tempSelectedCompanyObjectList);
        }
    }

    @Override
    public void saveSelectedSystemsToActivityControl(ArrayList<String> selectedSystemItems) {
        if (selectedSystemItems != null) {
            ArrayList<ParseObject> tempSelectedSystemObjectList = new ArrayList<>();
            for (String systemName : selectedSystemItems) {
                int position = systemNameList.indexOf(systemName);
                tempSelectedSystemObjectList.add(systemObjectList.get(position));
            }
            activityCurrentControl.setSystemList(tempSelectedSystemObjectList);
        }
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case TAB_DETAILS:
                    ControlDetailsTabFragment detailsFragment = new ControlDetailsTabFragment();
                    Bundle detailsArgs = new Bundle();
                    detailsArgs.putInt(MODE_FLAG, mode);
                    if (mode == EDIT_MODE || mode == ADD_MODE) {
                        detailsArgs.putStringArrayList(ControlDetailsTabFragment.FREQUENCY_LIST_CONTENT, frequencyDescriptionList);
                        detailsArgs.putStringArrayList(ControlDetailsTabFragment.TYPE_LIST_CONTENT, typeDescriptionList);
                        detailsArgs.putStringArrayList(ControlDetailsTabFragment.NATURE_LIST_CONTENT, natureDescriptionList);
                        detailsArgs.putStringArrayList(ControlDetailsTabFragment.RISK_LIST_CONTENT, riskDescriptionList);
                    }
                    if (mode == VIEW_MODE || mode == EDIT_MODE){
                        detailsArgs.putString(ControlDetailsTabFragment.DETAILS_ARGS_NAME, activityCurrentControl.getName());
                        detailsArgs.putString(ControlDetailsTabFragment.DETAILS_ARGS_DESCRIPTION, activityCurrentControl.getDescription());
                        detailsArgs.putString(ControlDetailsTabFragment.DETAILS_ARGS_POPULATION, activityCurrentControl.getPopulation());
                        detailsArgs.putString(ControlDetailsTabFragment.DETAILS_ARGS_OWNER, activityCurrentControl.getOwner());
                        detailsArgs.putString(ControlDetailsTabFragment.DETAILS_ARGS_FREQUENCY, activityCurrentControl.getFrequencyObject().getString(Control.KEY_CONTROL_GENERIC_DESCRIPTION));
                        detailsArgs.putString(ControlDetailsTabFragment.DETAILS_ARGS_TYPE, activityCurrentControl.getTypeObject().getString(Control.KEY_CONTROL_GENERIC_DESCRIPTION));
                        detailsArgs.putString(ControlDetailsTabFragment.DETAILS_ARGS_NATURE, activityCurrentControl.getNatureObject().getString(Control.KEY_CONTROL_GENERIC_DESCRIPTION));
                        detailsArgs.putString(ControlDetailsTabFragment.DETAILS_ARGS_RISK, activityCurrentControl.getRiskClassificationObject().getString(Control.KEY_CONTROL_GENERIC_DESCRIPTION));
                        detailsFragment.setArguments(detailsArgs);
                    }
                    return detailsFragment;

                case TAB_SYSTEM_SCOPE:
                    ControlSystemScopeTabFragment systemScopeFragment = new ControlSystemScopeTabFragment();
                    Bundle systemScopeArgs = new Bundle();
                    systemScopeArgs.putInt(MODE_FLAG, mode);
                    if (mode == EDIT_MODE || mode == ADD_MODE) {
                        systemScopeArgs.putStringArrayList(ControlSystemScopeTabFragment.SYSTEM_LIST_CONTENT, systemNameList);
                    }
                    if (mode == VIEW_MODE || mode == EDIT_MODE){
                        ArrayList<ParseObject> currentSystemScope = activityCurrentControl.getSystemScopeList();
                        ArrayList<String> currentSystemScopeNameList = new ArrayList<>();
                        for (ParseObject system : currentSystemScope) {
                            currentSystemScopeNameList.add(system.getString(SystemApp.KEY_SYSTEM_NAME));
                        }
                        systemScopeArgs.putStringArrayList(ControlSystemScopeTabFragment.SYSTEM_SCOPE_ARGS_SELECTED_NAME_LIST, currentSystemScopeNameList);
                        systemScopeFragment.setArguments(systemScopeArgs);
                    }
                    return systemScopeFragment;

                case TAB_COMPANY_SCOPE:
                    ControlCompanyScopeTabFragment companyScopeFragment = new ControlCompanyScopeTabFragment();
                    Bundle companyScopeArgs = new Bundle();
                    companyScopeArgs.putInt(MODE_FLAG, mode);
                    if (mode == EDIT_MODE || mode == ADD_MODE) {
                        companyScopeArgs.putStringArrayList(ControlCompanyScopeTabFragment.COMPANY_LIST_CONTENT, companyNameList);
                    }
                    if (mode == VIEW_MODE || mode == EDIT_MODE){
                        ArrayList<ParseObject> currentCompanyScope = activityCurrentControl.getCompanyScopeList();
                        ArrayList<String> currentCompanyScopeNameList = new ArrayList<>();
                        for (ParseObject company : currentCompanyScope) {
                            currentCompanyScopeNameList.add(company.getString(Company.KEY_COMPANY_NAME));
                        }
                        companyScopeArgs.putStringArrayList(ControlCompanyScopeTabFragment.COMPANY_SCOPE_ARGS_SELECTED_NAME_LIST, currentCompanyScopeNameList);
                        companyScopeFragment.setArguments(companyScopeArgs);
                    }
                    return companyScopeFragment;

                case TAB_RESPONSIBLE:
                    ControlMemberResponsibleTabFragment responsibleFragment = new ControlMemberResponsibleTabFragment();
                    Bundle memberResponsibleArgs = new Bundle();
                    memberResponsibleArgs.putInt(MODE_FLAG, mode);
                    if (mode == EDIT_MODE || mode == ADD_MODE) {
                        memberResponsibleArgs.putStringArrayList(ControlMemberResponsibleTabFragment.MEMBER_LIST_CONTENT, memberNameList);
                    }
                    if (mode == VIEW_MODE || mode == EDIT_MODE) {
                        memberResponsibleArgs.putString(ControlMemberResponsibleTabFragment.MEMBER_RESPONSIBLE_ARGS_SELECTED_NAME, activityCurrentControl.getMemberResponsibleObject().getUsername());
                        responsibleFragment.setArguments(memberResponsibleArgs);
                    }
                    return responsibleFragment;
            }
            return null;
        }

        @Override
        public int getCount() {
            // Show 4 total pages.
            return 4;
        }

        @Override
        public String getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case TAB_DETAILS:
                    return getString(R.string.add_control_tab_view_section_details_title).toUpperCase(l);

                case TAB_SYSTEM_SCOPE:
                    return getString(R.string.add_control_tab_view_section_system_scope_title).toUpperCase(l);

                case TAB_COMPANY_SCOPE:
                    return getString(R.string.add_control_tab_view_section_company_scope_title).toUpperCase(l);

                case TAB_RESPONSIBLE:
                    return getString(R.string.add_control_tab_view_section_responsible_title).toUpperCase(l);
            }
            return null;
        }
    }

}
