package com.softknife.testng;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.softknife.config.DemoTestConfig;
import com.restbusters.resource.GlobalResourceManager;
import com.softknife.resources.DemoTestConfigResourceProvider;
import com.restbusters.rest.client.RestClientHelper;
import com.restbusters.rest.model.HttpRestRequest;
import com.softknife.testng.model.TestCaseStatus;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.invoke.MethodHandles;

public class StatusSender {

    private static final ObjectMapper objectMapper = GlobalResourceManager.getInstance().getObjectMapper();
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static DemoTestConfig config = DemoTestConfigResourceProvider.getInstance().getGlobalConfig();
    private static OkHttpClient okHttpClient = RestClientHelper.getInstance().buildBasicAuthClient(config.elasticUser(), config.elasticPass());

    public static void send(final TestCaseStatus testCaseStatus){
        HttpRestRequest httpRestRequest = new HttpRestRequest("POST", config.elasticHost() + config.elasticApp());
        logger.info("elastic user {}, password {}", config.elasticUser(), config.elasticPass());
        try {
            httpRestRequest.setRequestBody(objectMapper.writeValueAsString(testCaseStatus));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        try {
            RestClientHelper.getInstance().executeRequest(okHttpClient, httpRestRequest);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
