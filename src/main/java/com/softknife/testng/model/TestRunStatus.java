package com.softknife.testng.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

/**
 * @author amatsaylo on 2/24/25
 * @project demo-restapi-test
 */
@Getter
@Setter
public class TestRunStatus {

    private UUID runId;
    private String description;
    private List<TestSuiteStatus> testSuiteStatuses;
    private String executionStartTime;
}
