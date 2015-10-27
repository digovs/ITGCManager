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

    boolean projectEditMode = false;

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

        projectEditMode = getIntent().getBooleanExtra(CreateProjectActivity.EDIT_MODE_FLAG, false);
        if (projectEditMode) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            ProjectDetailsFragment detailsFragment = new ProjectDetailsFragment();
            fragmentManager.beginTransaction()
                    .replace(R.id.container, detailsFragment)
                    .commit();
        }
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

}
