package com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.models;

import com.parse.ParseObject;
import com.parse.ParseUser;

import java.io.Serializable;
import java.io.SerializablePermission;
import java.util.ArrayList;

public class Control implements Serializable {

    public static final String TABLE_CONTROL = "Control";
    public static final String KEY_CONTROL_ID = "controlId";
    public static final String KEY_CONTROL_NAME = "name";
    public static final String KEY_CONTROL_DESCRIPTION = "description";
    public static final String KEY_CONTROL_RISK = "risk";
    public static final String KEY_CONTROL_OWNER = "owner";
    public static final String KEY_CONTROL_POPULATION = "population";
    public static final String KEY_CONTROL_FREQUENCY = "frequency";
    public static final String KEY_CONTROL_NATURE = "nature";
    public static final String KEY_CONTROL_TYPE = "type";
    public static final String KEY_CONTROL_PROJECT = "project";
    public static final String KEY_CONTROL_MEMBER_RESPONSIBLE = "memberResponsible";
    public static final String KEY_CONTROL_SYSTEM_SCOPE = "systemScope";
    public static final String KEY_CONTROL_COMPANY_SCOPE = "companyScope";

    public static final String TABLE_CONTROL_FREQUENCY = "ControlFrequency";
    public static final String TABLE_CONTROL_NATURE = "ControlNature";
    public static final String TABLE_CONTROL_RISK = "ControlRisk";
    public static final String TABLE_CONTROL_TYPE = "ControlType";

    public static final String KEY_CONTROL_GENERIC_DESCRIPTION = "description";


    String name = "";
    String description = "";
    String population = "";
    String owner = "";

    ParseObject riskClassificationObject;
    ParseObject frequencyObject;
    ParseObject natureObject;
    ParseObject typeObject;
    ParseUser memberResponsible;
    ArrayList companyList = new ArrayList<>();
    ArrayList systemList = new ArrayList<>();


    public Control() {
    }

    public Control(ParseObject controlLoadedFromParse) {
        this.name = controlLoadedFromParse.getString(KEY_CONTROL_NAME);
        this.description = controlLoadedFromParse.getString(KEY_CONTROL_DESCRIPTION);
        this.population = controlLoadedFromParse.getString(KEY_CONTROL_POPULATION);
        this.owner = controlLoadedFromParse.getString(KEY_CONTROL_OWNER);
        this.riskClassificationObject = controlLoadedFromParse.getParseObject(KEY_CONTROL_RISK);
        this.frequencyObject = controlLoadedFromParse.getParseObject(KEY_CONTROL_FREQUENCY);
        this.natureObject = controlLoadedFromParse.getParseObject(KEY_CONTROL_NATURE);
        this.typeObject = controlLoadedFromParse.getParseObject(KEY_CONTROL_TYPE);
        this.memberResponsible = controlLoadedFromParse.getParseUser(KEY_CONTROL_MEMBER_RESPONSIBLE);
        this.companyList = (ArrayList) controlLoadedFromParse.getList(KEY_CONTROL_COMPANY_SCOPE);
        this.systemList = (ArrayList) controlLoadedFromParse.getList(KEY_CONTROL_SYSTEM_SCOPE);
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

    public String getPopulation() {
        return population;
    }

    public void setPopulation(String population) {
        this.population = population;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public ParseObject getRiskClassificationObject() {
        return riskClassificationObject;
    }

    public void setRiskClassificationObject(ParseObject riskClassificationObject) {
        this.riskClassificationObject = riskClassificationObject;
    }

    public ParseObject getFrequencyObject() {
        return frequencyObject;
    }

    public void setFrequencyObject(ParseObject frequencyObject) {
        this.frequencyObject = frequencyObject;
    }

    public ParseObject getNatureObject() {
        return natureObject;
    }

    public void setNatureObject(ParseObject natureObject) {
        this.natureObject = natureObject;
    }

    public ParseObject getTypeObject() {
        return typeObject;
    }

    public void setTypeObject(ParseObject typeObject) {
        this.typeObject = typeObject;
    }

    public ArrayList getSystemScopeList() {
        return systemList;
    }

    public void setSystemList(ArrayList<ParseObject> systemList) {
        this.systemList = systemList;
    }

    public ArrayList getCompanyScopeList() {
        return companyList;
    }

    public void setCompanyList(ArrayList<ParseObject> companyList) {
        this.companyList = companyList;
    }

    public ParseUser getMemberResponsibleObject() {
        return memberResponsible;
    }

    public void setMemberResponsible(ParseUser memberResponsible) {
        this.memberResponsible = memberResponsible;
    }
}
