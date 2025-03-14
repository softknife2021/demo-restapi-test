package com.softknife.testng.listener;

/**
 * @author amatsaylo on 2/24/25
 * @project demo-restapi-test
 */

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.softknife.config.DemoTestConfig;
import com.softknife.resources.DemoTestConfigResourceProvider;
import com.softknife.testng.StatusSender;
import com.softknife.testng.model.ITestStatus;
import com.softknife.testng.model.TestExecResult;
import com.softknife.testng.model.TestRunStatus;
import com.softknife.testng.model.TestSuiteStatus;
import org.testng.IReporter;
import org.testng.ISuite;
import org.testng.ITestResult;
import org.testng.xml.XmlSuite;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class CustomRunReporter implements IReporter {

    TestRunStatus testRunStatus = new TestRunStatus();
    List<TestSuiteStatus> testSuiteStatusList = new ArrayList<>();
    private DemoTestConfig config = DemoTestConfigResourceProvider.getInstance().getGlobalConfig();
    private ObjectMapper mapper = DemoTestConfigResourceProvider.getInstance().getMapper();

    @Override
    public void generateReport(List<XmlSuite> xmlSuites, List<ISuite> suites, String outputDirectory) {
        System.out.println("Custom TestNG Report:");
        testRunStatus.setRunId(UUID.randomUUID());
        testRunStatus.setDescription("description should be tight to test pipeline");
        testRunStatus.setExecutionStartTime(LocalDateTime.now().toString());
        testRunStatus.setEnv(config.env());

        for (ISuite suite : suites) {
            System.out.println("Suite Name: " + suite.getName());
            TestSuiteStatus tss = new TestSuiteStatus();

            suite.getResults().forEach((key, result) -> {
                tss.setStartDate(result.getTestContext().getStartDate().toString());
                tss.setStartDate(result.getTestContext().getEndDate().toString());
                tss.setSuiteName(result.getTestContext().getName());
                tss.setTestPassed(result.getTestContext().getPassedTests().size());
                tss.setTestFailed(result.getTestContext().getFailedTests().size());
                tss.setTestSkipped(result.getTestContext().getSkippedTests().size());
                List<TestExecResult> testExecResults = new ArrayList<>();
                suite.getResults().forEach((suiteName, iSuiteResult) -> {
                    if (iSuiteResult.getTestContext().getPassedTests().size() > 0) {
                        Iterator<ITestResult> trIterator = iSuiteResult.getTestContext().getPassedTests().getAllResults().iterator();
                        this.setTestCaseStatus(testExecResults, trIterator, ITestStatus.PASS);

                    }
                    if (iSuiteResult.getTestContext().getFailedTests().size() > 0) {
                        Iterator<ITestResult> trIterator = iSuiteResult.getTestContext().getFailedTests().getAllResults().iterator();
                        this.setTestCaseStatus(testExecResults, trIterator, ITestStatus.FAIL);

                    }
                    if (iSuiteResult.getTestContext().getSkippedTests().size() > 0) {
                        Iterator<ITestResult> trIterator = iSuiteResult.getTestContext().getFailedTests().getAllResults().iterator();
                        this.setTestCaseStatus(testExecResults, trIterator, ITestStatus.SKIPPED);

                    }
                });
                tss.setTestExecResults(testExecResults);

            });
            this.testSuiteStatusList.add(tss);
        }
        this.testRunStatus.setTestSuiteStatuses(this.testSuiteStatusList);
        try {
            StatusSender.send(this.mapper.writeValueAsString(testRunStatus));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
    private List<TestExecResult> setTestCaseStatus(List<TestExecResult> testExecResults, Iterator<ITestResult> trIterator, ITestStatus iTestStatus) {
        while (trIterator.hasNext()) {
            TestExecResult tcs = new TestExecResult();
            ITestResult iTestResult = trIterator.next();
            tcs.setDescription(iTestResult.getMethod().getDescription());
            tcs.setTestName(iTestResult.getName());
            tcs.setEnv(this.config.env());
            tcs.setStartDate(iTestResult.getTestContext().getStartDate().toString());
            tcs.setEndDate(iTestResult.getTestContext().getEndDate().toString());
            tcs.setGroup(iTestResult.getTestContext().getIncludedGroups().toString());
            tcs.setExecutionTime(LocalDateTime.now().toString());
            tcs.setStatus(iTestStatus);
            if (iTestStatus.equals(ITestStatus.FAIL)) {
                tcs.setError(iTestResult.getThrowable().getLocalizedMessage());
            }
            if (iTestStatus.equals(ITestStatus.SKIPPED)) {
                tcs.setError(iTestResult.getSkipCausedBy().toString());
            }
            if (iTestResult.getTestContext().getIncludedGroups().length > 0) {
                tcs.setGroup(org.apache.commons.lang.StringUtils.join(iTestResult.getTestContext().getIncludedGroups(), ' '));
            }
            testExecResults.add(tcs);
        }
        return testExecResults;
    }
}
