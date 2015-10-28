package com.vieira.rodrigo.itgcmanager;

import java.util.ArrayList;
import java.util.Locale;

import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import com.parse.ParseObject;
import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.fragments.ControlCompanyScopeFragment;
import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.fragments.ControlDetailsFragment;
import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.fragments.ControlResponsibleFragment;
import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.fragments.ControlSystemScopeFragment;
import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.models.Control;


public class AddEditOrViewControlActivity extends ActionBarActivity implements ActionBar.TabListener,
        ControlDetailsFragment.OnFragmentInteractionListener, ControlSystemScopeFragment.OnFragmentInteractionListener,
        ControlCompanyScopeFragment.OnFragmentInteractionListener, ControlResponsibleFragment.OnFragmentInteractionListener {

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
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_or_view_control);

        // Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayHomeAsUpEnabled(true);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
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
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onSaveButtonClicked() {

    }

    @Override
    public void addSelectedCompaniesToActivityControl(ArrayList<ParseObject> selectedCompanyItems) {
        activityCurrentControl.setCompanyList(selectedCompanyItems);
    }

    @Override
    public void addSelectedSystemsToActivityControl(ArrayList<ParseObject> selectedSystemItems) {
        activityCurrentControl.setSystemList(selectedSystemItems);
    }

    @Override
    public void syncControlWithActivity(Control control) {
        activityCurrentControl = control;
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
                    ControlDetailsFragment detailsFragment = new ControlDetailsFragment();
                    return detailsFragment;

                case TAB_SYSTEM_SCOPE:
                    ControlSystemScopeFragment systemScopeFragment = new ControlSystemScopeFragment();
                    return systemScopeFragment;

                case TAB_COMPANY_SCOPE:
                    ControlCompanyScopeFragment companyScopeFragment = new ControlCompanyScopeFragment();
                    return companyScopeFragment;

                case TAB_RESPONSIBLE:
                    ControlResponsibleFragment responsibleFragment = new ControlResponsibleFragment();
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
}
