package com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.models;

import com.parse.Parse;
import com.parse.ParseObject;

import java.util.ArrayList;

public class Project {

    public static final String TABLE_PROJECT = "Project";
    public static final String KEY_PROJECT_OBJECT_ID = "objectId";
    public static final String KEY_PROJECT_ID = "projectId";
    public static final String KEY_PROJECT_USER_RELATION = "users";
    public static final String KEY_PROJECT_NAME = "projectName";
    public static final String KEY_SYSTEM_SCOPE_LIST = "systemScopeList";
    public static final String KEY_COMPANY_SCOPE_LIST = "companyScopeList";

    private String id;
    String name;

    public Project() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Project(String name) {
        this.name = name;
    }

    public Project(ParseObject parseObject) {
        this.name = parseObject.getString(KEY_PROJECT_NAME);
        this.id = parseObject.getObjectId();
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
