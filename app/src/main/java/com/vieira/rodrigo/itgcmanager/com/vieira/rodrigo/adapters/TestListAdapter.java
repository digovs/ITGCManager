package com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.parse.ParseObject;
import com.parse.ParseUser;
import com.vieira.rodrigo.itgcmanager.R;
import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.models.Control;
import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.models.Test;

import java.util.ArrayList;

public class TestListAdapter extends BaseAdapter{

    private LayoutInflater inflater;
    private ArrayList<ParseObject> testList;
    private Context context;

    public TestListAdapter(ArrayList<ParseObject> testList, Context context) {
        this.inflater = LayoutInflater.from(context);
        this.testList = testList;
        this.context = context;
    }

    @Override
    public int getCount() {
        if (testList != null) {
            return testList.size();
        } return 0;
    }

    @Override
    public Object getItem(int position) {
        if (testList != null)
            return testList.get(position);
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
            rowView = inflater.inflate(R.layout.test_list_item, null);

            holder = new ViewHolder();
            holder.testNameView = (TextView) rowView.findViewById(R.id.test_list_item_name);
            holder.testControlNameView = (TextView) rowView.findViewById(R.id.test_list_item_control_name);
            holder.testResponsibleView = (TextView) rowView.findViewById(R.id.test_list_item_responsible);
            holder.testHasExceptionsView = (TextView) rowView.findViewById(R.id.test_list_item_has_exceptions);
            holder.testStatusView = (TextView) rowView.findViewById(R.id.test_list_item_status);

            rowView.setTag(holder);
        } else
            holder = (ViewHolder) rowView.getTag();

        ParseObject testObject = testList.get(position);
        holder.testNameView.setText(testObject.getString(Test.KEY_TEST_NAME));

        ParseObject controlObject = testObject.getParseObject(Test.KEY_TEST_CONTROL);
        holder.testControlNameView.setText(controlObject.getString(Control.KEY_CONTROL_NAME));

        ParseUser responsibleObject = testObject.getParseUser(Test.KEY_TEST_MEMBER_RESPONSIBLE);
        holder.testResponsibleView.setText(responsibleObject.getUsername());

        boolean testHasExceptions = testObject.getBoolean(Test.KEY_TEST_HAS_EXCEPTIONS);
        if (testHasExceptions)
            holder.testHasExceptionsView.setText(context.getString(R.string.test_item_list_has_exceptions) + context.getString(R.string.test_activity_exception_radio_button_yes_text));
        else
            holder.testHasExceptionsView.setText(context.getString(R.string.test_item_list_has_exceptions) + context.getString(R.string.test_activity_exception_radio_button_no_text));

        ParseObject statusObject = testObject.getParseObject(Test.KEY_TEST_STATUS);
        holder.testStatusView.setText(statusObject.getString(Test.KEY_GENERIC_DESCRIPTION));

        return rowView;
    }

    public static class ViewHolder {
        public TextView testNameView;
        public TextView testControlNameView;
        public TextView testResponsibleView;
        public TextView testHasExceptionsView;
        public TextView testStatusView;
    }
}
