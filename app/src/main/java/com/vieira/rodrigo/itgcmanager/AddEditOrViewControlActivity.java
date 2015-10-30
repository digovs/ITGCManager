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
import android.widget.ProgressBar;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.Utils.ParseUtils;
import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.fragments.ControlCompanyScopeTabFragment;
import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.fragments.ControlDetailsTabFragment;
import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.fragments.ControlMemberResponsibleTabFragment;
import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.fragments.ControlSystemScopeTabFragment;
import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.models.Control;
import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.models.Project;


public class AddEditOrViewControlActivity extends ActionBarActivity implements ActionBar.TabListener,
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



    public static Control activityCurrentControl;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager viewPager;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_or_view_control);

        // Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayHomeAsUpEnabled(true);

        activityCurrentControl = new Control();

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(mSectionsPagerAdapter);

        // Set up the progress bar
        progressBar = (ProgressBar) findViewById(R.id.control_tab_view_progress_bar);

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
        if (id == R.id.action_settings) {
            return true;
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
        if (controlIsValid()) {
            final ProgressDialog progressDialog = ProgressDialog.show(AddEditOrViewControlActivity.this, "",getString(R.string.loading_dialog_message_saving_control), true, false);
            progressDialog.setCancelable(false);

            ParseObject currentProjectObject = getCurrentProjectObject();
            if (currentProjectObject != null) {
                ParseObject newParseObjectControl = new ParseObject(Control.TABLE_CONTROL);
                newParseObjectControl.put(Control.KEY_CONTROL_NAME, activityCurrentControl.getName());
                newParseObjectControl.put(Control.KEY_CONTROL_DESCRIPTION, activityCurrentControl.getDescription());
                newParseObjectControl.put(Control.KEY_CONTROL_OWNER, activityCurrentControl.getOwner());
                newParseObjectControl.put(Control.KEY_CONTROL_POPULATION, activityCurrentControl.getPopulation());
                newParseObjectControl.put(Control.KEY_CONTROL_RISK, activityCurrentControl.getRiskClassificationObject());
                newParseObjectControl.put(Control.KEY_CONTROL_TYPE, activityCurrentControl.getTypeObject());
                newParseObjectControl.put(Control.KEY_CONTROL_FREQUENCY, activityCurrentControl.getFrequencyObject());
                newParseObjectControl.put(Control.KEY_CONTROL_NATURE, activityCurrentControl.getNatureObject());
                newParseObjectControl.put(Control.KEY_CONTROL_SYSTEM_SCOPE, activityCurrentControl.getSystemScopeList());
                newParseObjectControl.put(Control.KEY_CONTROL_COMPANY_SCOPE, activityCurrentControl.getCompanyScopeList());
                newParseObjectControl.put(Control.KEY_CONTROL_PROJECT, currentProjectObject);
                newParseObjectControl.put(Control.KEY_CONTROL_MEMBER_RESPONSIBLE, activityCurrentControl.getMemberResponsibleObject());

                newParseObjectControl.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        progressDialog.dismiss();
                        if (e == null) {
                            Toast successToast = Toast.makeText(AddEditOrViewControlActivity.this, R.string.dialog_message_saved_successfully, Toast.LENGTH_LONG);
                            successToast.setGravity(Gravity.CENTER, 0, 0);
                            successToast.show();
                            Intent intent = new Intent(AddEditOrViewControlActivity.this, ProjectDashboardActivity.class);
                            intent.putExtra(ProjectDashboardActivity.KEY_COMING_FROM_ACTIVITY, ProjectDashboardActivity.COMING_FROM_CREATE_CONTROL);
                            startActivity(intent);
                            finish();
                        } else
                            ParseUtils.handleParseException(getApplicationContext(), e);
                    }
                });
            }
            progressDialog.dismiss();
        }
    }

    private ParseObject getCurrentProjectObject() {
        String currentProjectId = ParseUtils.getStringFromSession(getApplicationContext(), ParseUtils.PREFS_CURRENT_PROJECT_ID);
        ParseQuery<ParseObject> getCurrentProjectQuery = ParseQuery.getQuery(Project.TABLE_PROJECT);
        try {
            return getCurrentProjectQuery.get(currentProjectId);
        } catch (ParseException e) {
            return null;
        }
    }

    private boolean controlIsValid() {
        ProgressDialog progressDialog = ProgressDialog.show(AddEditOrViewControlActivity.this, "",getString(R.string.loading_dialog_message_validating_information), true, false);
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
            AlertDialog.Builder builder = new AlertDialog.Builder(AddEditOrViewControlActivity.this);
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
    public void saveSelectedMemberResponsibleToActivityControl(ParseUser selectedMemberResponsible) {
        if (selectedMemberResponsible != null)
            activityCurrentControl.setMemberResponsible(selectedMemberResponsible);
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
    public void saveRiskToActivityControl(ParseObject risk) {
        if (risk != null)
            activityCurrentControl.setRiskClassificationObject(risk);
    }

    @Override
    public void saveTypeToActivityControl(ParseObject type) {
        if (type != null)
            activityCurrentControl.setTypeObject(type);
    }

    @Override
    public void saveFrequencyToActivityControl(ParseObject frequency) {
        if (frequency != null)
            activityCurrentControl.setFrequencyObject(frequency);
    }

    @Override
    public void saveNatureToActivityControl(ParseObject nature) {
        if (nature != null)
            activityCurrentControl.setNatureObject(nature);
    }

    @Override
    public void saveSelectedCompaniesToActivityControl(ArrayList<ParseObject> selectedCompanyItems) {
        if (selectedCompanyItems != null)
            activityCurrentControl.setCompanyList(selectedCompanyItems);
    }

    @Override
    public void saveSelectedSystemsToActivityControl(ArrayList<ParseObject> selectedSystemItems) {
        if (selectedSystemItems != null)
            activityCurrentControl.setSystemList(selectedSystemItems);
    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            // return PlaceholderFragment.newInstance(position + 1);

            switch (position) {
                case TAB_DETAILS:
                    ControlDetailsTabFragment detailsFragment = new ControlDetailsTabFragment();
                    return detailsFragment;

                case TAB_SYSTEM_SCOPE:
                    ControlSystemScopeTabFragment systemScopeFragment = new ControlSystemScopeTabFragment();
                    return systemScopeFragment;

                case TAB_COMPANY_SCOPE:
                    ControlCompanyScopeTabFragment companyScopeFragment = new ControlCompanyScopeTabFragment();
                    return companyScopeFragment;

                case TAB_RESPONSIBLE:
                    ControlMemberResponsibleTabFragment responsibleFragment = new ControlMemberResponsibleTabFragment();
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
        public CharSequence getPageTitle(int position) {
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

    /*@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        float gone = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            viewPager.setVisibility(show ? View.GONE : View.VISIBLE);
            viewPager.animate().setDuration(shortAnimTime).alpha(
                    show ? gone : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    viewPager.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            progressBar.animate().setDuration(shortAnimTime).alpha(
                    show ? gone : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            progressBar.setAlpha(show ? 1 : gone);
            viewPager.setAlpha(show ? gone : 1);
        }
    }*/

    public void showProgress(final boolean show) {
        ProgressDialog progressDialog = new ProgressDialog(AddEditOrViewControlActivity.this);
        progressDialog.setMessage(getString(R.string.loading_dialog_message_saving_control));

        if (show)
            progressDialog.show();
        else
            progressDialog.dismiss();
    }
}
