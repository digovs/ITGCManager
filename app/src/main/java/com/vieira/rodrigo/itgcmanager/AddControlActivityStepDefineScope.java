package com.vieira.rodrigo.itgcmanager;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.parse.DeleteCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;
import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.Utils.ParseUtils;
import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.adapters.ScopeListAdapter;
import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.dialogs.SelectorDialogFragment;
import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.dialogs.SystemScopeSelectorDialogActivity;
import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.models.Company;
import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.models.Control;
import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.models.Project;
import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.models.Scope;
import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.models.SystemApp;

import java.util.ArrayList;


public class AddControlActivityStepDefineScope extends ActionBarActivity implements SelectorDialogFragment.SelectorDialogListener{

    public static final int REQUEST_SCOPE_SELECTOR = 5776;
    public static final String KEY_REQUEST_CODE = "requestCode";
    public static final int REQUEST_DEFINE_RESPONSIBLE = 345;

    ArrayList<ParseObject> controlScopeList = new ArrayList<>();
    ArrayList<ParseObject> projectCompanyList = new ArrayList<>();
    ParseObject selectedScope;
    Control newControl;

    ListView scopeListView;
    ScopeListAdapter scopeListAdapter;
    RelativeLayout formView;

    ProgressBar progressBar;
    TextView emptyText;

    Button addScopeButton;
    Button continueButton;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_control_step_define_scope);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);


        newControl = (Control) getIntent().getSerializableExtra(AddControlActivityStepDetails.KEY_NEW_CONTROL);

        formView = (RelativeLayout) findViewById(R.id.add_control_define_scope_form_view);
        progressBar = (ProgressBar) findViewById(R.id.add_control_define_scope_progress_bar);
        emptyText = (TextView) findViewById(R.id.add_control_define_scope_empty_list_message);

        loadProjectCompanyList();

        scopeListView = (ListView) findViewById(R.id.add_control_define_scope_list);
        scopeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                callDialogForEditingScope(position);
            }
        });


        addScopeButton = (Button) findViewById(R.id.add_control_define_scope_add_button);
        addScopeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callSystemSelectorDialog();
            }
        });

        continueButton = (Button) findViewById(R.id.add_control_define_scope_continue_button);
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (controlScopeList.isEmpty()) {
                    DialogFragment errorDialogFragment = new ScopeRequiredErrorMessageDialogFragment();
                    errorDialogFragment.show(getSupportFragmentManager(), "error");
                } else {
                    ArrayList<String> controlScopeIdList = new ArrayList<>();
                    for (ParseObject controlScope : controlScopeList) {
                        controlScopeIdList.add(controlScope.getObjectId());
                    }
                    newControl.setControlScopeIdList(controlScopeIdList);
                    Intent intent = new Intent(getApplicationContext(), AddControlActivityStepDefineResponsible.class);
                    intent.putExtra(Control.KEY_NEW_CONTROL, newControl);
                    startActivityForResult(intent, REQUEST_DEFINE_RESPONSIBLE);
                }
            }
        });
    }

    private void callDialogForEditingScope(int position) {
        SelectorDialogFragment selectorFragment = new SelectorDialogFragment();
        Bundle args = new Bundle();
        args.putBoolean(SelectorDialogFragment.KEY_EDIT_MODE, true);

        selectedScope = controlScopeList.get(position);
        String systemName = selectedScope.getParseObject(Scope.KEY_SYSTEM).getString(SystemApp.KEY_SYSTEM_NAME);
        args.putString(SelectorDialogFragment.KEY_TITLE_ARGS, systemName);

        ArrayList<ParseObject> selectedScopeCompanyList = (ArrayList) selectedScope.getList(Scope.KEY_COMPANY_LIST);
        boolean[] selectedItems = new boolean[projectCompanyList.size()];
        for (int i = 0; i < projectCompanyList.size(); i++) {
            if (selectedScopeCompanyList.contains(projectCompanyList.get(i)))
                selectedItems[i] = true;
            else
                selectedItems[i] = false;
        }
        args.putBooleanArray(SelectorDialogFragment.KEY_SELECTED_ITEMS_LIST_ARGS, selectedItems);

        CharSequence[] items = new CharSequence[projectCompanyList.size()];
        for (int i = 0; i < projectCompanyList.size(); i++) {
            items[i] = projectCompanyList.get(i).getString(Company.KEY_COMPANY_NAME);
        }
        args.putCharSequenceArray(SelectorDialogFragment.KEY_ITEM_LIST_ARGS, items);

        selectorFragment.setArguments(args);
        selectorFragment.show(getSupportFragmentManager(), "TAG");
    }

    private void callSystemSelectorDialog() {
        Intent intent = new Intent(getApplicationContext(), SystemScopeSelectorDialogActivity.class);
        ArrayList<String> listOfAlreadySelectedSystemIds = new ArrayList<>();
        if (controlScopeList != null && !controlScopeList.isEmpty()) {
            for (ParseObject controlScope : controlScopeList) {
                String systemId = controlScope.getParseObject(Scope.KEY_SYSTEM).getObjectId();
                listOfAlreadySelectedSystemIds.add(systemId);
            }
        }
        intent.putExtra(SystemScopeSelectorDialogActivity.KEY_LIST_OF_IDS_TO_EXCLUDE, listOfAlreadySelectedSystemIds);
        intent.putExtra(KEY_REQUEST_CODE, REQUEST_SCOPE_SELECTOR);
        startActivityForResult(intent, REQUEST_SCOPE_SELECTOR);
    }

    private void loadProjectCompanyList() {
        showProgress(true);
        String currentProjectId = ParseUtils.getStringFromSession(getApplicationContext(), ParseUtils.PREFS_CURRENT_PROJECT_ID);
        ParseQuery<ParseObject> getProjectCompanies = ParseQuery.getQuery(Project.TABLE_PROJECT);
        getProjectCompanies.include(Project.KEY_COMPANY_SCOPE_LIST);
        getProjectCompanies.getInBackground(currentProjectId, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject project, ParseException e) {
                showProgress(false);
                if (e == null)
                    projectCompanyList = (ArrayList) project.getList(Project.KEY_COMPANY_SCOPE_LIST);
                else
                    ParseUtils.handleParseException(getApplicationContext(), e);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_SCOPE_SELECTOR:
                if (resultCode == RESULT_OK && data != null) {
                    String newScopeId = data.getStringExtra(SystemScopeSelectorDialogActivity.KEY_SCOPE_ID);
                    addScopeToList(newScopeId);
                }
        }

    }

    @Override
    public void onSaveButtonClicked(boolean[] selectedItems, boolean editMode) {
        showProgress(true);
        ArrayList<ParseObject> selectedScopeCompanyList = new ArrayList<>();
        for (int i = 0; i < selectedItems.length; i++) {
            if (selectedItems[i] == true)
                selectedScopeCompanyList.add(projectCompanyList.get(i));
        }

        if (!editMode) {
            final ParseObject newScope = new ParseObject(Scope.TABLE_SCOPE);
            newScope.put(Scope.KEY_SYSTEM, selectedScope.getParseObject(Scope.KEY_SYSTEM));
            newScope.put(Scope.KEY_COMPANY_LIST, selectedScopeCompanyList);

            newScope.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    showProgress(false);
                    if (e == null) {
                        addScopeToList(newScope.getObjectId());
                    } else {
                        ParseUtils.handleParseException(getApplicationContext(), e);
                    }

                }
            });
        }
        else {
            controlScopeList.remove(selectedScope);
            selectedScope.put(Scope.KEY_COMPANY_LIST, selectedScopeCompanyList);
            selectedScope.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        controlScopeList.add(selectedScope);
                        scopeListAdapter.swapScopeList(controlScopeList);
                    } else {
                        ParseUtils.handleParseException(getApplicationContext(), e);
                    }
                    showProgress(false);
                }
            });
        }
    }

    private void addScopeToList(String newScopeId) {
        ParseQuery getNewScopeObject = ParseQuery.getQuery(Scope.TABLE_SCOPE);
        getNewScopeObject.include(Scope.KEY_COMPANY_LIST);
        getNewScopeObject.getInBackground(newScopeId, new GetCallback() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                if (e != null) {
                    showProgress(false);
                    ParseUtils.handleParseException(getApplicationContext(), e);
                }
            }

            @Override
            public void done(Object o, Throwable throwable) {
                if (o != null) {
                    controlScopeList.add((ParseObject) o);
                    scopeListAdapter = new ScopeListAdapter(getApplicationContext(), controlScopeList);
                    scopeListView.setAdapter(scopeListAdapter);
                    showProgress(false);
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
    public void onDeleteButtonClicked(boolean editMode) {
        if (editMode) {
            showProgress(true);
            selectedScope.deleteInBackground(new DeleteCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        controlScopeList.remove(selectedScope);
                        scopeListAdapter.swapScopeList(controlScopeList);
                    } else {
                        ParseUtils.handleParseException(getApplicationContext(), e);
                    }
                    showProgress(false);
                }
            });

        }

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
            if (controlScopeList == null || controlScopeList.isEmpty())
                emptyText.setVisibility(View.VISIBLE);
            else if (controlScopeList != null && !controlScopeList.isEmpty())
                emptyText.setVisibility(View.GONE);

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

    public static class ScopeRequiredErrorMessageDialogFragment extends DialogFragment {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(getString(R.string.add_control_error_fragment_title))
                    .setMessage(R.string.add_control_scope_error_fragment_message)
                    .setNeutralButton(R.string.add_control_error_fragment_ok_button, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dismiss();
                        }
                    });

            return builder.create();
        }
    }
}
