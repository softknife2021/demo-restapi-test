package com.softknife.testng.dataprovider;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.softknife.resources.ConfigProvider;
import com.softknife.utils.CommonUtils;
import org.testng.annotations.DataProvider;

import java.io.IOException;
import java.util.Map;

/**
 * @author amatsaylo on 3/21/25
 * @project demo-restapi-test
 */
public class JsonDataProvider {

    ObjectMapper objectMapper = ConfigProvider.getInstance().getMapper();

    @DataProvider(name = "jsonData")
    public Object[][] provideData() throws IOException {

        // Read JSON file and convert it to a List of Maps
        Map<String, String>[] data = objectMapper.readValue(CommonUtils.getInstance().readResourceFile("data/testdata.json"), Map[].class);

        // Convert the List of Maps to a 2D Object array
        Object[][] testData = new Object[data.length][1];
        for (int i = 0; i < data.length; i++) {
            testData[i][0] = data[i];
        }
        return testData;
    }

}
