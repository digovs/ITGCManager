package com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.models;

import com.parse.ParseObject;
import com.parse.ParseUser;

import java.io.Serializable;
import java.util.ArrayList;

public class Project implements Serializable{

    public static final String TABLE_PROJECT = "Project";
    public static final String KEY_PROJECT_OBJECT_ID = "objectId";
    public static final String KEY_PROJECT_CLIENT = "client";
    public static final String KEY_PROJECT_STATUS = "status";
    public static final String KEY_PROJECT_USER_RELATION = "users";
    public static final String KEY_PROJECT_NAME = "projectName";
    public static final String KEY_SYSTEM_SCOPE_LIST = "systemScopeList";
    public static final String KEY_COMPANY_SCOPE_LIST = "companyScopeList";

    String id;
    String name;
    String client;
    String status;
    ParseObject projectParseObject;
    ArrayList<ParseUser> users;
    ArrayList<ParseObject> systemList;
    ArrayList<ParseObject> companyList;

    public Project() {}

    public Project(ParseObject projectParseObject) {
        this.projectParseObject = projectParseObject;

        this.id = projectParseObject.getObjectId();
        this.name = projectParseObject.getString(KEY_PROJECT_NAME);
        this.client = projectParseObject.getString(KEY_PROJECT_CLIENT);
        this.status = projectParseObject.getString(KEY_PROJECT_STATUS);
        this.systemList = (ArrayList) projectParseObject.getList(KEY_SYSTEM_SCOPE_LIST);
        this.companyList = (ArrayList) projectParseObject.getList(KEY_COMPANY_SCOPE_LIST);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ParseObject getProjectParseObject() {
        return projectParseObject;
    }

    public void setProjectParseObject(ParseObject projectParseObject) {
        this.projectParseObject = projectParseObject;
    }

    public ArrayList<ParseUser> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<ParseUser> users) {
        this.users = users;
    }

    public void addUser(ParseUser newUser) {
        this.users.add(newUser);
    }

    public ArrayList<ParseObject> getSystemList() {
        return systemList;
    }

    public void setSystemList(ArrayList<ParseObject> systemList) {
        this.systemList = systemList;
    }

    public ArrayList<ParseObject> getCompanyList() {
        return companyList;
    }

    public void setCompanyList(ArrayList<ParseObject> companyList) {
        this.companyList = companyList;
    }
}
