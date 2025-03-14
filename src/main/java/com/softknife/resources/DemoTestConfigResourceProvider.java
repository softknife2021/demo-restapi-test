package com.softknife.resources;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.restbusters.data.templating.TemplateManager;
import com.restbusters.integraton.swagger.SwaggerManager;
import com.restbusters.integraton.swagger.model.SwaggerDescriptor;
import com.softknife.config.DemoTestConfig;
import com.softknife.resources.http.client.HttpClient;
import com.softknife.resources.http.client.HttpClientFactory;
import com.softknife.utils.CommonUtils;
import okhttp3.OkHttpClient;
import org.aeonbits.owner.ConfigFactory;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

/**
 * @author softknife on 10/15/18
 * @project qreasp
 */

public class DemoTestConfigResourceProvider {

    private static DemoTestConfigResourceProvider instance;
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private DemoTestConfig config;
    private List<SwaggerDescriptor> swaggerDescriptors = new ArrayList<SwaggerDescriptor>();
    private TemplateManager templateManager;
    private String[] extension;
    private ObjectMapper mapper;
    private List<HttpClient> httpClients;
    // Flag to track if init has been executed
    private boolean isInitialized = false;


    private DemoTestConfigResourceProvider(){
        this.config = ConfigFactory.create(DemoTestConfig.class, System.getProperties(), System.getenv());
        this.extension = config.templateExtension();
    }

    public static synchronized DemoTestConfigResourceProvider getInstance(){
        if (instance == null) {
            synchronized (CommonUtils.class) {
                if (instance == null) {
                    instance = new DemoTestConfigResourceProvider();;
                }
            }
        }
        return instance;
    }


    // Init method that can only be executed once
    public void intiResources() {
        synchronized (this) {
            if (isInitialized) {
                logger.info("Init method has already been executed.");
                return;
            }

            // Perform initialization logic here
            logger.info("Initializing resources");
            prepareSwaggerDescriptor();
            prepareTemplateManager();
            prepareMapper();
            prepareHttpClients();
            isInitialized = true;
        }
    }

    public DemoTestConfig getGlobalConfig() {
        return config;
    }

    private void prepareMapper(){
        this.mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    private void prepareSwaggerDescriptor(){
        if(CollectionUtils.isNotEmpty(config.swaggerUrls())){
            logger.info("Start processing swagger urls");
            for(String url : config.swaggerUrls()){
                this.swaggerDescriptors.add(SwaggerManager.getInstance().getSwaggerDescriptor(url));
            }
        }
        else {
            logger.warn("Swagger Descriptor processor is invoked but url list is empty");
        }
    }

    private void prepareHttpClients(){
        // Create HttpClient instances
        try {
            this.httpClients = HttpClientFactory.createClientsFromConfig(CommonUtils.getInstance().readResourceFile("config/client-config.json"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<SwaggerDescriptor> getSwaggerDescriptors() {
        return swaggerDescriptors;
    }

    private void prepareTemplateManager(){
        this.templateManager = new TemplateManager("src/test/resources/payload/template", extension, true, ";", "=");
    }

    public TemplateManager getTemplateManager(){
        return this.templateManager;
    }

    public ObjectMapper getMapper(){
        return this.mapper;
    }

    public OkHttpClient findHttpClientByName(String name){
        for (HttpClient httpClient : httpClients) {
            if(httpClient.getConfig().getName().equals(name)){
                return httpClient.getClient();
            }
        }
        return null;
    }


}

