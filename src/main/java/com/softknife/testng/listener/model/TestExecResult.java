package com.softknife.testng.listener.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TestExecResult extends BaseCustomReport {
    private String parameters;
    private String testClass;
    private String assertError;
    private String logError;
    private String testName;
    private ITestStatus status;
    private String group;
    private String pipeLineName;
}