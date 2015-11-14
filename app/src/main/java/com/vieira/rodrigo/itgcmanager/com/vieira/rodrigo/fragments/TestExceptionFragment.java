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
    public static final String EXCEPTION_ARGS_OBJECT_ID = "exception_args_object_id";

    private static OnFragmentInteractionListener mListener;

    public TestExceptionFragment() {
        // Required empty public constructor
    }

    RadioGroup testHasExceptionRadioGroup;
    RadioButton testHasExceptionButtonYes;
    RadioButton testHasExceptionButtonNo;

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

    boolean testHasException;
    boolean exceptionIsRemediated;
    boolean exceptionIsSignificant;

    TestException testException;
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

        testHasExceptionRadioGroup = (RadioGroup) rootView.findViewById(R.id.test_activity_exception_test_has_exception_radio_group);
        testHasExceptionButtonYes = (RadioButton) rootView.findViewById(R.id.test_activity_exception_test_has_exception_radio_button_yes);
        testHasExceptionButtonNo = (RadioButton) rootView.findViewById(R.id.test_activity_exception_test_has_exception_radio_button_no);

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

        testHasExceptionRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.test_activity_exception_test_has_exception_radio_button_yes) {
                    exceptionFoundDetailsRelativeLayout.setVisibility(View.VISIBLE);
                    testHasException = true;
                } else if (checkedId == R.id.test_activity_exception_test_has_exception_radio_button_no) {
                    exceptionFoundDetailsRelativeLayout.setVisibility(View.GONE);
                    testHasException = false;
                }
                mListener.saveTestHasException(testHasException);
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
                if (testHasException) {
                    populateTestException();
                    mListener.saveTestException(testException);
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
        testHasException = testExceptionArgs.getBoolean(EXCEPTION_ARGS_HAS_EXCEPTION);
        if (testHasException) {
            testHasExceptionButtonYes.setChecked(true);
            exceptionFoundDetailsRelativeLayout.setVisibility(View.VISIBLE);

            exceptionTitleView.setText(testExceptionArgs.getString(EXCEPTION_ARGS_TITLE));
            exceptionDescriptionView.setText(testExceptionArgs.getString(EXCEPTION_ARGS_DESCRIPTION));
            int i = testExceptionArgs.getInt(EXCEPTION_ARGS_NUMBER_OF_EXCEPTIONS);
            exceptionNumberOfExceptionsView.setText(String.valueOf(i));
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
            testHasExceptionButtonNo.setChecked(true);
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

    private void populateTestException() {
        if (testException == null)
            testException = new TestException();

        testException.setTitle(exceptionTitleView.getText().toString());
        testException.setDescription(exceptionDescriptionView.getText().toString());
        testException.setNumberOfExceptions(Integer.parseInt(exceptionNumberOfExceptionsView.getText().toString()));
        testException.setRemediationRationale(exceptionRemediationRationaleView.getText().toString());

        testException.setIsSignificant(exceptionIsSignificant);
        testException.setIsRemediated(exceptionIsRemediated);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (!isVisibleToUser && mListener != null) {
            if (testHasException) {
                populateTestException();
                mListener.saveTestException(testException);
            } else
                mListener.saveTestException(null);
        }
    }

    public interface OnFragmentInteractionListener {
        void saveTestException(TestException testException);
        void saveTestHasException(boolean hasException);
        void onSaveButtonClicked();
    }

}
