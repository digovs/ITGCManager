package com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.parse.ParseObject;
import com.vieira.rodrigo.itgcmanager.R;
import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.models.Control;
import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.models.Test;

import java.util.ArrayList;

public class TestListAdapter extends BaseAdapter{

    private LayoutInflater inflater;
    private ArrayList<ParseObject> testList;

    public TestListAdapter(ArrayList<ParseObject> testList, Context context) {
        this.inflater = LayoutInflater.from(context);
        this.testList = testList;
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        ViewHolder holder;

        if (convertView == null) {
            rowView = inflater.inflate(R.layout.test_list_item, null);

            holder = new ViewHolder();
            holder.testNameView = (TextView) rowView.findViewById(R.id.test_list_item_name);
            holder.controlNameView = (TextView) rowView.findViewById(R.id.test_list_item_owner);

            rowView.setTag(holder);
        } else
            holder = (ViewHolder) rowView.getTag();

        ParseObject testObject = testList.get(position);
        holder.testNameView.setText(testObject.getString(Test.KEY_TEST_NAME));

        ParseObject controlObject = testObject.getParseObject(Test.KEY_TEST_CONTROL);
        holder.controlNameView.setText(controlObject.getString(Control.KEY_CONTROL_NAME));

        return rowView;
    }

    public static class ViewHolder {
        public TextView testNameView;
        public TextView controlNameView;
    }
}
