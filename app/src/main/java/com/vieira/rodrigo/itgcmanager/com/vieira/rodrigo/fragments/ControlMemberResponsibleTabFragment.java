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
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.vieira.rodrigo.itgcmanager.R;
import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.Utils.ParseUtils;
import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.models.Project;
import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.models.User;

import java.util.ArrayList;
import java.util.List;

public class ControlMemberResponsibleTabFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    ArrayList<ParseUser> memberList = new ArrayList<>();
    ArrayList<CharSequence> memberNameList = new ArrayList<>();
    ArrayAdapter adapter;

    RelativeLayout formView;
    ProgressBar progressBar;
    TextView emptyText;
    Button saveButton;

    ListView memberListView;
    ParseUser selectedMember;

    public ControlMemberResponsibleTabFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_control_responsible, container, false);

        formView = (RelativeLayout) rootView.findViewById(R.id.control_define_member_responsible_form_view);
        progressBar = (ProgressBar) rootView.findViewById(R.id.control_define_member_responsible_progress_bar);
        emptyText = (TextView) rootView.findViewById(R.id.control_define_member_responsible_empty_text);
        saveButton = (Button) rootView.findViewById(R.id.control_define_member_responsible_save_button);
        memberListView = (ListView) rootView.findViewById(R.id.control_define_member_responsible_list_view);

        loadMemberList();
        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_single_choice, memberNameList);
        memberListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        memberListView.setAdapter(adapter);

        memberListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedMember = memberList.get(position);
                mListener.saveSelectedMemberResponsibleToActivityControl(selectedMember);
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedMember != null) {
                    mListener.saveSelectedMemberResponsibleToActivityControl(selectedMember);
                    mListener.onSaveButtonClicked();
                }
                else {
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

        return rootView;
    }

    private void loadMemberList() {
        showProgress(true);
        String currentProjectId = ParseUtils.getStringFromSession(getActivity(), ParseUtils.PREFS_CURRENT_PROJECT_ID);
        ParseQuery<ParseObject> getCurrentProjectObject = ParseQuery.getQuery(Project.TABLE_PROJECT);
        getCurrentProjectObject.getInBackground(currentProjectId, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject projectObject, ParseException e) {
                if (e == null) {
                    ParseRelation<ParseUser> projectUserRelation = projectObject.getRelation(Project.KEY_PROJECT_USER_RELATION);
                    ParseQuery<ParseUser> getProjectUserList = projectUserRelation.getQuery();
                    getProjectUserList.findInBackground(new FindCallback<ParseUser>() {
                        @Override
                        public void done(List<ParseUser> resultList, ParseException e) {
                            showProgress(false);
                            if (e == null) {
                                memberList = (ArrayList<ParseUser>) resultList;
                                for (ParseUser user : memberList) {
                                    memberNameList.add(user.getString(User.KEY_USER_FULL_NAME));
                                }
                            } else {
                                ParseUtils.handleParseException(getActivity(), e);
                            }
                        }
                    });
                } else {
                    showProgress(false);
                    ParseUtils.handleParseException(getActivity(), e);
                }
            }
        });
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
        void saveSelectedMemberResponsibleToActivityControl(ParseUser selectedMemberResponsible);
    }

}
