package com.softknife.testng.listener.model;

import lombok.Getter;
import lombok.Setter;

/**
 * @author amatsaylo on 3/21/25
 * @project demo-restapi-test
 */
@Getter
@Setter
public abstract class BaseCustomReport {

    private String runId;
    private String description;
    private String executionTime;
    private String startDate;
    private String endDate;
    private String env;
    private String buildJobName;
    private String buildId;
    private String buildUrl;
    private String suiteName;

}
