package com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;

import com.vieira.rodrigo.itgcmanager.AddEditOrViewControlActivity;
import com.vieira.rodrigo.itgcmanager.R;
import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.models.Control;

import java.util.ArrayList;


public class ControlDetailsTabFragment extends Fragment {

    public static final String DETAILS_ARGS_NAME = "details_args_name";
    public static final String DETAILS_ARGS_DESCRIPTION = "details_args_description";
    public static final String DETAILS_ARGS_POPULATION = "details_args_population";
    public static final String DETAILS_ARGS_OWNER = "details_args_owner";
    public static final String DETAILS_ARGS_RISK = "details_args_risk";
    public static final String DETAILS_ARGS_TYPE = "details_args_type";
    public static final String DETAILS_ARGS_FREQUENCY = "details_args_frequency";
    public static final String DETAILS_ARGS_NATURE = "details_args_nature";

    public static final String RISK_LIST_CONTENT = "risk_list_content";
    public static final String TYPE_LIST_CONTENT = "type_list_content";
    public static final String FREQUENCY_LIST_CONTENT = "frequency_list_content";
    public static final String NATURE_LIST_CONTENT = "nature_list_content";

    private OnFragmentInteractionListener mListener;

    Control currentControl;

    EditText controlNameView;
    EditText controlDescriptionView;
    Spinner controlRiskClassSpinner;
    EditText controlPopulationView;
    EditText controlOwnerView;
    Spinner controlTypeSpinner;
    Spinner controlFrequencySpinner;
    Spinner controlNatureSpinner;
    Button saveButton;
    ScrollView formView;

    ArrayList<String> frequencyDescriptionList;
    ArrayList<String> natureDescriptionList;
    ArrayList<String> riskDescriptionList;
    ArrayList<String> typeDescriptionList;

    int mode;
    Bundle controlDetailsArgs;
    ProgressDialog progressDialog;

    public ControlDetailsTabFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        controlDetailsArgs = getArguments();
        mode = controlDetailsArgs.getInt(AddEditOrViewControlActivity.MODE_FLAG, AddEditOrViewControlActivity.ADD_MODE);
        progressDialog = new ProgressDialog(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_control_details, container, false);

        currentControl = new Control();

        controlNameView = (EditText) rootView.findViewById(R.id.add_control_name);
        controlDescriptionView = (EditText) rootView.findViewById(R.id.add_control_description);
        controlPopulationView = (EditText) rootView.findViewById(R.id.add_control_population);
        controlOwnerView = (EditText) rootView.findViewById(R.id.add_control_owner_view);

        controlRiskClassSpinner = (Spinner) rootView.findViewById(R.id.add_control_risk_spinner);
        controlTypeSpinner = (Spinner) rootView.findViewById(R.id.add_control_type_spinner);
        controlFrequencySpinner = (Spinner) rootView.findViewById(R.id.add_control_frequency_spinner);
        controlNatureSpinner = (Spinner) rootView.findViewById(R.id.add_control_nature_spinner);

        formView = (ScrollView) rootView.findViewById(R.id.add_control_form_view);

        saveButton = (Button) rootView.findViewById(R.id.add_control_save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFormValid()) {
                    mListener.saveNameToActivityControl(controlNameView.getText().toString());
                    mListener.saveOwnerToActivityControl(controlOwnerView.getText().toString());
                    mListener.savePopulationToActivityControl(controlPopulationView.getText().toString());
                    mListener.saveDescriptionToActivityControl(controlDescriptionView.getText().toString());
                    mListener.onSaveButtonClicked();
                }
            }
        });

        switch (mode) {
            case AddEditOrViewControlActivity.VIEW_MODE:
                loadSpinnersContentsWithActivityControlDetails();
                loadActivityCurrentControlDetails();
                lockViewsFromEdit();
                break;

            case AddEditOrViewControlActivity.EDIT_MODE:
                loadDetailListsContents();
                loadActivityCurrentControlDetails();
                break;

            case AddEditOrViewControlActivity.ADD_MODE:
                loadDetailListsContents();
                break;
        }

        return rootView;
    }

    private void loadSpinnersContentsWithActivityControlDetails() {
        ArrayList<String> activityControlRiskSelected = new ArrayList<>();
        activityControlRiskSelected.add(controlDetailsArgs.getString(DETAILS_ARGS_RISK));
        riskDescriptionList = activityControlRiskSelected;

        ArrayList<String> activityControlFrequencySelected = new ArrayList<>();
        activityControlFrequencySelected.add(controlDetailsArgs.getString(DETAILS_ARGS_FREQUENCY));
        frequencyDescriptionList = activityControlFrequencySelected;

        ArrayList<String> activityControlNatureSelected = new ArrayList<>();
        activityControlNatureSelected.add(controlDetailsArgs.getString(DETAILS_ARGS_NATURE));
        natureDescriptionList = activityControlNatureSelected;

        ArrayList<String> activityControlTypeSelected = new ArrayList<>();
        activityControlTypeSelected.add(controlDetailsArgs.getString(DETAILS_ARGS_TYPE));
        typeDescriptionList = activityControlTypeSelected;

        loadSpinnersContent();
    }

    private void loadDetailListsContents() {
        riskDescriptionList = controlDetailsArgs.getStringArrayList(RISK_LIST_CONTENT);
        typeDescriptionList = controlDetailsArgs.getStringArrayList(TYPE_LIST_CONTENT);
        natureDescriptionList = controlDetailsArgs.getStringArrayList(NATURE_LIST_CONTENT);
        frequencyDescriptionList = controlDetailsArgs.getStringArrayList(FREQUENCY_LIST_CONTENT);

        loadSpinnersContent();
    }

    private void loadActivityCurrentControlDetails() {
        controlNameView.setText(controlDetailsArgs.getString(DETAILS_ARGS_NAME));
        controlDescriptionView.setText(controlDetailsArgs.getString(DETAILS_ARGS_DESCRIPTION));
        controlPopulationView.setText(controlDetailsArgs.getString(DETAILS_ARGS_POPULATION));
        controlOwnerView.setText(controlDetailsArgs.getString(DETAILS_ARGS_OWNER));

        controlFrequencySpinner.setSelection(frequencyDescriptionList.indexOf((controlDetailsArgs.getString(DETAILS_ARGS_FREQUENCY))));
        controlTypeSpinner.setSelection(typeDescriptionList.indexOf((controlDetailsArgs.getString(DETAILS_ARGS_TYPE))));
        controlNatureSpinner.setSelection(natureDescriptionList.indexOf((controlDetailsArgs.getString(DETAILS_ARGS_NATURE))));
        controlRiskClassSpinner.setSelection(riskDescriptionList.indexOf((controlDetailsArgs.getString(DETAILS_ARGS_RISK))));
    }

    private void lockViewsFromEdit() {
        controlNameView.setEnabled(false);
        controlDescriptionView.setEnabled(false);
        controlPopulationView.setEnabled(false);
        controlOwnerView.setEnabled(false);

        controlFrequencySpinner.setEnabled(false);
        controlTypeSpinner.setEnabled(false);
        controlNatureSpinner.setEnabled(false);
        controlRiskClassSpinner.setEnabled(false);

        saveButton.setVisibility(View.GONE);
    }

    private void loadSpinnersContent() {
        ArrayAdapter<String> riskSpinnerAdapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_item, riskDescriptionList);
        riskSpinnerAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        controlRiskClassSpinner.setAdapter(riskSpinnerAdapter);
        controlRiskClassSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0)
                    mListener.saveRiskToActivityControl(riskDescriptionList.get(position - 1));
                else
                    mListener.saveRiskToActivityControl(null);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        ArrayAdapter<String> typeSpinnerAdapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_item, typeDescriptionList);
        typeSpinnerAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        controlTypeSpinner.setAdapter(typeSpinnerAdapter);
        controlTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0)
                    mListener.saveTypeToActivityControl(typeDescriptionList.get(position - 1));
                else
                    mListener.saveTypeToActivityControl(null);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        ArrayAdapter<String> frequencySpinnerAdapter = new ArrayAdapter<>(getActivity(),  R.layout.spinner_item, frequencyDescriptionList);
        frequencySpinnerAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);


        controlFrequencySpinner.setAdapter(frequencySpinnerAdapter);
        controlFrequencySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0)
                    mListener.saveFrequencyToActivityControl(frequencyDescriptionList.get(position - 1));
                else
                    mListener.saveFrequencyToActivityControl(null);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        ArrayAdapter<String> natureSpinnerAdapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_item, natureDescriptionList);
        natureSpinnerAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        controlNatureSpinner.setAdapter(natureSpinnerAdapter);
        controlNatureSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0)
                    mListener.saveNatureToActivityControl(natureDescriptionList.get(position - 1));
                else
                    mListener.saveNatureToActivityControl(null);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    private boolean isFormValid() {
        if (controlNameView.getText().toString().isEmpty()) {
            controlNameView.setError(getString(R.string.error_field_required));
            return false;
        }

        if (controlDescriptionView.getText().toString().isEmpty()) {
            controlDescriptionView.setError(getString(R.string.error_field_required));
            return false;
        }

        if (controlTypeSpinner.getSelectedItemPosition() == 0 || controlFrequencySpinner.getSelectedItemPosition() == 0
                || controlNatureSpinner.getSelectedItemPosition() == 0 || controlRiskClassSpinner.getSelectedItemPosition() == 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(getString(R.string.add_control_error_fragment_title))
                    .setMessage(R.string.add_control_details_error_fragment_message)
                    .setNeutralButton(R.string.add_control_error_fragment_ok_button, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    });

            builder.create().show();
            return false;
        }

        if (controlPopulationView.getText().toString().isEmpty()) {
            controlPopulationView.setError(getString(R.string.error_field_required));
            return false;
        }

        if (controlOwnerView.getText().toString().isEmpty()) {
            controlOwnerView.setError(getString(R.string.error_field_required));
            return false;
        }

        return true;
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
        if (!isVisibleToUser && mListener != null) {
            mListener.saveNameToActivityControl(controlNameView.getText().toString());
            mListener.saveOwnerToActivityControl(controlOwnerView.getText().toString());
            mListener.savePopulationToActivityControl(controlPopulationView.getText().toString());
            mListener.saveDescriptionToActivityControl(controlDescriptionView.getText().toString());
        }
    }

    public interface OnFragmentInteractionListener {
        void onSaveButtonClicked();
        void saveNameToActivityControl(String name);
        void saveDescriptionToActivityControl(String description);
        void savePopulationToActivityControl(String population);
        void saveOwnerToActivityControl(String owner);
        void saveRiskToActivityControl(String riskDescription);
        void saveTypeToActivityControl(String typeDescription);
        void saveFrequencyToActivityControl(String frequencyDescription);
        void saveNatureToActivityControl(String natureDescription);
    }

}
