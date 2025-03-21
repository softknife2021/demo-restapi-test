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
import java.util.Arrays;
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
                tss.setIncludedGroups(Arrays.toString( result.getTestContext().getIncludedGroups()).replaceAll("^.|.$", ""));
                try {
                    logger.info(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(tss));
                    StatusSender.send(this.mapper.writeValueAsString(tss), config.elasticAppSuites());
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
                logger.info("Processing report for testcase: {}", result.getTestContext().getName());
                processTestResults(result.getTestContext().getPassedTests().getAllResults().iterator(), ITestStatus.PASS);
                processTestResults(result.getTestContext().getFailedTests().getAllResults().iterator(), ITestStatus.FAIL);
                processTestResults(result.getTestContext().getSkippedTests().getAllResults().iterator(), ITestStatus.SKIPPED);

            });
        }
    }


    private void processTestResults(Iterator<ITestResult> trIterator, ITestStatus iTestStatus) {
        while (trIterator.hasNext()) {
            ITestResult iTestResult = trIterator.next();
            logger.info("Processing report for testcase: {}", iTestResult.getName());
            TestExecResult tcs = createTestExecResult(iTestResult, iTestStatus);
            try {
                StatusSender.send(this.mapper.writeValueAsString(tcs), config.elasticAppTestCases());
            } catch (JsonProcessingException e) {
                logger.error("Failed to serialize or send TestExecResult", e);
            }
        }
    }

    //iterates over list of test results and sends them to elastic
    private TestExecResult createTestExecResult(ITestResult iTestResult, ITestStatus iTestStatus) {
        TestExecResult tcs = new TestExecResult();
        tcs.setRunId(runId);
        tcs.setDescription(iTestResult.getMethod().getDescription());
        tcs.setTestName(iTestResult.getName());
        tcs.setEnv(this.config.env());
        tcs.setStartDate(iTestResult.getTestContext().getStartDate().toString());
        tcs.setEndDate(iTestResult.getTestContext().getEndDate().toString());
        tcs.setExecutionTime(LocalDateTime.now().toString());
        tcs.setStatus(iTestStatus);
        tcs.setGroup(Arrays.toString( iTestResult.getTestContext().getIncludedGroups()).replaceAll("^.|.$", ""));

        if (iTestStatus.equals(ITestStatus.FAIL)) {
            tcs.setError(iTestResult.getThrowable() != null ? iTestResult.getThrowable().getLocalizedMessage() : "Unknown error");
        }
        if (iTestStatus.equals(ITestStatus.SKIPPED)) {
            tcs.setError(iTestResult.getSkipCausedBy() != null ? iTestResult.getSkipCausedBy().toString() : "Unknown skip reason");
        }
        if (iTestResult.getTestContext().getIncludedGroups().length > 0) {
            tcs.setGroup(String.join(" ", iTestResult.getTestContext().getIncludedGroups()));
        }

        return tcs;
    }
}
