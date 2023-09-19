package com.softknife.testng.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TestCaseStatus{

    @JsonProperty("executionTime")
    private String executionTime;

    @JsonProperty("parameters")
    private String parameters;

    @JsonProperty("description")
    private String description;

    @JsonProperty("testClass")
    private String testClass;

    @JsonProperty("error")
    private String error;

    @JsonProperty("testName")
    private String testName;

    @JsonProperty("status")
    private String status;

    @JsonProperty("group")
    private String group;

    @JsonProperty("env")
    private String env;

    @JsonProperty("buildJobName")
    private String buildJobName;

    @JsonProperty("buildId")
    private String buildId;

    @JsonProperty("buildUrl")
    private String buildUrl;
}