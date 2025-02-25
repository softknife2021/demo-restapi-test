package com.softknife.resources;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.softknife.config.DemoTestConfig;
import com.restbusters.data.templating.TemplateManager;
import com.restbusters.integraton.swagger.SwaggerManager;
import com.restbusters.integraton.swagger.model.SwaggerDescriptor;
import org.aeonbits.owner.ConfigFactory;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private DemoTestConfig config = ConfigFactory.create(DemoTestConfig.class, System.getProperties(), System.getenv());
    private List<SwaggerDescriptor> swaggerDescriptors = new ArrayList<SwaggerDescriptor>();
    private TemplateManager templateManager;
    private final String[] extension = config.templateExtension();
    private ObjectMapper mapper;


    private DemoTestConfigResourceProvider(){
        prepareSwaggerDescriptor();
        prepareTemplateManager();
        prepareMapper();
    }

    public static synchronized DemoTestConfigResourceProvider getInstance(){
        if(instance == null){
            instance = new DemoTestConfigResourceProvider();
        }
        return instance;
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


}

