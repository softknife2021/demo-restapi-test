package com.softknife.testng.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TestExecResult {

    private String runId;
    private String runDescription;
    private String executionTime;
    private String startDate;
    private String endDate;
    private String parameters;
    private String description;
    private String testClass;
    private String error;
    private String testName;
    private ITestStatus status;
    private String group;
    private String env;
    private String buildJobName;
    private String buildId;
    private String buildUrl;
}