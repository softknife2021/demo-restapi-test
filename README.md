# Project Title

This is a demo project that gives you a quick jump start with service-based automation (work in progress). 
I have not described all the capabilities of this framework, but I will be adding them as time allows. 
Feel free to provide your use cases, if you have any. If there is support for them already, I will update 
the readme file and provide code examples, but if not, I will implement them if time allows.

## Description

This is java based project using solid industry prove testNG framework. Project uses public petstore restapi
to demonstrate its ability to interact with web service interfaces, running over http api such as
rest api graphQL.

Supports:
* Integration with test rail test case management system setup
  * Uncomment this listener in testNG xml file.
 ```<!--<listener class-name="com.restbusters.testrails.integration.services.testrail.TestRailListener"/>-->```
  * Set the following env var
  `RAILS_USER, RAILS_PASS, RAILS_HOST, RAILS_APP, RAILS_RUN_NAME`
  * Annotate your tests with test caseId.
  ```@RailsMetaData(testCaseId = 122)```
* Support template based payload
  * Payload defined in resource folder default
    `resource.payload`
  * Configured via properties resources/config
    ```template.extension=ftl,json```
  * Setup template from test class
    ```
    @BeforeClass(alwaysRun = true)
     private void setUP(){
     this.provider = DemoTestConfigResourceProvider.getInstance();
     this.tm = provider.getTemplateManager();
    }
    ```
  * Usage from test using default values from file
    `String result = this.tm.processTemplateWithJsonInput("add-pet", "0.1");`
  * Usage from test using provided substitution values
    `String result = this.tm.processTemplateWithJsonInput("add-pet", "0.1", jsonPayloadSubs);`
* Supports Http Clients
  * Use this interface ```RestClientHelper.getInstance().``` to build your own client or retrieve oath 2.0 bearer token
  * Supported client 
    `Basic, Bearer, NoAuth,Trusted`
  * Supports OAuth2 token retrieval 
* Supports configuration properties for multiple env
  ```
  #DEV
  swagger.dev.service.url=https://petstore.swagger.io/v2/swagger.json,https://petstore3.swagger.io/api/v3/openapi.json
  #INT
  swagger.int.service.url=https://petstore.swagger.io/v2/swagger.json,https://petstore3.swagger.io/api/v3/openapi.json
  ```
* Support Swagger based configuration, no need to have properties for your api to store metadata in case swagger definition is available
  * Add your url int swagger property `swagger.int.service.url`
  * From your test class
    ```    @BeforeClass(alwaysRun = true)
    private void setUP(){
        this.provider = DemoTestConfigResourceProvider.getInstance();
    }
    ```
    * From the test call your swagger endpoint metadata by api title and summary or description
      ```
      SwaggerApiResource sar = SwaggerApiResourceFilter.fetchApiResource(this.provider.getSwaggerDescriptors(),
                "Swagger Petstore", "addPet");
      ```
    * Use them this way (I will consider a conversion method for future to covert from Swagger object to HttpRequest)
      ```      private void add_pet() throws RecordNotFound, IOException, TemplateException {
        String result = this.tm.processTemplateWithJsonInput("add-pet", "0.1");

        SwaggerApiResource sar = SwaggerApiResourceFilter.fetchApiResource(this.provider.getSwaggerDescriptors(),
                "Swagger Petstore", "addPet");
        HttpRestRequest httpRestRequest = new HttpRestRequest(sar.getHttpMethod(), sar.getResourcePath());
        httpRestRequest.setRequestBody(result);
        Response response = RestClientHelper.getInstance().executeRequest(this.okHttpClient, httpRestRequest);
        Assert.assertEquals(response.code(), 200);
        String body = response.body().string();
        this.petId = JsonPath.read(body, "$.id");
        Assert.assertTrue(Integer.class.isInstance(this.petId), "Expected pet id to be a valid number");
      }
      ```
## Getting Started
* Setup Java 11
* Setup gradle
* I recommend using this tool to manage you jvm tools [sdk man](https://www.sdkman.io)

### Dependencies
* Run with Java and gradle properly set in any OS
* Project tested with Java 8 and 11

### Installing

* git clone <this project>

### Executing program
* To execute framework run the following command from the root of the project
```
 ./gradlew clean build  demoTest -DDEMO_DEPLOY_ENV=int -DTEST_XML=demo-restapi-test
```

## Help

Any advice for common problems or issues.
```
WIP
```

## Authors

Contributors names and contact info

ex. [@Softknife](https://www.linkedin.com/in/alexander-matsaylo-3282649/)

## Version History

* 0.1
    * Initial Release

## License

This project is not licensed