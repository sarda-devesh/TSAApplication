package com.example.deves.tsaapplication;

import java.util.ArrayList;
import java.util.List;

public class TestScores {
    String testname;
    List<String> testscores;
    String testID;
    boolean testing = true;

    public TestScores() {

    }

    public TestScores(String name, ArrayList<String> scores, String id) {
        testname = name;
        testID = id;
        testscores = scores;
    }


    public boolean isTesting() {
        return  testing;
    }

    public String getTestname() {
        return testname;
    }

    public List<String> getTestscores() {
        return testscores;
    }

    public void setTestname(String testname) {
        this.testname = testname;
    }


    public void setTestID(String testID) {
        this.testID = testID;
    }

    public String getTestID() {
        return testID;
    }
}
