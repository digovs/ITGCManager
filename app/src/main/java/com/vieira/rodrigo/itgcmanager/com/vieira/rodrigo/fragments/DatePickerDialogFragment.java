package com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.fragments;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import com.vieira.rodrigo.itgcmanager.AddTestActivity;
import com.vieira.rodrigo.itgcmanager.R;

import java.util.Calendar;

public class DatePickerDialogFragment extends DialogFragment{

    public DatePickerDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(getActivity(), (AddTestActivity) getActivity(), year, month, day);
    }

}
