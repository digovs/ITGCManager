package com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.models;

import java.io.Serializable;

public class SystemApp  implements Serializable {

    public static final String TABLE_SYSTEM = "System";
    public static final String KEY_SYSTEM_ID = "objectId";
    public static final String KEY_SYSTEM_NAME = "name";

    private String id;
    private String name;

    public SystemApp() {
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
}
