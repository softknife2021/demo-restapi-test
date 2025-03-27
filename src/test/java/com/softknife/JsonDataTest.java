package com.softknife;

import com.softknife.testng.dataprovider.JsonDataProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.Test;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

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

        int size = 7; // Number of elements in the list
        List<Integer> randomList = new ArrayList<>();
        Random random = new Random();

        for (int i = 1; i < size; i++) {
            // Generates random number between 1 and 3 (inclusive)
            int randomNumber = random.nextInt(3) + 1;
            randomList.add(randomNumber);
        }
        for(Integer integer : randomList){
            if(integer == 1){
                Assert.assertTrue(true, "Result is expected");
            }
            if(integer == 2){
                Assert.assertTrue(false, "Result is not expected");
            }
            if(integer == 3){
                throw new SkipException("Skipping this exception");
            }
        }
        Assert.assertTrue(true, "Result is expected");
        // Your test logic here
    }
}
