package com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.parse.ParseObject;
import com.vieira.rodrigo.itgcmanager.R;
import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.models.Company;

import java.util.ArrayList;

public class CompanyListAdapter extends BaseAdapter{


    private Activity activity;
    private ArrayList<ParseObject> companyList;
    private static LayoutInflater inflater = null;

    public CompanyListAdapter(Activity activity, ArrayList<ParseObject> companyList) {
        this.activity = activity;
        this.companyList = companyList;

        inflater = LayoutInflater.from(activity);
    }

    public CompanyListAdapter() {

    }

    @Override
    public int getCount() {
        return companyList.size();
    }

    @Override
    public Object getItem(int position) {
        return companyList.get(position);
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
            rowView = inflater.inflate(R.layout.company_list_item, null);

            holder = new ViewHolder();
            holder.companyNameView = (TextView) rowView.findViewById(R.id.company_list_item_name);

            rowView.setTag(holder);
        } else
            holder = (ViewHolder) rowView.getTag();

        ParseObject companyObject = companyList.get(position);
        holder.companyNameView.setText(companyObject.getString(Company.KEY_COMPANY_NAME));

        return rowView;
    }

    public static class ViewHolder {
        public TextView companyNameView;
    }
}


