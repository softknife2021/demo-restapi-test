package com.softknife;

import com.jayway.jsonpath.JsonPath;
import com.restbusters.data.templating.TemplateManager;
import com.restbusters.exception.RecordNotFound;
import com.restbusters.integraton.swagger.SwaggerApiResourceFilter;
import com.restbusters.integraton.swagger.model.SwaggerApiResource;
import com.softknife.resources.ConfigProvider;
import com.restbusters.rest.client.RestClientHelper;
import com.restbusters.rest.model.HttpRestRequest;
import com.restbusters.testrails.integration.services.testrail.RailsMetaData;
import com.softknife.testng.listener.RetryAnalyzer;
import freemarker.template.TemplateException;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.HashMap;
import java.util.Map;

import static org.testng.Assert.assertEquals;

/**
 * @author Sasha Matsaylo on 9/12/21
 * @project demo-restapi-test
 */
public class RestApiDemoTest {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private ConfigProvider provider;
    private OkHttpClient okHttpClient;
    private TemplateManager tm;
    private int petId;

    @BeforeClass(alwaysRun = true)
    private void setUP() {
        logger.info("Setting up test class");
        this.provider = ConfigProvider.getInstance();
        this.provider.intiResources();
        this.okHttpClient = ConfigProvider.getInstance().findHttpClientByName("DemoBasic");
        this.tm = provider.getTemplateManager();
    }

    @Test(groups = {"smoke"}, description = "create pet should always pass")
    @RailsMetaData(testCaseId = 120)
    private void add_pet() throws RecordNotFound, IOException, TemplateException {
        String result = this.tm.processTemplateWithJsonInput("add-pet", "0.1");

        SwaggerApiResource sar = SwaggerApiResourceFilter.fetchApiResource(this.provider.getSwaggerDescriptors(),
                "Swagger Petstore", "addPet");
        HttpRestRequest httpRestRequest = new HttpRestRequest(sar.getHttpMethod(), sar.getResourcePath());
        httpRestRequest.setRequestBody(result);
        Response response = RestClientHelper.getInstance().executeRequest(this.okHttpClient, httpRestRequest);
        assertEquals(response.code(), 200);
        String body = response.body().string();
        this.petId = JsonPath.read(body, "$.id");
        Assert.assertTrue(Integer.class.isInstance(this.petId), "Expected pet id to be a valid number");
    }

    @Test(dependsOnMethods = "add_pet", description = "get by id should always pass functional", groups = {"functional"})
    @RailsMetaData(testCaseId = 121)
    private void find_pet_by_id_func() throws RecordNotFound, IOException {
        SwaggerApiResource sar = SwaggerApiResourceFilter.fetchApiResource(this.provider.getSwaggerDescriptors(),
                "Swagger Petstore", "getPetById");
        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("petId", String.valueOf(this.petId));
        HttpRestRequest httpRestRequest = new HttpRestRequest(sar.getHttpMethod(), sar.getResourcePath());
        httpRestRequest.setUrlParams(urlParams);
        Response response = RestClientHelper.getInstance().executeRequest(this.okHttpClient, httpRestRequest);
        assertEquals(response.code(), 200, "Expected successful response");
        int actualValue = JsonPath.read(response.body().string(), "$.id");
        this.provider.getGlobalConfig().messageAssertNotEqual(actualValue, this.petId);
    }

    @Test(dependsOnMethods = "add_pet", description = "get by id should always pass security", groups = {"security"})
    @RailsMetaData(testCaseId = 121)
    private void find_pet_by_id_sec() throws RecordNotFound, IOException {
        SwaggerApiResource sar = SwaggerApiResourceFilter.fetchApiResource(this.provider.getSwaggerDescriptors(),
                "Swagger Petstore", "getPetById");
        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("petId", String.valueOf(this.petId));
        HttpRestRequest httpRestRequest = new HttpRestRequest(sar.getHttpMethod(), sar.getResourcePath());
        httpRestRequest.setUrlParams(urlParams);
        Response response = RestClientHelper.getInstance().executeRequest(this.okHttpClient, httpRestRequest);
        assertEquals(response.code(), 200, "Expected successful response");
        int actualValue = JsonPath.read(response.body().string(), "$.id");
        this.provider.getGlobalConfig().messageAssertNotEqual(actualValue, this.petId);
    }

    @Test(dependsOnMethods = "add_pet", description = "functional get by id should always pass", groups = {"smoke"})
    @RailsMetaData(testCaseId = 121)
    private void find_pet_by_id() throws RecordNotFound, IOException {
        SwaggerApiResource sar = SwaggerApiResourceFilter.fetchApiResource(this.provider.getSwaggerDescriptors(),
                "Swagger Petstore", "getPetById");
        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("petId", String.valueOf(this.petId));
        HttpRestRequest httpRestRequest = new HttpRestRequest(sar.getHttpMethod(), sar.getResourcePath());
        httpRestRequest.setUrlParams(urlParams);
        Response response = RestClientHelper.getInstance().executeRequest(this.okHttpClient, httpRestRequest);
        assertEquals(response.code(), 200, "Expected successful response");
        int actualValue = JsonPath.read(response.body().string(), "$.id");
        this.provider.getGlobalConfig().messageAssertNotEqual(actualValue, this.petId);
    }

    @Test(description = "get by id should fail", groups = {"negative"})
    @RailsMetaData(testCaseId = 122)
    private void find_pet_by_id_fail() throws RecordNotFound, IOException {
        SwaggerApiResource sar = SwaggerApiResourceFilter.fetchApiResource(this.provider.getSwaggerDescriptors(),
                "Swagger Petstore", "getPetById");
        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("petId", String.valueOf(this.petId));
        HttpRestRequest httpRestRequest = new HttpRestRequest(sar.getHttpMethod(), sar.getResourcePath());
        httpRestRequest.setUrlParams(urlParams);
        Response response = RestClientHelper.getInstance().executeRequest(this.okHttpClient, httpRestRequest);
        int fakeExpected = 33;
        assertEquals(this.petId, fakeExpected, this.provider.getGlobalConfig().messageAssertNotEqual(this.petId, fakeExpected));
    }

    @Test(retryAnalyzer = RetryAnalyzer.class, dependsOnMethods = "add_pet", description = "this test should fail", groups = {"functional"})
    private void pet_should_fail() throws RecordNotFound, IOException {
        assertEquals(0, 1);
    }

    @Test(dependsOnMethods = "pet_should_fail", description = "this test should be skipped", groups = {"functional"})
    private void pet_should_be_skipped() throws RecordNotFound, IOException {
    }

}

