package com.softknife.resources.http.client;

/**
 * @author amatsaylo on 3/13/25
 * @project demo-restapi-test
 */
import lombok.Data;
import okhttp3.OkHttpClient;

@Data
public class HttpClient {
    private ClientConfig config;
    private OkHttpClient client;

    public HttpClient(ClientConfig config, OkHttpClient client) {
        this.config = config;
        this.client = client;
    }
}