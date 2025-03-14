package com.softknife.testng.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.softknife.config.TestConfig;
import com.softknife.resources.ConfigProvider;
import com.softknife.testng.StatusSender;
import com.softknife.testng.model.ITestStatus;
import com.softknife.testng.model.TestExecResult;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.lang.invoke.MethodHandles;
import java.time.LocalDateTime;

public class ElasticSearchListener implements ITestListener {

    private TestExecResult testExecResult;
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private TestConfig config = ConfigProvider.getInstance().getGlobalConfig();
    private ObjectMapper mapper =  ConfigProvider.getInstance().getMapper();

    public void onTestStart(ITestResult iTestResult) {
        logger.info("Test started:{}", iTestResult.getMethod().getMethodName());
        this.testExecResult = new TestExecResult();

    }


    public void onTestSuccess(ITestResult iTestResult) {
        this.sendStatus(iTestResult, ITestStatus.PASS);
    }

    public void onTestFailure(ITestResult iTestResult) {
        this.sendStatus(iTestResult,ITestStatus.FAIL);
    }

    public void onTestSkipped(ITestResult iTestResult) {
        this.sendStatus(iTestResult,ITestStatus.SKIPPED);
    }

    public void onTestFailedButWithinSuccessPercentage(ITestResult iTestResult) {
        //skip
    }

    public void onStart(ITestContext iTestContext) {
        //skip
    }

    public void onFinish(ITestContext iTestContext) {
        logger.info("Test Finished: {}", iTestContext.getName());
    }

    private void sendStatus(ITestResult iTestResult, ITestStatus status){
        if(config.sendResultElastic()){
            this.testExecResult.setGroup(StringUtils.join(iTestResult.getMethod().getGroups(), ","));
            this.testExecResult.setTestClass(iTestResult.getTestClass().getName());
            this.testExecResult.setDescription(iTestResult.getMethod().getDescription());
            this.testExecResult.setStatus(status);
            this.testExecResult.setTestName(iTestResult.getTestName());
            this.testExecResult.setExecutionTime(LocalDateTime.now().toString());
            this.testExecResult.setTestName(iTestResult.getName());
            this.testExecResult.setEnv(ConfigProvider.getInstance().getGlobalConfig().env());
            if(iTestResult.getTestContext().getIncludedGroups().length > 0){
                this.testExecResult.setGroup( StringUtils.join(iTestResult.getTestContext().getIncludedGroups(), ' '));
            }
            if(iTestResult.getParameters().length > 0){
                this.testExecResult.setParameters(iTestResult.getParameters()[0].toString());
            }
            if(status.equals(ITestStatus.FAIL)){
                this.testExecResult.setError(iTestResult.getThrowable().getLocalizedMessage());
            }
            if(StringUtils.isNotBlank(config.buildId())){
                this.testExecResult.setBuildUrl(config.buildUrl());
                this.testExecResult.setBuildJobName(config.buildJobName());
                this.testExecResult.setBuildId(config.buildId());
            }
            try {
                StatusSender.send(this.mapper.writeValueAsString(testExecResult));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        else {
            logger.info("Elastic test case result sends it turned off");
        }
    }


}
