package com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.fragments;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.parse.ParseObject;
import com.vieira.rodrigo.itgcmanager.R;
import com.vieira.rodrigo.itgcmanager.TestActivity;
import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.models.TestException;

public class TestExceptionFragment extends Fragment {

    public static final String EXCEPTION_ARGS_TITLE = "exception_args_title";
    public static final String EXCEPTION_ARGS_DESCRIPTION = "exception_args_description";
    public static final String EXCEPTION_ARGS_NUMBER_OF_EXCEPTIONS = "exception_args_number_of_exceptions";
    public static final String EXCEPTION_ARGS_IS_REMEDIATED = "exception_args_is_remediated";
    public static final String EXCEPTION_ARGS_REMEDIATION_RACIONALE = "exception_args_remediation_rationale";
    public static final String EXCEPTION_ARGS_IS_SIGNIFICANT = "exception_args_is_significant";
    public static final String EXCEPTION_ARGS_HAS_EXCEPTION = "exception_args_has_exception";

    private static OnFragmentInteractionListener mListener;

    public TestExceptionFragment() {
        // Required empty public constructor
    }

    RadioGroup exceptionFoundRadioGroup;
    RadioButton exceptionFoundButtonYes;
    RadioButton exceptionFoundButtonNo;

    RelativeLayout exceptionFoundDetailsRelativeLayout;
    EditText exceptionTitleView;
    EditText exceptionDescriptionView;
    EditText exceptionNumberOfExceptionsView;
    EditText exceptionRemediationRationaleView;
    RadioGroup exceptionIsRemediatedRadioGroup;
    RadioButton exceptionIsRemediatedButtonYes;
    RadioButton exceptionIsRemediatedButtonNo;
    RadioGroup exceptionIsSignificantRadioGroup;
    RadioButton exceptionIsSignificantButtonYes;
    RadioButton exceptionIsSignificantButtonNo;
    Button saveButton;

    int mode;
    Bundle testExceptionArgs;

    boolean exceptionFound;
    boolean exceptionIsRemediated;
    boolean exceptionIsSignificant;

    ParseObject testExceptionObject;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        testExceptionArgs = getArguments();
        mode = testExceptionArgs.getInt(TestActivity.MODE_FLAG, TestActivity.ADD_MODE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_test_exception, container, false);

        exceptionFoundRadioGroup = (RadioGroup) rootView.findViewById(R.id.test_activity_exception_found_radio_group);
        exceptionFoundButtonYes = (RadioButton) rootView.findViewById(R.id.test_activity_exception_found_radio_button_yes);
        exceptionFoundButtonNo = (RadioButton) rootView.findViewById(R.id.test_activity_exception_found_radio_button_no);

        exceptionFoundDetailsRelativeLayout = (RelativeLayout) rootView.findViewById(R.id.test_activity_exception_found_details_relative_layout);

        exceptionTitleView = (EditText) rootView.findViewById(R.id.test_activity_exception_title);
        exceptionDescriptionView = (EditText) rootView.findViewById(R.id.test_activity_exception_description);
        exceptionNumberOfExceptionsView = (EditText) rootView.findViewById(R.id.test_activity_exception_number_of_exceptions);
        exceptionRemediationRationaleView = (EditText) rootView.findViewById(R.id.test_activity_exception_remediation_rationale);

        exceptionIsRemediatedRadioGroup = (RadioGroup) rootView.findViewById(R.id.test_activity_exception_is_remediated_radio_group);
        exceptionIsRemediatedButtonYes = (RadioButton) rootView.findViewById(R.id.test_activity_exception_is_remediated_radio_yes);
        exceptionIsRemediatedButtonNo = (RadioButton) rootView.findViewById(R.id.test_activity_exception_is_remediated_radio_no);

        exceptionIsSignificantRadioGroup = (RadioGroup) rootView.findViewById(R.id.test_activity_exception_is_significant_radio_group);
        exceptionIsSignificantButtonYes = (RadioButton) rootView.findViewById(R.id.test_activity_exception_is_significant_radio_yes);
        exceptionIsSignificantButtonNo = (RadioButton) rootView.findViewById(R.id.test_activity_exception_is_significant_radio_no);

        exceptionFoundRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.test_activity_exception_found_radio_button_yes) {
                    exceptionFoundDetailsRelativeLayout.setVisibility(View.VISIBLE);
                    exceptionFound = true;
                } else if (checkedId == R.id.test_activity_exception_found_radio_button_no) {
                    exceptionFoundDetailsRelativeLayout.setVisibility(View.GONE);
                    exceptionFound = false;
                }
            }
        });

        exceptionIsRemediatedRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.test_activity_exception_is_remediated_radio_yes)
                    exceptionIsRemediated = true;
                else if (checkedId == R.id.test_activity_exception_is_remediated_radio_no)
                    exceptionIsRemediated = false;
            }
        });

        exceptionIsSignificantRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.test_activity_exception_is_significant_radio_yes)
                    exceptionIsSignificant = true;
                else if (checkedId == R.id.test_activity_exception_is_significant_radio_no)
                    exceptionIsSignificant = false;

            }
        });

        saveButton = (Button) rootView.findViewById(R.id.test_activity_exception_save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (exceptionFound) {
                    testExceptionObject = new ParseObject(TestException.TABLE_TEST_EXCEPTION);
                    testExceptionObject.put(TestException.KEY_TEST_EXCEPTION_TITLE, exceptionTitleView.getText().toString());
                    testExceptionObject.put(TestException.KEY_TEST_EXCEPTION_DESCRIPTION, exceptionDescriptionView.getText().toString());
                    testExceptionObject.put(TestException.KEY_TEST_EXCEPTION_NUMBER_OF_EXCEPTIONS, Integer.parseInt(exceptionNumberOfExceptionsView.getText().toString()));
                    testExceptionObject.put(TestException.KEY_TEST_EXCEPTION_REMEDIATION_RATIONALE, exceptionRemediationRationaleView.getText().toString());
                    testExceptionObject.put(TestException.KEY_TEST_EXCEPTION_IS_REMEDIATED, exceptionIsRemediated);
                    testExceptionObject.put(TestException.KEY_TEST_EXCEPTION_IS_SIGNIFICANT, exceptionIsSignificant);

                    mListener.saveTestException(testExceptionObject);
                } else {
                    mListener.saveTestException(null);
                }

                mListener.onSaveButtonClicked();
            }
        });

        if (mode != TestActivity.ADD_MODE) {
            loadExceptionTestFromActivity();
        }

        return rootView;
    }

    private void loadExceptionTestFromActivity() {
        exceptionFound = testExceptionArgs.getBoolean(EXCEPTION_ARGS_HAS_EXCEPTION);
        if (exceptionFound) {
            exceptionFoundButtonYes.setChecked(true);
            exceptionFoundDetailsRelativeLayout.setVisibility(View.VISIBLE);

            exceptionTitleView.setText(testExceptionArgs.getString(EXCEPTION_ARGS_TITLE));
            exceptionDescriptionView.setText(testExceptionArgs.getString(EXCEPTION_ARGS_DESCRIPTION));
            exceptionNumberOfExceptionsView.setText(testExceptionArgs.getString(EXCEPTION_ARGS_NUMBER_OF_EXCEPTIONS));
            exceptionRemediationRationaleView.setText(testExceptionArgs.getString(EXCEPTION_ARGS_REMEDIATION_RACIONALE));

            exceptionIsRemediated = testExceptionArgs.getBoolean(EXCEPTION_ARGS_IS_REMEDIATED);
            if (exceptionIsRemediated) {
                exceptionIsRemediatedButtonYes.setChecked(true);
            } else
                exceptionIsRemediatedButtonNo.setChecked(true);

            exceptionIsSignificant = testExceptionArgs.getBoolean(EXCEPTION_ARGS_IS_SIGNIFICANT);
            if (exceptionIsSignificant)
                exceptionIsSignificantButtonYes.setChecked(true);
            else
                exceptionIsSignificantButtonNo.setChecked(true);

        } else {
            exceptionFoundButtonNo.setChecked(true);
            exceptionFoundDetailsRelativeLayout.setVisibility(View.GONE);
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
        if (!isVisibleToUser && mListener != null) {
            mListener.saveTestException(testExceptionObject);
        }
    }

    public interface OnFragmentInteractionListener {
        void saveTestException(ParseObject testException);
        void onSaveButtonClicked();
    }

}
