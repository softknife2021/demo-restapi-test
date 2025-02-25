package com.softknife.testng.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author amatsaylo on 2/24/25
 * @project demo-restapi-test
 */
@Setter
@Getter
public class TestSuiteStatus {

    private String suiteName;
    private Integer testPassed;
    private Integer testFailed;
    private Integer testSkipped;
    private String startDate;
    private String getEndDate;
    private List<TestExecResult> TestExecResults;

}
