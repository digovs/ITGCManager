package com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vieira.rodrigo.itgcmanager.TestActivity;
import com.vieira.rodrigo.itgcmanager.R;

import java.util.ArrayList;

public class TestMemberResponsibleFragment extends Fragment {

    public static final String MEMBER_RESPONSIBLE_ARGS_SELECTED_NAME = "member_responsible_args_selected_name";
    public static final String MEMBER_LIST_CONTENT = "member_list_content";
    private OnFragmentInteractionListener mListener;

    ArrayList<String> memberNameList = new ArrayList<>();
    ArrayAdapter adapter;

    RelativeLayout formView;
    TextView emptyText;
    Button saveButton;

    ListView memberListView;
    String selectedMemberName;

    int mode;
    Bundle memberResponsibleArgs;

    public TestMemberResponsibleFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        memberResponsibleArgs = getArguments();
        mode = memberResponsibleArgs.getInt(TestActivity.MODE_FLAG, TestActivity.ADD_MODE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_test_responsible, container, false);

        formView = (RelativeLayout) rootView.findViewById(R.id.test_define_member_responsible_form_view);
        emptyText = (TextView) rootView.findViewById(R.id.test_define_member_responsible_empty_text);
        saveButton = (Button) rootView.findViewById(R.id.test_define_member_responsible_save_button);

        memberListView = (ListView) rootView.findViewById(R.id.test_define_member_responsible_list_view);
        memberListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        memberListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedMemberName = memberNameList.get(position);
                mListener.saveSelectedMemberResponsibleToActivityTest(selectedMemberName);
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedMemberName != null) {
                    mListener.saveSelectedMemberResponsibleToActivityTest(selectedMemberName);
                    mListener.onSaveButtonClicked();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle(R.string.add_control_error_fragment_title)
                            .setMessage(R.string.control_define_member_responsible_field_is_required_message)
                            .setNeutralButton(R.string.add_control_error_fragment_ok_button, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            }).create().show();
                }
            }
        });

        switch (mode) {
            case TestActivity.VIEW_MODE:
                ArrayList<String> tempSelectedMemberResponsible = new ArrayList<>();
                tempSelectedMemberResponsible.add(memberResponsibleArgs.getString(MEMBER_RESPONSIBLE_ARGS_SELECTED_NAME));
                adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_single_choice, tempSelectedMemberResponsible);
                memberListView.setAdapter(adapter);
                memberListView.setItemChecked(0, true);
                memberListView.setEnabled(false);
                saveButton.setVisibility(View.GONE);
                break;

            case TestActivity.EDIT_MODE:
                loadMemberListContent();
                loadActivityCurrentTestMemberResponsible();
                break;

            case TestActivity.ADD_MODE:
                loadMemberListContent();
                break;
        }
        return rootView;
    }

    private void loadMemberListContent() {
        memberNameList = memberResponsibleArgs.getStringArrayList(MEMBER_LIST_CONTENT);
        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_single_choice, memberNameList);
        memberListView.setAdapter(adapter);
    }

    private void loadActivityCurrentTestMemberResponsible() {
        String selectedMemberResponsibleName = getArguments().getString(MEMBER_RESPONSIBLE_ARGS_SELECTED_NAME);
        memberListView.setItemChecked(memberNameList.indexOf(selectedMemberResponsibleName), true);
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
        void saveSelectedMemberResponsibleToActivityTest(String selectedMemberResponsibleName);
    }

}
