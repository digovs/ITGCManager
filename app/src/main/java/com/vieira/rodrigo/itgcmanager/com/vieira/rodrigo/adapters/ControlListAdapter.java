package com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.adapters;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.vieira.rodrigo.itgcmanager.R;
import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.Utils.ParseUtils;
import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.models.Control;
import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.models.Test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class ControlListAdapter extends BaseAdapter{

    private LayoutInflater inflater;
    private ArrayList<ParseObject> controlList;
    public static Context context;

    public ControlListAdapter(Context context, ArrayList<ParseObject> controlList) {
        this.controlList = controlList;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        if (controlList != null)
            return controlList.size();
        else
            return 0;
    }

    @Override
    public Object getItem(int position) {
        if (controlList != null)
            return controlList.get(position);
        else
            return 0;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        ViewHolder holder;

        if (convertView == null) {
            rowView = inflater.inflate(R.layout.control_list_item, null);

            holder = new ViewHolder();
            holder.controlNameView = (TextView) rowView.findViewById(R.id.control_list_item_name);
            holder.controlResponsibleView = (TextView) rowView.findViewById(R.id.control_list_item_member_responsible);
            holder.controlCoverageDateFromTestsView = (TextView) rowView.findViewById(R.id.control_list_item_coverage_date);
            holder.controlNumberOfTests = (TextView) rowView.findViewById(R.id.control_list_item_number_of_tests);
            holder.controlConclusionPercentage = (TextView) rowView.findViewById(R.id.control_list_item_conclusion_percentage);

            rowView.setTag(holder);
        } else
            holder = (ViewHolder) rowView.getTag();

        ParseObject controlObject = controlList.get(position);
        holder.controlNameView.setText(controlObject.getString(Control.KEY_CONTROL_NAME));

        ParseUser responsible = controlObject.getParseUser(Control.KEY_CONTROL_MEMBER_RESPONSIBLE);
        holder.controlResponsibleView.setText("Responsible: " + responsible.getUsername());

        TextView coverageDateTextView = holder.controlCoverageDateFromTestsView;
        TextView numberOfTestsView = holder.controlNumberOfTests;
        TextView percentageTextView = holder.controlConclusionPercentage;

        displayItems(controlObject, coverageDateTextView, numberOfTestsView, percentageTextView);

        return rowView;
    }

    private void displayItems(final ParseObject control, final TextView coverageDateView, final TextView numberOfTestsView, final TextView percentageTextView) {
        ParseQuery<ParseObject> getControlTests = ParseQuery.getQuery(Test.TABLE_TEST);
        getControlTests.include(Test.KEY_TEST_COMPANY_SCOPE)
                .include(Test.KEY_TEST_SYSTEM_SCOPE);
        getControlTests.whereEqualTo(Test.KEY_TEST_CONTROL, control);
        getControlTests.findInBackground(new FindCallback<ParseObject>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null && !list.isEmpty()) {
                    Date latestCoverageDate = new Date(0);
                    List<ParseObject> testCompanyList = new ArrayList<>();
                    List<ParseObject> testSystemList = new ArrayList<>();

                    for (ParseObject testObject : list) {
                        Date coverageDate = testObject.getDate(Test.KEY_TEST_COVERAGE_DATE);
                        if (coverageDate.after(latestCoverageDate))
                            latestCoverageDate = coverageDate;

                        List<ParseObject> tempCompanyList = testObject.getList(Test.KEY_TEST_COMPANY_SCOPE);
                        if (tempCompanyList != null && !tempCompanyList.isEmpty()) {
                            for (ParseObject company : tempCompanyList) {
                                if (!testCompanyList.contains(company)) {
                                    testCompanyList.add(company);
                                }
                            }
                        }

                        List<ParseObject> tempSystemList = testObject.getList(Test.KEY_TEST_SYSTEM_SCOPE);
                        if (tempSystemList != null && !tempSystemList.isEmpty()) {
                            for (ParseObject system : tempSystemList) {
                                if (!testSystemList.contains(system)) {
                                    testSystemList.add(system);
                                }
                            }
                        }
                    }

                    GregorianCalendar dateGreg = new GregorianCalendar();
                    dateGreg.setTime(latestCoverageDate);
                    coverageDateView.setText("Date Covered: " + dateGreg.get(GregorianCalendar.YEAR) + "/" + (dateGreg.get(GregorianCalendar.MONTH)+1) + "/" + dateGreg.get(GregorianCalendar.DAY_OF_MONTH));
                    numberOfTestsView.setText("Tests: " + list.size());

                    String projectYearCoverage = ParseUtils.getStringFromSession(context, ParseUtils.PREFS_CURRENT_PROJECT_YEAR_COVERAGE);

                    boolean isYearCovered = false;
                    if (dateGreg.get(GregorianCalendar.YEAR) >= Integer.parseInt(projectYearCoverage) || (dateGreg.get(GregorianCalendar.YEAR) == Integer.parseInt(projectYearCoverage) &&
                            dateGreg.get(GregorianCalendar.MONTH) == 11 && dateGreg.get(Calendar.DAY_OF_MONTH) == 31))
                        isYearCovered = true;

                    List<ParseObject> controlSystemScope = control.getList(Control.KEY_CONTROL_SYSTEM_SCOPE);
                    List<ParseObject> controlCompanyScope = control.getList(Control.KEY_CONTROL_COMPANY_SCOPE);

                    int fullPercentageCompany = controlCompanyScope.size();
                    int fullPercentageSystem = controlSystemScope.size();

                    int fullPercentageTotal = fullPercentageCompany + fullPercentageSystem;
                    int realPercentage = testCompanyList.size() + testSystemList.size();

                    float percentage = Float.intBitsToFloat(realPercentage) / Float.intBitsToFloat(fullPercentageTotal);
                    float displayPercentage;
                    if (realPercentage >= fullPercentageTotal)
                        displayPercentage = 50f;
                    else
                        displayPercentage = percentage*50f;

                    if (isYearCovered)
                        displayPercentage = displayPercentage + 50f;

                    percentageTextView.setText(displayPercentage + "%");
                } else {
                    coverageDateView.setText("Coverage Date: NONE");
                    numberOfTestsView.setText("Tests: 0");
                    percentageTextView.setText("0%");
                }
            }
        });

    }

    public static class ViewHolder {
        public TextView controlNameView;
        public TextView controlResponsibleView;
        public TextView controlCoverageDateFromTestsView;
        public TextView controlNumberOfTests;
        public TextView controlConclusionPercentage;
    }


}
