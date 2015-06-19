package com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.models;

import java.io.Serializable;

/**
 * Created by 305988 on 05/05/2015.
 */
public class Company  implements Serializable {

    public static final String TABLE_COMPANY = "Company";
    public static final String KEY_COMPANY_ID = "objectId";
    public static final String KEY_COMPANY_NAME = "name";

    private String id;
    private String name;
}
