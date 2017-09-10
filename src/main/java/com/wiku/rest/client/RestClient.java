/*
 * Copyright (c) 2017 Wiku. All rights reserved.
 */
package com.wiku.rest.client;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RestClient
{

    private static ObjectMapper jsonMapper = new ObjectMapper();
    private final CloseableHttpClient client;
    private final HttpRequestFactory requestFactory;

    public RestClient()
    {
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        cm.setMaxTotal(8);

        requestFactory = new HttpRequestFactory();
        client = HttpClients.custom().setConnectionManager(cm).build();
    }

    public <RESP> RESP get( String uri, Class<RESP> responseClass ) throws RestClientException
    {
        HttpUriRequest getRequest = requestFactory.newHttpGet(uri);
        return sendRequestAndGetResponse(getRequest, responseClass);
    }
    

    public <REQ, RESP> RESP post( String uri, REQ reqest, Class<RESP> responseClass ) throws RestClientException
    {
        HttpUriRequest postRequest = requestFactory.newHttpPost(uri, getJsonFromRequestObject(reqest));
        return sendRequestAndGetResponse(postRequest, responseClass);
    }

    public <REQ, RESP> RESP put( String uri, REQ reqest, Class<RESP> responseClass ) throws RestClientException
    {
        HttpUriRequest putRequest = requestFactory.newHttpPut(uri, getJsonFromRequestObject(reqest));
        return sendRequestAndGetResponse(putRequest, responseClass);
    }

    public <REQ, RESP> RESP delete( String uri, Class<RESP> responseClass ) throws RestClientException
    {
        HttpUriRequest deleteRequest = requestFactory.newHttpDelete(uri);
        return sendRequestAndGetResponse(deleteRequest, responseClass);
    }

    public <RESOURCE> CrudResource<RESOURCE> getResource( String url, Class<RESOURCE> resourceClass )
    {
        return new CrudResource<RESOURCE>(this, url, resourceClass);
    }

    private <RESP> RESP sendRequestAndGetResponse( HttpUriRequest request, Class<RESP> responseClass )
            throws RestClientException
    {
        CloseableHttpResponse response = sendRequest(request);
        checkStatusCode(response);
        return getEntityFromResponse(response, responseClass);
    }

    private CloseableHttpResponse sendRequest( HttpUriRequest request ) throws RestClientException
    {
        try
        {
            return client.execute(request);
        }
        catch( IOException e )
        {
            throw new RestClientException("Failed to execute request " + request, e);
        }
    }

    private <REQ> String getJsonFromRequestObject( REQ reqest ) throws RestClientException
    {
        try
        {
            return jsonMapper.writeValueAsString(reqest);
        }
        catch( JsonProcessingException e )
        {
            throw new RestClientException("Failed to convert object to json message", e);
        }
    }

    private <RESP> RESP getEntityFromResponse( CloseableHttpResponse response, Class<RESP> resourceClass )
            throws RestClientException
    {
        HttpEntity entity = response.getEntity();
        if( entity == null )
        {
            throw new RestClientException("Response does not contain any entities");
        }

        try
        {
            return jsonMapper.readValue(entity.getContent(), resourceClass);
        }
        catch( UnsupportedOperationException | IOException e )
        {
            throw new RestClientException("Failed to create response object", e);
        }
    }

    private void checkStatusCode( CloseableHttpResponse response ) throws RestClientException
    {
        if( response.getStatusLine().getStatusCode() != 200 )
        {
            throw new RestClientException("Failed : HTTP error code : " + response.getStatusLine().getStatusCode());
        }
    }

}
