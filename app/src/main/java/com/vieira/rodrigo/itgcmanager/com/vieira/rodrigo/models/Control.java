package com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.models;

import com.parse.ParseObject;

import java.util.ArrayList;

public class Control {

    public static final String TABLE_CONTROL = "Control";
    public static final String KEY_CONTROL_ID = "controlId";
    public static final String KEY_CONTROL_NAME = "name";
    public static final String KEY_CONTROL_DESCRIPTION = "description";
    public static final String KEY_CONTROL_POPULATION = "population";
    public static final String KEY_CONTROL_IS_AUTOMATIC = "isAutomatic";
    public static final String KEY_CONTROL_PROJECT = "project";
    public static final String KEY_CONTROL_OWNER = "owner";
    public static final String KEY_CONTROL_SCOPE_LIST = "controlScopeList";

    String id;
    String name;
    String description;
    boolean isAutomatic;
    ArrayList<ParseObject> companyList;
    ArrayList<ParseObject> systemList;

    public Control() {
    }

    public Control(String id, String name, String description, boolean isAutomatic, ArrayList<ParseObject> companyList, ArrayList<ParseObject> systemList) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.isAutomatic = isAutomatic;
        this.companyList = companyList;
        this.systemList = systemList;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isAutomatic() {
        return isAutomatic;
    }

    public void setIsAutomatic(boolean isAutomatic) {
        this.isAutomatic = isAutomatic;
    }

    public ArrayList<ParseObject> getCompanyList() {
        return companyList;
    }

    public void setCompanyList(ArrayList<ParseObject> companyList) {
        this.companyList = companyList;
    }

    public ArrayList<ParseObject> getSystemList() {
        return systemList;
    }

    public void setSystemList(ArrayList<ParseObject> systemList) {
        this.systemList = systemList;
    }
}
