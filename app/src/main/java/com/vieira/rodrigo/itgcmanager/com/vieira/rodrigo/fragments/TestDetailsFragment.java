package com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import com.vieira.rodrigo.itgcmanager.R;
import com.vieira.rodrigo.itgcmanager.TestActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class TestDetailsFragment extends Fragment {

    public static final String DETAILS_ARGS_NAME = "details_args_name";
    public static final String DETAILS_ARGS_DESCRIPTION = "details_args_description";
    public static final String DETAILS_ARGS_TYPE = "details_args_type";
    public static final String DETAILS_ARGS_POPULATION = "details_args_population";
    public static final String DETAILS_ARGS_SAMPLE = "details_args_sample";
    public static final String DETAILS_ARGS_STATUS = "details_args_status";
    public static final String DETAILS_ARGS_COVERAGE_DATE = "details_args_coverage_date";
    public static final String DETAILS_ARGS_CONTROL = "details_args_control";

    public static final String TYPE_LIST_CONTENT = "type_list_content";
    public static final String STATUS_LIST_CONTENT = "status_list_content";
    public static final String CONTROL_LIST_CONTENT = "control_list_content";

    private static OnFragmentInteractionListener mListener;

    public TestDetailsFragment() {
        // Required empty public constructor
    }

    Spinner testControlSpinner;
    EditText testNameView;
    EditText testDescriptionView;
    Spinner testTypeSpinner;
    EditText testPopulationView;
    EditText testSampleView;
    static EditText testCoverageDate;
    Button setCoverageDateButton;
    Spinner testStatusSpinner;
    Button saveButton;

    int mode;
    Bundle testDetailsArgs;
    ProgressDialog progressDialog;

    ArrayList<String> testStatusSpinnerContent;
    ArrayList<String> testTypeSpinnerContent;
    ArrayList<String> testControlSpinnerContent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        testDetailsArgs = getArguments();
        mode  = testDetailsArgs.getInt(TestActivity.MODE_FLAG, TestActivity.ADD_MODE);

        testStatusSpinnerContent = testDetailsArgs.getStringArrayList(STATUS_LIST_CONTENT);
        testTypeSpinnerContent = testDetailsArgs.getStringArrayList(TYPE_LIST_CONTENT);
        testControlSpinnerContent = testDetailsArgs.getStringArrayList(CONTROL_LIST_CONTENT);
        progressDialog = new ProgressDialog(getActivity());
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
        View rootView = inflater.inflate(R.layout.fragment_test_details, container, false);

        testNameView = (EditText) rootView.findViewById(R.id.test_activity_details_name);
        testDescriptionView = (EditText) rootView.findViewById(R.id.test_activity_details_description);
        testPopulationView = (EditText) rootView.findViewById(R.id.test_activity_details_population);
        testSampleView = (EditText) rootView.findViewById(R.id.test_activity_details_sample);
        setCoverageDateButton = (Button) rootView.findViewById(R.id.test_activity_details_set_date_button);
        testCoverageDate = (EditText) rootView.findViewById(R.id.test_activity_details_coverage_date);

        setCoverageDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePickerFragment = new DatePickerDialogFragment();
                datePickerFragment.show(getActivity().getSupportFragmentManager(), "datePicker");

            }
        });

        testControlSpinner = (Spinner) rootView.findViewById(R.id.test_activity_details_control_spinner);
        testTypeSpinner = (Spinner) rootView.findViewById(R.id.test_activity_details_type_spinner);
        testStatusSpinner = (Spinner) rootView.findViewById(R.id.test_activity_details_status_spinner);

        saveButton = (Button) rootView.findViewById(R.id.test_activity_details_save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFormValid()) {
                    mListener.saveTestName(testNameView.getText().toString());
                    mListener.saveTestDescription(testDescriptionView.getText().toString());
                    mListener.saveTestPopulation(testPopulationView.getText().toString());
                    mListener.saveTestSample(testSampleView.getText().toString());
                    mListener.onSaveButtonClicked();
                }
            }
        });

        switch (mode) {
            case TestActivity.ADD_MODE:
                loadSpinnerContents();
                break;

            case TestActivity.EDIT_MODE:
                loadSpinnerContents();
                loadTestFromActivity();
                break;

            case TestActivity.VIEW_MODE:
                loadSpinnerContents();
                loadTestFromActivity();
                disableViews();
                break;
        }

        return rootView;
    }

    private void disableViews() {
        testNameView.setEnabled(false);
        testDescriptionView.setEnabled(false);
        testPopulationView.setEnabled(false);
        testSampleView.setEnabled(false);
        testCoverageDate.setEnabled(false);
        setCoverageDateButton.setVisibility(View.GONE);
        saveButton.setVisibility(View.INVISIBLE);
        testTypeSpinner.setEnabled(false);
        testStatusSpinner.setEnabled(false);
        testControlSpinner.setEnabled(false);

    }

    private void loadTestFromActivity() {
        testNameView.setText(testDetailsArgs.getString(DETAILS_ARGS_NAME));
        testDescriptionView.setText(testDetailsArgs.getString(DETAILS_ARGS_DESCRIPTION));
        testPopulationView.setText(testDetailsArgs.getString(DETAILS_ARGS_POPULATION));
        testSampleView.setText(testDetailsArgs.getString(DETAILS_ARGS_SAMPLE));

        GregorianCalendar coverageDate = new GregorianCalendar();
        coverageDate.setTimeInMillis(testDetailsArgs.getLong(DETAILS_ARGS_COVERAGE_DATE));
        String coverageDateString = coverageDate.get(GregorianCalendar.YEAR) + "/" + coverageDate.get(GregorianCalendar.MONTH) + "/" + coverageDate.get(GregorianCalendar.DAY_OF_MONTH);
        testCoverageDate.setText(coverageDateString);

        testTypeSpinner.setSelection(testTypeSpinnerContent.indexOf(testDetailsArgs.getString(DETAILS_ARGS_TYPE)));
        testStatusSpinner.setSelection(testStatusSpinnerContent.indexOf(testDetailsArgs.getString(DETAILS_ARGS_STATUS)));
        testControlSpinner.setSelection(testControlSpinnerContent.indexOf(testDetailsArgs.getString(DETAILS_ARGS_CONTROL)));
    }

    private void loadSpinnerContents() {
        ArrayAdapter<String> controlSpinnerAdapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_item, testControlSpinnerContent);
        controlSpinnerAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        testControlSpinner.setAdapter(controlSpinnerAdapter);
        testControlSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0)
                    mListener.saveTestControl(testControlSpinnerContent.get(position));
                else
                    mListener.saveTestControl(null);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayAdapter<String> typeSpinnerAdapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_item, testTypeSpinnerContent);
        typeSpinnerAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        testTypeSpinner.setAdapter(typeSpinnerAdapter);
        testTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0)
                    mListener.saveTestType(testTypeSpinnerContent.get(position));
                else
                    mListener.saveTestType(null);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayAdapter<String> statusSpinnerAdapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_item, testStatusSpinnerContent);
        statusSpinnerAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        testStatusSpinner.setAdapter(statusSpinnerAdapter);
        testStatusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0)
                    mListener.saveTestStatus(testStatusSpinnerContent.get(position));
                else
                    mListener.saveTestStatus(null);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private boolean isFormValid() {
        if (testNameView.getText().toString().isEmpty()){
            testNameView.setError(getString(R.string.error_field_required));
            return false;
        }

        if (testDescriptionView.getText().toString().isEmpty()){
            testDescriptionView.setError(getString(R.string.error_field_required));
            return false;
        }

        if (testPopulationView.getText().toString().isEmpty()){
            testPopulationView.setError(getString(R.string.error_field_required));
            return false;
        }

        if (testSampleView.getText().toString().isEmpty()){
            testSampleView.setError(getString(R.string.error_field_required));
            return false;
        }

        if (testStatusSpinner.getSelectedItemPosition() == 0 || testTypeSpinner.getSelectedItemPosition() == 0
                || testControlSpinner.getSelectedItemPosition() == 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(R.string.test_activity_details_error_fields_required)
                    .setNeutralButton(R.string.add_control_error_fragment_ok_button, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });
            builder.create().show();
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
            mListener.saveTestName(testNameView.getText().toString());
            mListener.saveTestDescription(testDescriptionView.getText().toString());
            mListener.saveTestPopulation(testPopulationView.getText().toString());
            mListener.saveTestSample(testSampleView.getText().toString());
        }
    }

    public interface OnFragmentInteractionListener {
        void saveTestName(String name);
        void saveTestDescription(String description);
        void saveTestType(String type);
        void saveTestPopulation(String population);
        void saveTestSample(String sample);
        void saveTestControl(String control);
        void saveCoverageDate(int year, int monthOfYear, int dayOfMonth);
        void saveTestStatus(String status);
        void onSaveButtonClicked();
    }

    public static class DatePickerDialogFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            testCoverageDate.setText(year + "/" + (monthOfYear+1) + "/" + dayOfMonth);
            mListener.saveCoverageDate(year, monthOfYear+1, dayOfMonth);
        }

        public DatePickerDialogFragment() {
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            return new DatePickerDialog(getActivity(), this, year, month, day);
        }
    }

}
