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

import com.vieira.rodrigo.itgcmanager.TestActivity;
import com.vieira.rodrigo.itgcmanager.R;

import java.util.ArrayList;


public class TestSystemScopeFragment extends Fragment {

    public static final String SYSTEM_SCOPE_ARGS_SELECTED_NAME_LIST = "system_scope_args_selected_name_list";
    public static final String SYSTEM_LIST_CONTENT = "system_list_content";

    ArrayList<String> systemNameList;
    private ArrayList<String> selectedSystemNames = new ArrayList<>();

    ListView systemListView;
    RelativeLayout formView;
    ArrayAdapter adapter;
    ProgressBar progressBar;

    TextView emptyText;
    int mode;
    Bundle systemScopeArgs;

    Button saveButton;
    private OnFragmentInteractionListener mListener;

    public TestSystemScopeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        systemScopeArgs = getArguments();
        mode = systemScopeArgs.getInt(TestActivity.MODE_FLAG, TestActivity.ADD_MODE);
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
        View rootView = inflater.inflate(R.layout.fragment_test_system_scope, container, false);

        formView = (RelativeLayout) rootView.findViewById(R.id.test_system_multichoice_relative_layout);
        progressBar = (ProgressBar) rootView.findViewById(R.id.test_system_multichoice_progress_bar);
        emptyText = (TextView) rootView.findViewById(R.id.test_system_multichoice_empty_message);

        systemListView = (ListView) rootView.findViewById(R.id.test_system_multichoice_list);
        systemListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        systemListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (systemListView.getCheckedItemCount() > 0) {
                    SparseBooleanArray checked = systemListView.getCheckedItemPositions();
                    ArrayList<String> tempSelectedSystemNames = new ArrayList<>();
                    for (int i = 0; i < checked.size(); i++) {
                        int index = checked.keyAt(i);
                        if (checked.valueAt(i))
                            tempSelectedSystemNames.add(systemNameList.get(index));
                    }
                    selectedSystemNames = tempSelectedSystemNames;
                    mListener.saveSelectedSystemsToActivityTest(selectedSystemNames);
                }
            }
        });

        saveButton = (Button) rootView.findViewById(R.id.test_system_save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (systemListView.getCheckedItemCount() > 0) {
                    SparseBooleanArray checked = systemListView.getCheckedItemPositions();
                    ArrayList<String> tempSelectedSystemObjects = new ArrayList<>();
                    for (int i = 0; i < checked.size(); i++) {
                        int position = checked.keyAt(i);
                        if (checked.valueAt(i))
                            tempSelectedSystemObjects.add(systemNameList.get(position));
                    }
                    selectedSystemNames = tempSelectedSystemObjects;
                    mListener.saveSelectedSystemsToActivityTest(selectedSystemNames);
                    mListener.onSaveButtonClicked();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle(getString(R.string.add_control_error_fragment_title))
                            .setMessage(R.string.add_control_system_scope_error_fragment_message)
                            .setNeutralButton(R.string.add_control_error_fragment_ok_button, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                }
                            });
                    builder.create().show();
                }
            }
        });

        switch (mode) {
            case TestActivity.VIEW_MODE:
                ArrayList<String> selectedSystemNameList = systemScopeArgs.getStringArrayList(SYSTEM_SCOPE_ARGS_SELECTED_NAME_LIST);
                adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_multiple_choice, selectedSystemNameList);
                systemListView.setAdapter(adapter);
                for (int i  = 0; i < selectedSystemNameList.size(); i++)
                    systemListView.setItemChecked(i, true);
                systemListView.setEnabled(false);
                saveButton.setVisibility(View.GONE);
                break;

            case TestActivity.EDIT_MODE:
                loadSystemListContent();
                loadActivityCurrentTestSystemScope();
                break;

            case TestActivity.ADD_MODE:
                loadSystemListContent();
                break;
        }

        return rootView;
    }

    private void loadSystemListContent() {
        systemNameList = systemScopeArgs.getStringArrayList(SYSTEM_LIST_CONTENT);
        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_multiple_choice, systemNameList);
        systemListView.setAdapter(adapter);
        if (systemNameList.isEmpty())
            emptyText.setVisibility(View.VISIBLE);
        else
            emptyText.setVisibility(View.GONE);
    }

    private void loadActivityCurrentTestSystemScope() {
        ArrayList<String> selectedSystemNameList = systemScopeArgs.getStringArrayList(SYSTEM_SCOPE_ARGS_SELECTED_NAME_LIST);
        if (selectedSystemNameList != null) {
            for (String systemName : selectedSystemNameList) {
                int positionSelected = systemNameList.indexOf(systemName);
                systemListView.setItemChecked(positionSelected, true);
            }
        } else {
            adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_multiple_choice, new ArrayList<String>());
            systemListView.setAdapter(adapter);
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
        if (!isVisibleToUser && mListener != null && !selectedSystemNames.isEmpty()) {
            mListener.saveSelectedSystemsToActivityTest(selectedSystemNames);
        }
    }

    public interface OnFragmentInteractionListener {

        void onSaveButtonClicked();
        void saveSelectedSystemsToActivityTest(ArrayList<String> selectedSystemItems);
    }

}
