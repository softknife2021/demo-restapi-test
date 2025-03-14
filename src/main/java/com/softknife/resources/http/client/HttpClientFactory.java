package com.softknife.resources.http.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.restbusters.rest.client.RestClientHelper;
import com.softknife.config.TestConfig;
import com.softknife.resources.ConfigProvider;
import com.softknife.utils.AESUtil;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

/**
 * @author amatsaylo on 3/13/25
 * @project demo-restapi-test
 */

public class HttpClientFactory {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private static ClientType basic = ClientType.BASIC;
    private static ClientType bearer = ClientType.BEARER;
    private static RestClientHelper rsh = RestClientHelper.getInstance();
    private static TestConfig config = ConfigProvider.getInstance().getGlobalConfig();

    public static List<HttpClient> createClientsFromConfig(String clientConfigList) throws IOException {
        // Read JSON configuration
        ObjectMapper objectMapper = new ObjectMapper();
        ClientConfigList configList = objectMapper.readValue(clientConfigList, ClientConfigList.class);

        // Create OkHttpClient instances
        List<HttpClient> httpClients = new ArrayList<>();
        for (ClientConfig clientConfig : configList.getClients()) {
            OkHttpClient client = null;
            if (bearer == ClientType.BEARER) {
                try {
                    client = RestClientHelper.getInstance().buildBearerClient(clientConfig.getApiKey());
                } catch (Exception e) {
                    logger.error("Failed to build bearer http client");
                    e.printStackTrace();
                }
            }
            if (basic == ClientType.BASIC) {
                try {
                    logger.info("pass code {}", config.passKey());
                    client = rsh.buildBasicAuthClient(clientConfig.getUserName(), decodePassword(clientConfig));
                } catch (Exception e) {
                    logger.error("Failed to build basic http client");
                    e.printStackTrace();
                }
            }

            if (client != null) {
                httpClients.add(new HttpClient(clientConfig, client));
            }
        }

        return httpClients;
    }

    private static String decodePassword(ClientConfig clientConfig) {
        if (clientConfig.getIsEncrypted() == true) {
            return AESUtil.decrypt(clientConfig.getPassword(), config.passKey());
        }
        return clientConfig.getPassword();

    }

    // Inner class to represent the root of the JSON configuration
    private static class ClientConfigList {
        private List<ClientConfig> clients;

        // Getter and Setter
        public List<ClientConfig> getClients() {
            return clients;
        }

        public void setClients(List<ClientConfig> clients) {
            this.clients = clients;
        }
    }
}
