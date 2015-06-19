package com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import java.util.Arrays;

public class SelectorDialogFragment extends DialogFragment {

    public static final String KEY_ITEM_LIST_ARGS = "itemList";
    public static final String KEY_SELECTED_ITEMS_LIST_ARGS = "selectedItemList";
    public static final String KEY_TITLE_ARGS = "title";
    public static final String KEY_EDIT_MODE = "editMode";

    public interface SelectorDialogListener {
        void onSaveButtonClicked(boolean[] selectedItems, boolean editMode);
        void onDeleteButtonClicked(boolean editMode);
    }

    SelectorDialogListener listener;

    boolean[] selectedItems;
    CharSequence[] items;
    String title;
    boolean editMode = false;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            listener = (SelectorDialogListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement SelectorDialogListener");
        }

        editMode = getArguments().getBoolean(KEY_EDIT_MODE);
        items = getArguments().getCharSequenceArray(KEY_ITEM_LIST_ARGS);
        selectedItems = getArguments().getBooleanArray(KEY_SELECTED_ITEMS_LIST_ARGS);
        if (selectedItems == null) {
            selectedItems = new boolean[items.length];
            Arrays.fill(selectedItems, false);
        }

        title = getArguments().getString(KEY_TITLE_ARGS);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title)
                .setMultiChoiceItems(items, selectedItems, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        if (isChecked)
                            selectedItems[which] = true;
                        else
                            selectedItems[which] = false;
                    }
                })
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        listener.onSaveButtonClicked(selectedItems, editMode);
                    }
                })
                .setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        listener.onDeleteButtonClicked(editMode);
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
