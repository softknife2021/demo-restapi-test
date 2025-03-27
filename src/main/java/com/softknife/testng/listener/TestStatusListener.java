package com.softknife.testng.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.lang.invoke.MethodHandles;

/**
 * @author amatsaylo on 3/26/25
 * @project demo-restapi-test
 */

public class TestStatusListener implements ITestListener {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());


    @Override
    public void onTestStart(ITestResult result) {
       logger.info("[STARTED] Test: " + result.getName());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        logger.info("[PASSED] Test: " + result.getName());
    }

    @Override
    public void onTestFailure(ITestResult result) {
        logger.info("[FAILED] Test: " + result.getName());
        if (result.getThrowable() != null) {
            logger.warn("Failure Reason: " + result.getThrowable().getMessage());
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        logger.warn("[SKIPPED] Test: " + result.getName());
    }
}