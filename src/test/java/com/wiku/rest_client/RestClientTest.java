package com.wiku.rest_client;

import static org.junit.Assert.*;

import org.junit.Test;

public class RestClientTest
{
    
    @Test
    public void test()
    {
        RestClient client = new RestClient();
        CrudResource<Asset> resource = client.getResource("http://localhost:8080/api/v1/assets", Asset.class);
        assertNotNull(resource);
        
        
        
    }

}
