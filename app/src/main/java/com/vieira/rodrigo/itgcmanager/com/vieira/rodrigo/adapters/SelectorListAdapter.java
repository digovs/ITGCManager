package com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.parse.ParseObject;
import com.vieira.rodrigo.itgcmanager.R;
import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.Utils.SelectorDialogActivity;
import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.models.Company;
import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.models.SystemApp;
import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.models.User;

import java.util.ArrayList;
import java.util.Arrays;

public class SelectorListAdapter extends BaseAdapter{


    private Context context;
    private ArrayList<ParseObject> dataList;
    private boolean[] selectedItems;
    private static LayoutInflater inflater = null;
    private int requestCode;

    public SelectorListAdapter(Context context, ArrayList<ParseObject> dataList, int requestCode) {
        this.context = context;
        this.dataList = dataList;
        this.requestCode = requestCode;
        this.selectedItems = new boolean[dataList.size()];
        Arrays.fill(selectedItems, Boolean.FALSE);

        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList.get(position);
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
            rowView = inflater.inflate(R.layout.selector_list_item, null);

            RelativeLayout backgroundLayout = (RelativeLayout) rowView.findViewById(R.id.selector_list_item_background);

            holder = new ViewHolder();
            holder.objectName = (TextView) rowView.findViewById(R.id.selector_list_item_name);

            rowView.setTag(holder);
        } else
            holder = (ViewHolder) rowView.getTag();

        ParseObject dataObject = dataList.get(position);

        switch (requestCode){
            case SelectorDialogActivity.REQUEST_MEMBER_LIST:
                holder.objectName.setText(dataObject.getString(User.KEY_USER_NAME));
                break;

            case SelectorDialogActivity.REQUEST_SYSTEM_LIST:
                holder.objectName.setText(dataObject.getString(SystemApp.KEY_SYSTEM_NAME));
                break;

            case SelectorDialogActivity.REQUEST_COMPANY_LIST:
                holder.objectName.setText(dataObject.getString(Company.KEY_COMPANY_NAME));
                break;
        }

        rowView.setTag(holder);
        return rowView;
    }

    public static class ViewHolder {
        public TextView objectName;
    }
}
