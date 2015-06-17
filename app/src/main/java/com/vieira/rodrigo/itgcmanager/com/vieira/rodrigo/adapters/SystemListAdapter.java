package com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.parse.ParseObject;
import com.vieira.rodrigo.itgcmanager.R;
import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.models.SystemApp;

import java.util.ArrayList;

public class SystemListAdapter extends BaseAdapter{


    private Activity activity;
    private ArrayList<ParseObject> systemApps;
    private static LayoutInflater inflater = null;

    public SystemListAdapter(Activity activity, ArrayList<ParseObject> systemApps) {
        this.activity = activity;
        this.systemApps = systemApps;

        inflater = LayoutInflater.from(activity);
    }

    @Override
    public int getCount() {
        if (systemApps != null)
            return systemApps.size();
        else
            return 0;
    }

    @Override
    public Object getItem(int position) {
        if (systemApps != null)
            return systemApps.get(position);
        else
            return null;
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
            rowView = inflater.inflate(R.layout.system_list_item, null);

            holder = new ViewHolder();
            holder.systemNameView = (TextView) rowView.findViewById(R.id.system_list_item_name);

            rowView.setTag(holder);
        } else
            holder = (ViewHolder) rowView.getTag();

        ParseObject systemObject = systemApps.get(position);
        holder.systemNameView.setText(systemObject.getString(SystemApp.KEY_SYSTEM_NAME));

        return rowView;
    }

    public static class ViewHolder {
        public TextView systemNameView;
    }
}


