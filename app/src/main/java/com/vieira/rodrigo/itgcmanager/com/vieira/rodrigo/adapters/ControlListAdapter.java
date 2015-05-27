package com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.adapters;


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
import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.models.User;

import java.util.ArrayList;

public class ControlListAdapter extends BaseAdapter{

    private LayoutInflater inflater;
    private ArrayList<ParseObject> controlList;
    private Context context;

    public ControlListAdapter(Context context, ArrayList<ParseObject> controlList) {
        this.controlList = controlList;
        this.context = context;

        inflater = LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return controlList.size();
    }

    @Override
    public Object getItem(int position) {
        return controlList.get(position);
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
            rowView = inflater.inflate(R.layout.control_list_item, null);

            holder = new ViewHolder();
            holder.controlNameView = (TextView) rowView.findViewById(R.id.control_list_item_name);
            holder.controlOwnerView = (TextView) rowView.findViewById(R.id.control_list_item_owner);

            rowView.setTag(holder);
        } else
            holder = (ViewHolder) rowView.getTag();

        ParseObject controlObject = controlList.get(position);
        holder.controlNameView.setText(controlObject.getString(Control.KEY_CONTROL_NAME));

        ParseUser owner = (ParseUser) controlObject.get(Control.KEY_CONTROL_OWNER);
        holder.controlOwnerView.setText(owner.getString(User.KEY_USER_NAME));

        return rowView;
    }

    public static class ViewHolder {
        public TextView controlNameView;
        public TextView controlOwnerView;
    }
}