package com.softknife;

import com.softknife.testng.dataprovider.JsonDataProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.lang.invoke.MethodHandles;
import java.util.Map;

/**
 * @author amatsaylo on 3/21/25
 * @project demo-restapi-test
 */
public class JsonDataTest {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());


    @Test(dataProvider = "jsonData", dataProviderClass = JsonDataProvider.class,
            groups = {"functional"}, description = "Demo Data provider")
    public void testLogin(Map<String, String> data) {
        String username = data.get("username");
        String password = data.get("password");

        logger.info("Username: {}", username);
        logger.info("Password: {}",  password);
        Assert.assertTrue(true, "Result is expected");
        // Your test logic here
    }
}
