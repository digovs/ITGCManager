package com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.vieira.rodrigo.itgcmanager.R;
import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.models.Project;
import com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.models.User;

import java.util.ArrayList;

public class MemberListAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private ArrayList<User> userList;
    private Context context;

    public MemberListAdapter(Context context, ArrayList<User> userList) {
        this.userList = userList;
        this.context = context;

        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return userList.size();
    }

    @Override
    public User getItem(int position) {
        return userList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void swapList(ArrayList<User> newUserList) {
        this.userList = newUserList;
        notifyDataSetChanged();
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        ViewHolder holder;

        if (convertView == null) {
            rowView = inflater.inflate(R.layout.member_list_item, null);

            holder = new ViewHolder();
            holder.userNameView = (TextView) rowView.findViewById(R.id.member_list_item_user_name);
            holder.userFullNameView = (TextView) rowView.findViewById(R.id.member_list_item_user_full_name);
            holder.userEmailView = (TextView) rowView.findViewById(R.id.member_list_item_user_email);

            rowView.setTag(holder);
        } else
            holder = (ViewHolder) rowView.getTag();

        User currentUser = userList.get(position);
        holder.userNameView.setText(currentUser.getUserName());
        holder.userFullNameView.setText(currentUser.getFullName());
        holder.userEmailView.setText(currentUser.getEmail());

        return rowView;
    }

    public static class ViewHolder {
        public TextView userNameView;
        public TextView userFullNameView;
        public TextView userEmailView;
    }
}
