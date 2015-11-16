package com.vieira.rodrigo.itgcmanager;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.widget.DrawerLayout;

import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.fragments.CompanyListFragment;
import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.fragments.ControlListFragment;
import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.fragments.ProjectDashboardNavigationDrawerFragment;
import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.fragments.ProjectDetailsFragment;
import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.fragments.SystemListFragment;
import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.fragments.TeamMemberListFragment;
import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.fragments.TestListFragment;

public class ProjectDashboardActivity extends ActionBarActivity
        implements ProjectDashboardNavigationDrawerFragment.NavigationDrawerCallbacks, ControlListFragment.OnControlItemClickedListener {

    public static final int HOME_SECTION = 1;
    public static final int TEAM_SECTION = 2;
    public static final int SYSTEM_SCOPE_SECTION = 3;
    public static final int COMPANY_SCOPE_SECTION = 4;
    public static final int CONTROLS_SECTION = 5;
    public static final int TESTS_SECTION = 6;
    public static final int PROJECT_LIST = 7;

    public static final int COMING_FROM_CREATE_SYSTEM = 0;
    public static final int COMING_FROM_CREATE_COMPANY = 1;
    public static final int COMING_FROM_CREATE_TEAM_MEMBER = 2;
    public static final int COMING_FROM_CREATE_CONTROL = 3;
    public static final int COMING_FROM_CREATE_TEST = 4;

    public static final String KEY_COMING_FROM_ACTIVITY = "key_coming_from_activity";
    public static final String KEY_CONTROL_ID = "key_control_id";

    boolean projectEditMode = false;
    int comingFromActivity;

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

        FragmentManager fragmentManager = getSupportFragmentManager();

        projectEditMode = getIntent().getBooleanExtra(ProjectActivity.EDIT_MODE_FLAG, false);
        if (projectEditMode) {
            ProjectDetailsFragment detailsFragment = new ProjectDetailsFragment();
            fragmentManager.beginTransaction()
                    .replace(R.id.container, detailsFragment)
                    .commit();
        }

        comingFromActivity = getIntent().getIntExtra(KEY_COMING_FROM_ACTIVITY, -1);
        switch (comingFromActivity) {
            case COMING_FROM_CREATE_CONTROL:
                ControlListFragment controlListFragment = new ControlListFragment();
                fragmentManager.beginTransaction()
                        .replace(R.id.container, controlListFragment)
                        .commit();
                break;

            case COMING_FROM_CREATE_COMPANY:
                CompanyListFragment companyListFragment = new CompanyListFragment();
                fragmentManager.beginTransaction()
                        .replace(R.id.container, companyListFragment)
                        .commit();
                break;

            case COMING_FROM_CREATE_SYSTEM:
                SystemListFragment systemListFragment = new SystemListFragment();
                fragmentManager.beginTransaction()
                        .replace(R.id.container, systemListFragment)
                        .commit();
                break;

            case COMING_FROM_CREATE_TEAM_MEMBER:
                TeamMemberListFragment teamMemberListFragment = new TeamMemberListFragment();
                fragmentManager.beginTransaction()
                        .replace(R.id.container, teamMemberListFragment)
                        .commit();
                break;

            case COMING_FROM_CREATE_TEST:
                TestListFragment testListFragment = new TestListFragment();
                fragmentManager.beginTransaction()
                        .replace(R.id.container, testListFragment)
                        .commit();
                break;

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

            case TESTS_SECTION:
                TestListFragment testListFragment = new TestListFragment();
                fragmentManager.beginTransaction()
                        .replace(R.id.container, testListFragment)
                        .commit();
                break;

            case PROJECT_LIST:
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
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
            case TESTS_SECTION:
                mTitle = getString(R.string.title_test_section);
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
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onControlItemClicked(String controlId) {
        Bundle testArgs = new Bundle();
        testArgs.putString(KEY_CONTROL_ID, controlId);
        FragmentManager fragmentManager = getSupportFragmentManager();
        TestListFragment testListFragment = new TestListFragment();
        testListFragment.setArguments(testArgs);

        fragmentManager.beginTransaction()
                .replace(R.id.container, testListFragment)
                .commit();
    }
}
