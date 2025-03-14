package com.softknife.resources.http.client;

import lombok.Getter;
import lombok.Setter;

/**
 * @author amatsaylo on 3/13/25
 * @project demo-restapi-test
 */
@Setter
@Getter
public class ClientConfig {

    private String name;
    private String description;
    private String userName;
    private String password;
    private Boolean isEncrypted;
    private String apiKey;
    private ClientType clientType;
    private int timeout;

}
