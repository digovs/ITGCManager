package com.vieira.rodrigo.itgcmanager;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.models.Control;

public class AddControlActivityStepDetails extends ActionBarActivity {

    public static final String KEY_NEW_CONTROL = "newControl";
    public static final int REQUEST_DEFINE_SCOPE = 7654;
    Control newControl;

    EditText controlNameView;
    EditText controlDescriptionView;
    Spinner controlRiskClassSpinner;
    EditText controlPopulationView;
    EditText controlOwner;
    Spinner controlTypeSpinner;
    Spinner controlFrequencySpinner;
    Spinner controlNatureSpinner;
    Button continueButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_control_step_details);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);

        newControl = new Control();

        controlNameView = (EditText) findViewById(R.id.add_control_name);
        controlDescriptionView = (EditText) findViewById(R.id.add_control_description);
        controlRiskClassSpinner = (Spinner) findViewById(R.id.add_control_risk_spinner);
        controlPopulationView = (EditText) findViewById(R.id.add_control_population);
        controlOwner = (EditText) findViewById(R.id.add_control_owner_view);
        controlTypeSpinner = (Spinner) findViewById(R.id.add_control_type_spinner);
        controlFrequencySpinner = (Spinner) findViewById(R.id.add_control_frequency_spinner);
        controlNatureSpinner = (Spinner) findViewById(R.id.add_control_nature_spinner);

        loadSpinnersContent();

        continueButton = (Button) findViewById(R.id.add_control_save_button);
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFormValid()) {
                    populateNewControl();
                    Intent intent = new Intent(getApplicationContext(), AddControlActivityStepDefineScope.class);
                    intent.putExtra(Control.KEY_NEW_CONTROL, newControl);
                    startActivityForResult(intent, REQUEST_DEFINE_SCOPE);
                }
            }
        });
    }

    private void loadSpinnersContent() {
        ArrayAdapter<CharSequence> riskSpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.add_control_risk_classification_options, R.layout.spinner_item);
        riskSpinnerAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        controlRiskClassSpinner.setAdapter(riskSpinnerAdapter);
        controlRiskClassSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0)
                    newControl.setRiskClassification(((TextView) view).getText().toString());
                else
                    newControl.setRiskClassification("");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                newControl.setRiskClassification("");
            }
        });

        ArrayAdapter<CharSequence> typeSpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.add_control_type_options, R.layout.spinner_item);
        typeSpinnerAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        controlTypeSpinner.setAdapter(typeSpinnerAdapter);
        controlTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0)
                    newControl.setType(((TextView) view).getText().toString());
                else
                    newControl.setType("");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                newControl.setType("");
            }
        });

        ArrayAdapter<CharSequence> frequencySpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.add_control_frequency_options, R.layout.spinner_item);
        frequencySpinnerAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        controlFrequencySpinner.setAdapter(frequencySpinnerAdapter);
        controlFrequencySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0)
                    newControl.setFrequency(((TextView) view).getText().toString());
                else
                    newControl.setFrequency("");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                newControl.setFrequency("");
            }
        });

        ArrayAdapter<CharSequence> natureSpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.add_control_nature_options, R.layout.spinner_item);
        natureSpinnerAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        controlNatureSpinner.setAdapter(natureSpinnerAdapter);
        controlNatureSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0)
                    newControl.setNature(((TextView) view).getText().toString());
                else
                    newControl.setNature("");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                newControl.setNature("");
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

        if (newControl.getRiskClassification().isEmpty() || newControl.getType().isEmpty()
                || newControl.getFrequency().isEmpty() || newControl.getNature().isEmpty()) {
            DialogFragment errorDialogFragment = new FormErrorMessageDialogFragment();
            errorDialogFragment.show(getSupportFragmentManager(), "error");
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
        newControl.setName(controlNameView.getText().toString());
        newControl.setDescription(controlDescriptionView.getText().toString());
        newControl.setPopulation(controlPopulationView.getText().toString());
        newControl.setOwner(controlOwner.getText().toString());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_control, menu);
        return true;
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

    public static class FormErrorMessageDialogFragment extends DialogFragment {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(getString(R.string.add_control_error_fragment_title))
                    .setMessage(R.string.add_control_details_error_fragment_message)
                    .setNeutralButton(R.string.add_control_error_fragment_ok_button, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dismiss();
                        }
                    });

            return builder.create();
        }
    }
}
