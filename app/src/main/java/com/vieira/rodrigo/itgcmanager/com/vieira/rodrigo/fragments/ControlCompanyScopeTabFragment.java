package com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vieira.rodrigo.itgcmanager.ControlActivity;
import com.vieira.rodrigo.itgcmanager.R;

import java.util.ArrayList;

public class ControlCompanyScopeTabFragment extends Fragment {

    public static final String COMPANY_SCOPE_ARGS_SELECTED_NAME_LIST = "company_scope_args_selected_name_list";
    public static final String COMPANY_LIST_CONTENT = "company_list_content";

    ArrayList<String> companyNameList;
    private ArrayList selectedCompanyNames = new ArrayList();

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

    int mode;
    Bundle companyArgs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        companyArgs = getArguments();
        mode = companyArgs.getInt(ControlActivity.MODE_FLAG, ControlActivity.ADD_MODE);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().finish();
        }
        return true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_control_company_scope, container, false);

        formView = (RelativeLayout) rootView.findViewById(R.id.control_company_multichoice_relative_layout);
        progressBar = (ProgressBar) rootView.findViewById(R.id.control_company_multichoice_progress_bar);
        emptyText = (TextView) rootView.findViewById(R.id.control_company_multichoice_empty_message);

        companyListView = (ListView) rootView.findViewById(R.id.control_company_multichoice_list);
        companyListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        companyListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (companyListView.getCheckedItemCount() > 0) {
                    SparseBooleanArray checked = companyListView.getCheckedItemPositions();
                    ArrayList<String> tempSelectedCompanyNames = new ArrayList<>();
                    for (int i = 0; i < checked.size(); i++) {
                        int index = checked.keyAt(i);
                        if (checked.valueAt(i) == true)
                            tempSelectedCompanyNames.add(companyNameList.get(index));
                    }
                    selectedCompanyNames = tempSelectedCompanyNames;
                    mListener.saveSelectedCompaniesToActivityControl(selectedCompanyNames);
                }
            }
        });

        saveButton = (Button) rootView.findViewById(R.id.control_company_save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (companyListView.getCheckedItemCount() > 0) {
                    SparseBooleanArray checked = companyListView.getCheckedItemPositions();
                    ArrayList<String> tempSelectedCompanyNames = new ArrayList<>();
                    for (int i = 0; i < checked.size(); i++) {
                        int position = checked.keyAt(i);
                        if (checked.valueAt(i) == true)
                            tempSelectedCompanyNames.add(companyNameList.get(position));
                    }
                    selectedCompanyNames = tempSelectedCompanyNames;
                    mListener.saveSelectedCompaniesToActivityControl(selectedCompanyNames);
                    mListener.onSaveButtonClicked();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle(getString(R.string.add_control_error_fragment_title))
                            .setMessage(R.string.add_control_scope_error_fragment_message)
                            .setNeutralButton(R.string.add_control_error_fragment_ok_button, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                }
                            });
                    builder.create().show();
                }
            }
        });

        switch (mode) {
            case ControlActivity.VIEW_MODE:
                ArrayList<String> selectedCompanyNameList = companyArgs.getStringArrayList(COMPANY_SCOPE_ARGS_SELECTED_NAME_LIST);
                adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_multiple_choice, selectedCompanyNameList);
                companyListView.setAdapter(adapter);
                for (int i = 0; i < selectedCompanyNameList.size(); i++) {
                    companyListView.setItemChecked(i, true);
                }
                companyListView.setEnabled(false);
                saveButton.setVisibility(View.GONE);
                break;

            case ControlActivity.EDIT_MODE:
                companyNameList = companyArgs.getStringArrayList(COMPANY_LIST_CONTENT);
                adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_multiple_choice, companyNameList);
                companyListView.setAdapter(adapter);
                loadActivityCurrentControlCompanyScope();
                break;

            case ControlActivity.ADD_MODE:
                companyNameList = companyArgs.getStringArrayList(COMPANY_LIST_CONTENT);
                adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_multiple_choice, companyNameList);
                break;
        }

        if (companyNameList.isEmpty())
            emptyText.setVisibility(View.VISIBLE);
        else
            emptyText.setVisibility(View.GONE);

        return rootView;
    }

    private void loadActivityCurrentControlCompanyScope() {
        ArrayList<String> selectedCompanyNameList = companyArgs.getStringArrayList(COMPANY_SCOPE_ARGS_SELECTED_NAME_LIST);
        if (selectedCompanyNameList != null) {
            for (String companyName : selectedCompanyNameList) {
                int positionSelected = companyNameList.indexOf(companyName);
                companyListView.setItemChecked(positionSelected, true);
            }
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
        if (!isVisibleToUser && mListener != null && !selectedCompanyNames.isEmpty()){
            mListener.saveSelectedCompaniesToActivityControl(selectedCompanyNames);
        }
    }

    public interface OnFragmentInteractionListener {
        void onSaveButtonClicked();
        void saveSelectedCompaniesToActivityControl(ArrayList<String> selectedCompanyItems);
    }

}
