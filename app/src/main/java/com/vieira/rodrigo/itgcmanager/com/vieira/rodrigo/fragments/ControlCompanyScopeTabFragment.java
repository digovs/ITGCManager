package com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.vieira.rodrigo.itgcmanager.R;
import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.Utils.ParseUtils;
import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.models.Control;
import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.models.Project;
import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.models.Company;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ControlCompanyScopeTabFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class ControlCompanyScopeTabFragment extends Fragment {

    ArrayList<CharSequence> projectCompanyCharSequenceList;
    private ArrayList projectCompanyObjectList;
    private ArrayList selectedCompanyObjects = new ArrayList();

    Control currentControl;
    ListView companyListView;
    RelativeLayout formView;
    ArrayAdapter adapter;
    ProgressBar progressBar;

    TextView emptyText;


    Button saveButton;
    private OnFragmentInteractionListener mListener;

    public ControlCompanyScopeTabFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_control_company_scope, container, false);

        formView = (RelativeLayout) rootView.findViewById(R.id.control_company_multichoice_relative_layout);
        companyListView = (ListView) rootView.findViewById(R.id.control_company_multichoice_list);
        progressBar = (ProgressBar) rootView.findViewById(R.id.control_company_multichoice_progress_bar);
        emptyText = (TextView) rootView.findViewById(R.id.control_company_multichoice_empty_message);

        loadProjectCompanyList();
        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_multiple_choice, projectCompanyCharSequenceList);

        companyListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        companyListView.setAdapter(adapter);

        companyListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (companyListView.getCheckedItemCount() > 0) {
                    SparseBooleanArray checked = companyListView.getCheckedItemPositions();
                    ArrayList<ParseObject> tempSelectedCompanyObjects = new ArrayList<>();
                    for (int i = 0; i < checked.size(); i++) {
                        int index = checked.keyAt(i);
                        if (checked.valueAt(i) == true)
                            tempSelectedCompanyObjects.add((ParseObject) projectCompanyObjectList.get(index));
                    }
                    selectedCompanyObjects = tempSelectedCompanyObjects;
                    mListener.saveSelectedCompaniesToActivityControl(selectedCompanyObjects);
                }
            }
        });

        saveButton = (Button) rootView.findViewById(R.id.control_company_save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (companyListView.getCheckedItemCount() > 0) {
                    SparseBooleanArray checked = companyListView.getCheckedItemPositions();
                    ArrayList<ParseObject> tempSelectedCompanyObjects = new ArrayList<>();
                    for (int i = 0; i < checked.size(); i++) {
                        int position = checked.keyAt(i);
                        if (checked.valueAt(i) == true)
                            tempSelectedCompanyObjects.add((ParseObject) projectCompanyObjectList.get(position));
                    }
                    selectedCompanyObjects = tempSelectedCompanyObjects;
                    mListener.saveSelectedCompaniesToActivityControl(selectedCompanyObjects);
                    mListener.onSaveButtonClicked();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle(getString(R.string.add_control_error_fragment_title))
                            .setMessage(R.string.add_control_scope_error_fragment_message)
                            .setNeutralButton(R.string.add_control_error_fragment_ok_button, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                }
                            });
                }
            }
        });

        return rootView;
    }

    private void loadProjectCompanyList() {
        showProgress(true);
        String currentProjectId = ParseUtils.getStringFromSession(getActivity(), ParseUtils.PREFS_CURRENT_PROJECT_ID);
        ParseQuery<ParseObject> getProjectCompanies = ParseQuery.getQuery(Project.TABLE_PROJECT);
        getProjectCompanies.include(Project.KEY_COMPANY_SCOPE_LIST);
        try {
            ParseObject project = getProjectCompanies.get(currentProjectId);
            projectCompanyObjectList = (ArrayList) project.getList(Project.KEY_COMPANY_SCOPE_LIST);
            projectCompanyCharSequenceList = castObjectListToCharSequenceList(projectCompanyObjectList);
            showProgress(false);
        } catch (ParseException e) {
            ParseUtils.handleParseException(getActivity(), e);
            showProgress(false);
        }
    }

    private ArrayList<CharSequence> castObjectListToCharSequenceList(ArrayList<ParseObject> companyObjectList) {
        if (companyObjectList != null) {
            ArrayList<CharSequence> resultList = new ArrayList<>();
            for (ParseObject obj : companyObjectList) {
                resultList.add(obj.getString(Company.KEY_COMPANY_NAME));
            }
            return resultList;
        } else
            return new ArrayList<>();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            if (projectCompanyObjectList == null || projectCompanyObjectList.isEmpty())
                emptyText.setVisibility(View.VISIBLE);
            else
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

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (!isVisibleToUser && mListener != null && !selectedCompanyObjects.isEmpty()){
            mListener.saveSelectedCompaniesToActivityControl(selectedCompanyObjects);
        }
    }

    public interface OnFragmentInteractionListener {
        void onSaveButtonClicked();
        void saveSelectedCompaniesToActivityControl(ArrayList<ParseObject> selectedCompanyItems);
    }

}
