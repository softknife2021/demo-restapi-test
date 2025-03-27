package com.softknife.testng.listener.model;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

/**
 * @author amatsaylo on 3/21/25
 * @project demo-restapi-test
 */
@Getter
@Setter
public abstract class BaseCustomReport {

    private String runId;
    private String description;
    private Instant executionTime;
    private Instant startDate;
    private Instant endDate;
    private String env;
    private String buildJobName;
    private String buildId;
    private String buildUrl;
    private String suiteName;
    private long duration;

}
