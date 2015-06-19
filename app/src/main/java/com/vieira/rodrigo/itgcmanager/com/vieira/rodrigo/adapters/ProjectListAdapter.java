package com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.parse.ParseObject;
import com.vieira.rodrigo.itgcmanager.R;
import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.models.Project;

import java.util.ArrayList;

public class ProjectListAdapter  extends BaseAdapter {

    private Activity activity;
    private ArrayList<ParseObject> projects;
    private static LayoutInflater inflater = null;

    public ProjectListAdapter(Activity activity, ArrayList<ParseObject> projects) {
        this.activity = activity;
        this.projects = projects;

        inflater = LayoutInflater.from(activity);
    }

    public ProjectListAdapter() {

    }

    @Override
    public int getCount() {
        return projects.size();
    }

    @Override
    public Object getItem(int position) {
        return projects.get(position);
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
            rowView = inflater.inflate(R.layout.project_list_item, null);

            holder = new ViewHolder();
            holder.projectNameView = (TextView) rowView.findViewById(R.id.project_list_item_project_name);

            rowView.setTag(holder);
        } else
            holder = (ViewHolder) rowView.getTag();

        ParseObject currentProject = projects.get(position);
        holder.projectNameView.setText(currentProject.getString(Project.KEY_PROJECT_NAME));

        return rowView;
    }

    public static class ViewHolder {
        public TextView projectNameView;
    }
}
