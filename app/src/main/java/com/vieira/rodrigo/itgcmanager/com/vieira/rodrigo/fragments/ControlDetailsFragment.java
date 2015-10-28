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


public class ControlDetailsFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    Control currentControl;

    EditText controlNameView;
    EditText controlDescriptionView;
    Spinner controlRiskClassSpinner;
    EditText controlPopulationView;
    EditText controlOwner;
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

    public ControlDetailsFragment() {
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
        controlRiskClassSpinner = (Spinner) rootView.findViewById(R.id.add_control_risk_spinner);
        controlPopulationView = (EditText) rootView.findViewById(R.id.add_control_population);
        controlOwner = (EditText) rootView.findViewById(R.id.add_control_owner_view);
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
                    populateNewControl();
                    mListener.syncControlWithActivity(currentControl);
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
                    currentControl.setRiskClassificationObject(riskDescriptionObjectList.get(position-1));
                else
                    currentControl.setRiskClassificationObject(null);
                mListener.syncControlWithActivity(currentControl);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                currentControl.setRiskClassificationObject(null);
                mListener.syncControlWithActivity(currentControl);
            }
        });


        ArrayAdapter<CharSequence> typeSpinnerAdapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_item, typeDescriptionList);
        typeSpinnerAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        controlTypeSpinner.setAdapter(typeSpinnerAdapter);
        controlTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0)
                    currentControl.setTypeObject(typeDescriptionObjectList.get(position-1));
                else
                    currentControl.setTypeObject(null);
                mListener.syncControlWithActivity(currentControl);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                currentControl.setTypeObject(null);
                mListener.syncControlWithActivity(currentControl);
            }
        });

        ArrayAdapter<CharSequence> frequencySpinnerAdapter = new ArrayAdapter<>(getActivity(),  R.layout.spinner_item, frequencyDescriptionList);
        frequencySpinnerAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);


        controlFrequencySpinner.setAdapter(frequencySpinnerAdapter);
        controlFrequencySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0)
                    currentControl.setFrequencyObject(frequencyDescriptionObjectList.get(position-1));
                else
                    currentControl.setFrequencyObject(null);
                mListener.syncControlWithActivity(currentControl);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                currentControl.setFrequencyObject(null);
                mListener.syncControlWithActivity(currentControl);
            }
        });

        ArrayAdapter<CharSequence> natureSpinnerAdapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_item, natureDescriptionList);
        natureSpinnerAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        controlNatureSpinner.setAdapter(natureSpinnerAdapter);
        controlNatureSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0)
                    currentControl.setNatureObject(natureDescriptionObjectList.get(position-1));
                else
                    currentControl.setNatureObject(null);
                mListener.syncControlWithActivity(currentControl);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                currentControl.setNatureObject(null);
                mListener.syncControlWithActivity(currentControl);
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

        if (currentControl.getRiskClassificationObject() == null || currentControl.getTypeObject() == null
                || currentControl.getFrequencyObject() == null || currentControl.getNatureObject() == null) {
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

        if (controlOwner.getText().toString().isEmpty()) {
            controlOwner.setError(getString(R.string.error_field_required));
            return false;
        }

        return true;
    }

    private void populateNewControl() {
        currentControl.setName(controlNameView.getText().toString());
        currentControl.setDescription(controlDescriptionView.getText().toString());
        currentControl.setPopulation(controlPopulationView.getText().toString());
        currentControl.setOwner(controlOwner.getText().toString());
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

    public interface OnFragmentInteractionListener {
        void onSaveButtonClicked();
        void syncControlWithActivity(Control control);
    }

}
