package com.softknife.testng.model;

import lombok.Getter;
import lombok.Setter;


/**
 * @author amatsaylo on 2/24/25
 * @project demo-restapi-test
 */
@Setter
@Getter
public class TestSuiteStatus extends BaseCustomReport {

    private String suiteName;
    private Integer testPassed;
    private Integer testFailed;
    private Integer testSkipped;
    private String startDate;
    private String getEndDate;
    private String includedGroups;

}
