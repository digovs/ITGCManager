package com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.models;

import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;

public class Test {

    public static final String TABLE_TEST = "Test";
    public static final String TABLE_TEST_TYPE = "TestType";
    public static final String TABLE_TEST_STATUS = "TestStatus";
    public static final String KEY_TEST_ID = "testId";
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

    public static final String KEY_GENERIC_DESCRIPTION = "description";
    public static final String KEY_GENERAL_DESCRIPTION_BR = "description_br";

    String name;
    String description;
    GregorianCalendar coverageDate;
    int population;
    int sample;

    ParseObject statusObject;
    ParseObject controlObject;
    ArrayList<ParseObject> systemScopeObject;
    ArrayList<ParseObject> companyScopeObject;
    ParseUser memberResponsible;
    ParseObject typeObject;
    ParseObject projectObject;

    public Test() {

    }

    public Test(ParseObject testObject) {
        this.name = testObject.getString(KEY_TEST_NAME);
        this.description = testObject.getString(KEY_TEST_DESCRIPTION);
        Date date = testObject.getDate(KEY_TEST_COVERAGE_DATE);
        this.coverageDate = new GregorianCalendar();
        this.coverageDate.setTime(date);

        this.population = testObject.getInt(KEY_TEST_POPULATION);
        this.sample = testObject.getInt(KEY_TEST_SAMPLE);

        this.statusObject = testObject.getParseObject(KEY_TEST_STATUS);
        this.controlObject = testObject.getParseObject(KEY_TEST_CONTROL);
        this.systemScopeObject = (ArrayList) testObject.getList(KEY_TEST_SYSTEM_SCOPE);
        this.companyScopeObject = (ArrayList) testObject.getList(KEY_TEST_COMPANY_SCOPE);
        this.memberResponsible = testObject.getParseUser(KEY_TEST_MEMBER_RESPONSIBLE);
        this.typeObject = testObject.getParseObject(KEY_TEST_TYPE);
        this.projectObject = testObject.getParseObject(KEY_TEST_PROJECT);
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

    public GregorianCalendar getCoverageDate() {
        return coverageDate;
    }

    public void setCoverageDate(GregorianCalendar coverageDate) {
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

    public void setMemberResponsible(ParseUser memberResponsible) {
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
