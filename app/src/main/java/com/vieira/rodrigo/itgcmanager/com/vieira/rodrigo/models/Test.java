package com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.models;

import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.Date;

public class Test {

    public static final String KEY_TEST_OBJECT_ID = "objectId";
    public static final String TABLE_TEST = "Test";
    public static final String KEY_TEST_NAME = "name";
    public static final String KEY_TEST_DESCRIPTION = "description";
    public static final String KEY_TEST_COVERAGE_DATE = "coverageDate";
    public static final String KEY_TEST_POPULATION = "population";
    public static final String KEY_TEST_SAMPLE = "sample";
    public static final String KEY_TEST_STATUS = "status";
    public static final String KEY_TEST_CONTROL = "control";
    public static final String KEY_TEST_SYSTEM_SCOPE = "systemScope";
    public static final String KEY_TEST_COMPANY_SCOPE = "companyScope";
    public static final String KEY_TEST_MEMBER_RESPONSIBLE = "memberResponsible";
    public static final String KEY_TEST_TYPE = "type";
    public static final String KEY_TEST_PROJECT = "project";

    String name;
    String description;
    Date coverageDate;
    int population;
    int sample;

    ParseObject statusObject;
    ParseObject controlObject;
    ArrayList<ParseObject> systemScopeObject;
    ArrayList<ParseObject> companyScopeObject;
    ParseObject memberResponsible;
    ParseObject typeObject;
    ParseObject projectObject;

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

    public Date getCoverageDate() {
        return coverageDate;
    }

    public void setCoverageDate(Date coverageDate) {
        this.coverageDate = coverageDate;
    }

    public int getPopulation() {
        return population;
    }

    public void setPopulation(int population) {
        this.population = population;
    }

    public int getSample() {
        return sample;
    }

    public void setSample(int sample) {
        this.sample = sample;
    }

    public ParseObject getControlObject() {
        return controlObject;
    }

    public void setControlObject(ParseObject controlObject) {
        this.controlObject = controlObject;
    }

    public ParseObject getMemberResponsible() {
        return memberResponsible;
    }

    public void setMemberResponsible(ParseObject memberResponsible) {
        this.memberResponsible = memberResponsible;
    }

    public ParseObject getStatusObject() {
        return statusObject;
    }

    public void setStatusObject(ParseObject statusObject) {
        this.statusObject = statusObject;
    }

    public ParseObject getTypeObject() {
        return typeObject;
    }

    public void setTypeObject(ParseObject typeObject) {
        this.typeObject = typeObject;
    }

    public ArrayList<ParseObject> getSystemScopeObject() {
        return systemScopeObject;
    }

    public void setSystemScopeObject(ArrayList<ParseObject> systemScopeObject) {
        this.systemScopeObject = systemScopeObject;
    }

    public ArrayList<ParseObject> getCompanyScopeObject() {
        return companyScopeObject;
    }

    public void setCompanyScopeObject(ArrayList<ParseObject> companyScopeObject) {
        this.companyScopeObject = companyScopeObject;
    }

    public ParseObject getProjectObject() {
        return projectObject;
    }

    public void setProjectObject(ParseObject projectObject) {
        this.projectObject = projectObject;
    }
}
