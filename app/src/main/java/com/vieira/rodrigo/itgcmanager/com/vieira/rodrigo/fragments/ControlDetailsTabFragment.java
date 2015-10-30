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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.vieira.rodrigo.itgcmanager.R;
import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.Utils.ParseUtils;
import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.models.Control;

import java.util.ArrayList;


public class ControlDetailsTabFragment extends Fragment {

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
    Button continueButton;
    ProgressBar progressBar;
    ScrollView formView;

    ArrayList<CharSequence> frequencyDescriptionList;
    ArrayList<CharSequence> natureDescriptionList;
    ArrayList<CharSequence> riskDescriptionList;
    ArrayList<CharSequence> typeDescriptionList;

    ArrayList<ParseObject> frequencyDescriptionObjectList;
    ArrayList<ParseObject> natureDescriptionObjectList;
    ArrayList<ParseObject> riskDescriptionObjectList;
    ArrayList<ParseObject> typeDescriptionObjectList;

    public ControlDetailsTabFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        progressBar = (ProgressBar) rootView.findViewById(R.id.add_control_progress_bar);
        formView = (ScrollView) rootView.findViewById(R.id.add_control_form_view);

        loadSpinnersContent();

        continueButton = (Button) rootView.findViewById(R.id.add_control_save_button);
        continueButton.setOnClickListener(new View.OnClickListener() {
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

        return rootView;
    }

    private void loadSpinnerDescriptionContents() {
        showProgress(true);
        ParseQuery<ParseObject> getFrequencyDescription = ParseQuery.getQuery(Control.TABLE_CONTROL_FREQUENCY);
        ParseQuery<ParseObject> getNatureDescription = ParseQuery.getQuery(Control.TABLE_CONTROL_NATURE);
        ParseQuery<ParseObject> getRiskDescription = ParseQuery.getQuery(Control.TABLE_CONTROL_RISK);
        ParseQuery<ParseObject> getTypeDescription = ParseQuery.getQuery(Control.TABLE_CONTROL_TYPE);
        try {
            frequencyDescriptionObjectList = (ArrayList<ParseObject>) getFrequencyDescription.find();
            frequencyDescriptionList = castParseObjectListToCharSequenceList(frequencyDescriptionObjectList);
            frequencyDescriptionList.add(0, getString(R.string.add_control_details_frequency_label));

            natureDescriptionObjectList = (ArrayList<ParseObject>) getNatureDescription.find();
            natureDescriptionList = castParseObjectListToCharSequenceList(natureDescriptionObjectList);
            natureDescriptionList.add(0, getString(R.string.add_control_details_nature_label));

            riskDescriptionObjectList = (ArrayList<ParseObject>) getRiskDescription.find();
            riskDescriptionList = castParseObjectListToCharSequenceList(riskDescriptionObjectList);
            riskDescriptionList.add(0, getString(R.string.add_control_details_risk_classification_label));

            typeDescriptionObjectList = (ArrayList<ParseObject>) getTypeDescription.find();
            typeDescriptionList = castParseObjectListToCharSequenceList(typeDescriptionObjectList);
            typeDescriptionList.add(0, getString(R.string.add_control_details_type_label));

            showProgress(false);
        } catch (ParseException e) {
            ParseUtils.handleParseException(getActivity(), e);
            showProgress(false);
        }

    }

    private ArrayList<CharSequence> castParseObjectListToCharSequenceList(ArrayList<ParseObject> parseObjectList) {
        ArrayList<CharSequence> output = new ArrayList<>();
        for (ParseObject obj : parseObjectList){
            CharSequence desc = obj.getString(Control.KEY_CONTROL_GENERIC_DESCRIPTION);
            output.add(desc);
        }
        return output;
    }

    private void loadSpinnersContent() {
        loadSpinnerDescriptionContents();

        ArrayAdapter<CharSequence> riskSpinnerAdapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_item, riskDescriptionList);
        riskSpinnerAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        controlRiskClassSpinner.setAdapter(riskSpinnerAdapter);
        controlRiskClassSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0)
                    mListener.saveRiskToActivityControl(riskDescriptionObjectList.get(position - 1));
                else
                    mListener.saveRiskToActivityControl(null);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        ArrayAdapter<CharSequence> typeSpinnerAdapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_item, typeDescriptionList);
        typeSpinnerAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        controlTypeSpinner.setAdapter(typeSpinnerAdapter);
        controlTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0)
                    mListener.saveTypeToActivityControl(typeDescriptionObjectList.get(position - 1));
                else
                    mListener.saveTypeToActivityControl(null);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        ArrayAdapter<CharSequence> frequencySpinnerAdapter = new ArrayAdapter<>(getActivity(),  R.layout.spinner_item, frequencyDescriptionList);
        frequencySpinnerAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);


        controlFrequencySpinner.setAdapter(frequencySpinnerAdapter);
        controlFrequencySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0)
                    mListener.saveFrequencyToActivityControl(frequencyDescriptionObjectList.get(position - 1));
                else
                    mListener.saveFrequencyToActivityControl(null);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        ArrayAdapter<CharSequence> natureSpinnerAdapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_item, natureDescriptionList);
        natureSpinnerAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        controlNatureSpinner.setAdapter(natureSpinnerAdapter);
        controlNatureSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0)
                    mListener.saveNatureToActivityControl(natureDescriptionObjectList.get(position - 1));
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

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
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
        void saveRiskToActivityControl(ParseObject risk);
        void saveTypeToActivityControl(ParseObject type);
        void saveFrequencyToActivityControl(ParseObject frequency);
        void saveNatureToActivityControl(ParseObject nature);
    }

}
