package com.softknife.testng.listener;

/**
 * @author amatsaylo on 2/24/25
 * @project demo-restapi-test
 */

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.softknife.config.TestConfig;
import com.softknife.resources.ConfigProvider;
import com.softknife.testng.StatusSender;
import com.softknife.testng.model.ITestStatus;
import com.softknife.testng.model.TestExecResult;
import com.softknife.testng.model.TestSuiteStatus;
import com.softknife.utils.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.IReporter;
import org.testng.ISuite;
import org.testng.ITestResult;
import org.testng.xml.XmlSuite;

import java.lang.invoke.MethodHandles;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;

public class CustomTestReporter implements IReporter {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private TestConfig config = ConfigProvider.getInstance().getGlobalConfig();
    private ObjectMapper mapper = null;
    private CommonUtils commonUtils = CommonUtils.getInstance();
    private String runId = null;

    @Override
    public void generateReport(List<XmlSuite> xmlSuites, List<ISuite> suites, String outputDirectory) {
        this.mapper = ConfigProvider.getInstance().getMapper();
        runId = commonUtils.generateRunId();
        String runDesc = "Run description can be set as env var";
        String execStartTime = LocalDateTime.now().toString();
        for (ISuite suite : suites) {
            logger.info("Processing report for suite: {}", suite.getName());
            TestSuiteStatus tss = new TestSuiteStatus();
            suite.getResults().forEach((key, result) -> {
                tss.setRunId(runId);
                tss.setDescription(runDesc);
                tss.setEnv(config.env());
                tss.setExecutionTime(LocalDateTime.now().toString());
                tss.setStartDate(result.getTestContext().getStartDate().toString());
                tss.setStartDate(result.getTestContext().getEndDate().toString());
                tss.setSuiteName(result.getTestContext().getName());
                tss.setTestPassed(result.getTestContext().getPassedTests().size());
                tss.setTestFailed(result.getTestContext().getFailedTests().size());
                tss.setTestSkipped(result.getTestContext().getSkippedTests().size());
                tss.setIncludedGroups(result.getTestContext().getIncludedGroups().toString());
                try {
                    logger.info(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(tss));
                    StatusSender.send(this.mapper.writeValueAsString(tss), config.elasticAppSuites());
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
                suite.getResults().forEach((suiteName, iSuiteResult) -> {
                    logger.info("Processing report for testcase: {}", result.getTestContext().getName());
                    if (iSuiteResult.getTestContext().getPassedTests().size() > 0) {
                        Iterator<ITestResult> trIterator = iSuiteResult.getTestContext().getPassedTests().getAllResults().iterator();
                        this.setTestCaseStatus(trIterator, ITestStatus.PASS);

                    }
                    if (iSuiteResult.getTestContext().getFailedTests().size() > 0) {
                        Iterator<ITestResult> trIterator = iSuiteResult.getTestContext().getFailedTests().getAllResults().iterator();
                        this.setTestCaseStatus(trIterator, ITestStatus.FAIL);

                    }
                    if (iSuiteResult.getTestContext().getSkippedTests().size() > 0) {
                        Iterator<ITestResult> trIterator = iSuiteResult.getTestContext().getFailedTests().getAllResults().iterator();
                        this.setTestCaseStatus(trIterator, ITestStatus.SKIPPED);

                    }
                });

            });
        }
    }

    //iterates over list of test results and sends them to elastic
    private void setTestCaseStatus(Iterator<ITestResult> trIterator, ITestStatus iTestStatus) {
        TestExecResult tcs = null;
        while (trIterator.hasNext()) {
            tcs = new TestExecResult();
            tcs.setRunId(runId);
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
            try {
                StatusSender.send(this.mapper.writeValueAsString(tcs), config.elasticAppTestCases());
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
    }
}
