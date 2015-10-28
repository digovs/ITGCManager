package com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.models;

import com.parse.ParseObject;

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
    public static final String KEY_CONTROL_SCOPE_LIST = "controlScopeList";
    public static final String KEY_NEW_CONTROL = "newControl";

    public static final String TABLE_CONTROL_FREQUENCY = "ControlFrequency";
    public static final String TABLE_CONTROL_NATURE = "ControlNature";
    public static final String TABLE_CONTROL_RISK = "ControlRisk";
    public static final String TABLE_CONTROL_TYPE = "ControlType";

    public static final String KEY_CONTROL_GENERIC_DESCRIPTION = "description";


    String name = "";
    String description = "";
    String riskClassification = "";
    String population = "";
    String owner = "";
    String type = "";
    String frequency = "";
    String nature = "";
    ArrayList<ParseObject> controlScopeList = new ArrayList<>();
    ArrayList<String> controlScopeIdList = new ArrayList<>();
    ParseObject riskClassificationObject;
    ParseObject frequencyObject;
    ParseObject natureObject;
    ParseObject typeObject;

    ArrayList<ParseObject> companyList = new ArrayList<>();
    ArrayList<ParseObject> systemList = new ArrayList<>();

    public Control() {
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

    public String getRiskClassification() {
        return riskClassification;
    }

    public void setRiskClassification(String riskClassification) {
        this.riskClassification = riskClassification;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public String getNature() {
        return nature;
    }

    public void setNature(String nature) {
        this.nature = nature;
    }

    public ArrayList<ParseObject> getControlScopeList() {
        return controlScopeList;
    }

    public void setControlScopeList(ArrayList<ParseObject> controlScopeList) {
        this.controlScopeList = controlScopeList;
    }

    public ArrayList<String> getControlScopeIdList() {
        return controlScopeIdList;
    }

    public void setControlScopeIdList(ArrayList<String> controlScopeIdList) {
        this.controlScopeIdList = controlScopeIdList;
    }

    public void addControlScopeObject(ParseObject controlScope) {
        this.controlScopeList.add(controlScope);
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
