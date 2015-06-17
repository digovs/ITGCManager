package com.vieira.rodrigo.itgcmanager;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;

import com.parse.ParseUser;
import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.fragments.CompanyListFragment;
import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.fragments.ControlListFragment;
import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.fragments.ProjectDashboardNavigationDrawerFragment;
import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.fragments.ProjectDetailsFragment;
import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.fragments.SystemListFragment;
import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.fragments.TeamMemberListFragment;

public class ProjectDashboardActivity extends ActionBarActivity
        implements ProjectDashboardNavigationDrawerFragment.NavigationDrawerCallbacks {

    public static final int HOME_SECTION = 1;
    public static final int TEAM_SECTION = 2;
    public static final int SYSTEM_SCOPE_SECTION = 3;
    public static final int COMPANY_SCOPE_SECTION = 4;
    public static final int CONTROLS_SECTION = 5;
    public static final int LOG_OUT_SECTION = 6;

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private ProjectDashboardNavigationDrawerFragment mProjectDashboardNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_dashboard);

        mProjectDashboardNavigationDrawerFragment = (ProjectDashboardNavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mProjectDashboardNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        switch (position+1) {
            case HOME_SECTION:
                ProjectDetailsFragment detailsFragment = new ProjectDetailsFragment();
                fragmentManager.beginTransaction()
                        .replace(R.id.container, detailsFragment)
                        .commit();
                break;

            case TEAM_SECTION:
                TeamMemberListFragment teamMemberListFragment = new TeamMemberListFragment();
                fragmentManager.beginTransaction()
                        .replace(R.id.container, teamMemberListFragment)
                        .commit();

                break;

            case SYSTEM_SCOPE_SECTION:
                SystemListFragment systemListFragment = new SystemListFragment();
                fragmentManager.beginTransaction()
                        .replace(R.id.container, systemListFragment)
                        .commit();
                break;

            case COMPANY_SCOPE_SECTION:
                CompanyListFragment companyListFragment = new CompanyListFragment();
                fragmentManager.beginTransaction()
                        .replace(R.id.container, companyListFragment)
                        .commit();
                break;

            case CONTROLS_SECTION:
                ControlListFragment controlListFragment = new ControlListFragment();
                fragmentManager.beginTransaction()
                        .replace(R.id.container, controlListFragment)
                        .commit();
                break;

            case LOG_OUT_SECTION:
                ParseUser.logOut();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
                break;
        }

    }

    public void onSectionAttached(int number) {
        switch (number) {
            case HOME_SECTION:
                mTitle = getString(R.string.title_home_section);
                break;
            case TEAM_SECTION:
                mTitle = getString(R.string.title_team_section);
                break;
            case SYSTEM_SCOPE_SECTION:
                mTitle = getString(R.string.title_system_section);
                break;
            case COMPANY_SCOPE_SECTION:
                mTitle = getString(R.string.title_company_section);
                break;
            case CONTROLS_SECTION:
                mTitle = getString(R.string.title_control_section);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mProjectDashboardNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.project_dashboard, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
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

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_project_dashboard, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((ProjectDashboardActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

}
