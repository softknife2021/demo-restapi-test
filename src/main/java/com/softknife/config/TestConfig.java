package com.softknife.config;

import org.aeonbits.owner.Config;

import java.util.List;

/**
 * @author Sasha Matsaylo on 10/18/21
 * @project demo-restapi-test
 */

@Config.LoadPolicy(Config.LoadType.MERGE)
@Config.Sources({"file:/opt/config/deploy-config.properties",
        "classpath:config/automation-config.properties"})
public interface TestConfig extends Config {

    @DefaultValue("${DEMO_DEPLOY_ENV}")
    String env();

    @DefaultValue("${java.home}")
    String javaHome();

    @Config.DefaultValue("${user.home}")
    String userHome();

    @DefaultValue("${DEMO_DEPLOY_ENV}")
    String deployEnv();

    @Key("deploy.request.body.trigger")
    String triggerRequestBody(String manifestId, String submittedBy);

    @Key("deploy.request.url.path")
    String triggerDeployUrl();

    @DefaultValue("${ELASTIC_SEND_TEST_RESULTS}")
    boolean sendResultElastic();

    @DefaultValue("${ELASTIC_HOST}")
    String elasticHost();

    @DefaultValue("${ELASTIC_APP}")
    String elasticApp();

    @DefaultValue("${ELASTIC_USER}")
    String elasticUser();

    @DefaultValue("${ELASTIC_PASS}")
    String elasticPass();

    @DefaultValue("${JOB_NAME}")
    String buildJobName();

    @DefaultValue("${BUILD_ID}")
    String buildId();

    @DefaultValue("${BUILD_URL}")
    String buildUrl();

    @TokenizerClass(CommaTokenizer.class)
    @Key("swagger.${env}.service.url")
    public List<String> swaggerUrls();

    @Key("template.extension")
    public String[] templateExtension();

    @Key("test.equal.digit.assertion")
    public String messageAssertNotEqual(int actualValue, int expectedValue);

    @DefaultValue("${PASS_KEY}")
    String passKey();

    @Key("elastic.app.suite")
    String elasticAppSuites();

    @Key("elastic.app.testcase")
    String elasticAppTestCases();
}
