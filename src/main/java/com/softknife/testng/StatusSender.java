package com.softknife.testng;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.restbusters.resource.GlobalResourceManager;
import com.restbusters.rest.client.RestClientHelper;
import com.restbusters.rest.model.HttpRestRequest;
import com.softknife.config.DemoTestConfig;
import com.softknife.resources.DemoTestConfigResourceProvider;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.invoke.MethodHandles;

public class StatusSender {

    private static final ObjectMapper objectMapper = GlobalResourceManager.getInstance().getObjectMapper();
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static DemoTestConfig config = DemoTestConfigResourceProvider.getInstance().getGlobalConfig();
    private static OkHttpClient okHttpClient = RestClientHelper.getInstance().buildBasicAuthClient(config.elasticUser(), config.elasticPass());

    public static void send(final String testResults){
        HttpRestRequest httpRestRequest = new HttpRestRequest("POST", config.elasticHost() + "/" + config.elasticApp());
        logger.info("elastic user {}, password {}", config.elasticUser(), config.elasticPass());
        httpRestRequest.setRequestBody(testResults);
        try {
            Response response = RestClientHelper.getInstance().executeRequest(okHttpClient, httpRestRequest);
            logger.info("Response from elastic code: {} \n body: ", response.code(), response.body());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
