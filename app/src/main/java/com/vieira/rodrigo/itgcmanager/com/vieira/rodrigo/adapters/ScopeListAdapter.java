package com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.parse.ParseObject;
import com.vieira.rodrigo.itgcmanager.R;
import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.models.Company;
import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.models.Scope;
import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.models.SystemApp;

import java.util.ArrayList;

public class ScopeListAdapter extends BaseAdapter{

    Context context;
    ArrayList<ParseObject> scopeList;
    LayoutInflater inflater;

    public ScopeListAdapter(Context context, ArrayList<ParseObject> scopeList) {
        this.context = context;
        this.scopeList = scopeList;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        if (scopeList != null)
            return scopeList.size();
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if (scopeList != null)
            return scopeList.get(position);
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
            rowView = inflater.inflate(R.layout.scope_list_item, null);

            holder = new ViewHolder();
            holder.scopeSystemName = (TextView) rowView.findViewById(R.id.scope_list_item_system_name);
            holder.scopeCompanyNameList = (TextView) rowView.findViewById(R.id.scope_list_item_company_list);

            rowView.setTag(holder);
        } else
            holder = (ViewHolder) rowView.getTag();

        ParseObject system = scopeList.get(position).getParseObject(Scope.KEY_SYSTEM);
        ArrayList<ParseObject> companyList = (ArrayList) scopeList.get(position).getList(Scope.KEY_COMPANY_LIST);
        String companyListText = companyList.get(0).getString(Company.KEY_COMPANY_NAME);
        for (int i = 1; i < companyList.size(); i++) {
            companyListText = companyListText + ", " + companyList.get(i).getString(Company.KEY_COMPANY_NAME);
        }

        holder.scopeSystemName.setText(system.getString(SystemApp.KEY_SYSTEM_NAME));
        holder.scopeCompanyNameList.setText(companyListText);

        return rowView;
    }

    public static class ViewHolder {
        public TextView scopeSystemName;
        public TextView scopeCompanyNameList;
    }

}
