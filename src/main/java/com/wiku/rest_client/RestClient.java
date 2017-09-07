package com.wiku.rest_client;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RestClient
{
    private final CloseableHttpClient client;

    public RestClient()
    {
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        cm.setMaxTotal(100);

        client = HttpClients.custom()
                .setConnectionManager(cm)
                .build();
    }

    public <T> CrudResource<T> getResource( String url, Class<T> resourceClass )
    {
        return new CrudResource<T>(client, url, resourceClass);
    }

}
