package com.vieira.rodrigo.itgcmanager.com.vieira.rodrigo.models;

import com.parse.ParseObject;

public class TestException {

    public static final String TABLE_TEST_EXCEPTION = "Exception";
    public static final String KEY_TEST_OBJECT = "test";
    public static final String KEY_TEST_EXCEPTION_TITLE = "title";
    public static final String KEY_TEST_EXCEPTION_DESCRIPTION = "description";
    public static final String KEY_TEST_EXCEPTION_NUMBER_OF_EXCEPTIONS = "numberOfExceptions";
    public static final String KEY_TEST_EXCEPTION_IS_REMEDIATED = "isRemediated";
    public static final String KEY_TEST_EXCEPTION_REMEDIATION_RATIONALE = "remediationRationale";
    public static final String KEY_TEST_EXCEPTION_IS_SIGNIFICANT = "isSignificant";

    ParseObject testObject;
    String title;
    String description;
    int numberOfExceptions;
    boolean isRemediated;
    String remediationRationale;
    boolean isSignificant;

    public TestException() {
    }

    public TestException(ParseObject testExceptionObject) {
        this.title = testExceptionObject.getString(KEY_TEST_EXCEPTION_TITLE);
        this.description = testExceptionObject.getString(KEY_TEST_EXCEPTION_DESCRIPTION);
        this.remediationRationale = testExceptionObject.getString(KEY_TEST_EXCEPTION_REMEDIATION_RATIONALE);
        this.isRemediated = testExceptionObject.getBoolean(KEY_TEST_EXCEPTION_IS_REMEDIATED);
        this.isSignificant = testExceptionObject.getBoolean(KEY_TEST_EXCEPTION_IS_SIGNIFICANT);
        this.numberOfExceptions = testExceptionObject.getInt(KEY_TEST_EXCEPTION_NUMBER_OF_EXCEPTIONS);
        this.testObject = testExceptionObject.getParseObject(KEY_TEST_OBJECT);
    }

    public ParseObject getTestObject() {
        return testObject;
    }

    public void setTestObject(ParseObject testObject) {
        this.testObject = testObject;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getNumberOfExceptions() {
        return numberOfExceptions;
    }

    public void setNumberOfExceptions(int numberOfExceptions) {
        this.numberOfExceptions = numberOfExceptions;
    }

    public boolean isRemediated() {
        return isRemediated;
    }

    public void setIsRemediated(boolean isRemediated) {
        this.isRemediated = isRemediated;
    }

    public String getRemediationRationale() {
        return remediationRationale;
    }

    public void setRemediationRationale(String remediationRationale) {
        this.remediationRationale = remediationRationale;
    }

    public boolean isSignificant() {
        return isSignificant;
    }

    public void setIsSignificant(boolean isSignificant) {
        this.isSignificant = isSignificant;
    }
}
