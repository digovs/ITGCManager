package com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.models;

import com.parse.ParseObject;
import com.parse.ParseUser;

public class User {

    public static final String TABLE_USER = "User";
    public static final String KEY_USER_FULL_NAME = "fullName";
    public static final String KEY_USER_ROLE = "role";
    public static final String KEY_USER_EMAIL = "email";
    public static final String KEY_USER_ID = "id";
    public static final String KEY_USER_OBJECT_ID = "objectId";

    public static final String KEY_USER_NAME = "username";
    public static final String KEY_USER_PASSWORD = "password";

    public static final String KEY_SELECTED_USER_ID = "selectedUserId";
    public static final String KEY_SELECTED_USER_NAME = "selectedUserName";

    private String id;
    private String fullName;
    private String email;
    private String userName;
    private ParseUser parseUser;

    public User() {
    }

    public User(ParseUser object) {
        this.fullName = object.getString(KEY_USER_FULL_NAME);
        this.userName = object.getString(KEY_USER_NAME);
        this.email = object.getString(KEY_USER_EMAIL);
        this.id = object.getObjectId();
        this.parseUser = object;
    }

    public User(String fullName, String email, String userName) {
        this.fullName = fullName;
        this.email = email;
        this.userName = userName;
    }

    public String getFullName() {
        return fullName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public ParseUser getParseUser() {
        return parseUser;
    }

    public void setParseUser(ParseUser parseUser) {
        this.parseUser = parseUser;
    }
}
