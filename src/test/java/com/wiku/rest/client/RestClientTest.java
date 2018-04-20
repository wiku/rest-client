/*
 * Copyright (c) 2017 Wiku. All rights reserved.
 */
package com.wiku.rest.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.Rule;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit.WireMockRule;

public class RestClientTest
{

    private static ObjectMapper jsonMapper = new ObjectMapper();
    private static String uri = "http://localhost:8080/api/v1/assets/1";
    private static TestClass requestObject = new TestClass(1, "aaa");
    private static TestClass responseObject = new TestClass(1, "bbb");
    


    @Test
    public void canGetCrudResourceFromRestClient()
    {
        RestClient client = new RestClient();
        CrudResource<Asset> resource = client.getResource("http://localhost:8080/api/v1/assets", Asset.class);
        assertNotNull(resource);
    }

    @Test
    public void canPerformGetRequestAndParseResponse() throws RestClientException, ClientProtocolException, IOException
    {
        HttpGet request = mock(HttpGet.class);
        HttpRequestFactory requestFactory = mockRequestFactoryForGet(request);

        CloseableHttpClient httpClient = mock(CloseableHttpClient.class);
        CloseableHttpResponse response = mockResponse(responseObject);
        when(httpClient.execute(request)).thenReturn(response);

        RestClient client = new RestClient(httpClient, requestFactory);

        TestClass t = client.get(uri, TestClass.class);
        assertEquals(responseObject, t);
    }

    @Test
    public void canPerformDeleteRequestAndParseResponse()
            throws RestClientException, ClientProtocolException, IOException
    {
        HttpDelete request = mock(HttpDelete.class);
        HttpRequestFactory requestFactory = mockRequestFactoryForDelete(request);

        CloseableHttpClient httpClient = mock(CloseableHttpClient.class);
        CloseableHttpResponse response = mockResponse(responseObject);
        when(httpClient.execute(request)).thenReturn(response);

        RestClient client = new RestClient(httpClient, requestFactory);

        TestClass t = client.delete(uri, TestClass.class);
        assertEquals(responseObject, t);
    }

    @Test
    public void canPerformPostRequestAndParseResponse() throws RestClientException, ClientProtocolException, IOException
    {
        HttpPost request = mock(HttpPost.class);
        HttpRequestFactory requestFactory = mockRequestFactoryForPOST(uri, requestObject, request);
        CloseableHttpResponse response = mockResponse(responseObject);
        CloseableHttpClient httpClient = mockHttpClient(request, response);

        RestClient client = new RestClient(httpClient, requestFactory);

        TestClass t = client.post(uri, requestObject, TestClass.class);
        assertEquals(responseObject, t);
    }

    @Test
    public void canPerformPutRequestAndParseResponse() throws RestClientException, ClientProtocolException, IOException
    {
        HttpPut request = mock(HttpPut.class);
        HttpRequestFactory requestFactory = mockRequestFactoryForPut(uri, requestObject, request);
        CloseableHttpResponse response = mockResponse(responseObject);
        CloseableHttpClient httpClient = mockHttpClient(request, response);

        RestClient client = new RestClient(httpClient, requestFactory);

        TestClass t = client.put(uri, requestObject, TestClass.class);
        assertEquals(responseObject, t);
    }

    @Test(expected = RestClientException.class)
    public void throwsExceptionWhenCannotParseResponse()
            throws RestClientException, ClientProtocolException, IOException
    {
        HttpPut request = mock(HttpPut.class);
        HttpRequestFactory requestFactory = mockRequestFactoryForPut(uri, requestObject, request);
        CloseableHttpResponse response = mockResponse("{ bad response }");
        CloseableHttpClient httpClient = mockHttpClient(request, response);

        RestClient client = new RestClient(httpClient, requestFactory);

        client.put(uri, requestObject, TestClass.class);
    }

    @Test(expected = RestClientException.class)
    public void throwsExceptionWhenResponseContainsNoEntity()
            throws RestClientException, ClientProtocolException, IOException
    {
        HttpPut request = mock(HttpPut.class);
        HttpRequestFactory requestFactory = mockRequestFactoryForPut(uri, requestObject, request);
        CloseableHttpResponse response = mockResponse(null, 200);
        CloseableHttpClient httpClient = mockHttpClient(request, response);

        RestClient client = new RestClient(httpClient, requestFactory);

        client.put(uri, requestObject, TestClass.class);
    }

    @Test(expected = RestClientException.class)
    public void throwsExceptionWhenResponseWithErrorCode()
            throws RestClientException, ClientProtocolException, IOException
    {
        HttpPut request = mock(HttpPut.class);
        HttpRequestFactory requestFactory = mockRequestFactoryForPut(uri, requestObject, request);
        CloseableHttpResponse response = mockResponse(mockEntity("error"), 404);
        CloseableHttpClient httpClient = mockHttpClient(request, response);

        RestClient client = new RestClient(httpClient, requestFactory);

        client.put(uri, requestObject, TestClass.class);
    }

    @Test(expected = RestClientException.class)
    public void throwsExceptionWhenClientThrowsIOException()
            throws RestClientException, ClientProtocolException, IOException
    {
        HttpPut request = mock(HttpPut.class);
        HttpRequestFactory requestFactory = mockRequestFactoryForPut(uri, requestObject, request);
        CloseableHttpClient httpClient = mock(CloseableHttpClient.class);
        when(httpClient.execute(request)).thenThrow(new IOException());

        RestClient client = new RestClient(httpClient, requestFactory);
        client.put(uri, requestObject, TestClass.class);
    }


    private CloseableHttpClient mockHttpClient( HttpUriRequest request, CloseableHttpResponse response )
            throws IOException, ClientProtocolException
    {
        CloseableHttpClient httpClient = mock(CloseableHttpClient.class);
        when(httpClient.execute(request)).thenReturn(response);
        return httpClient;
    }

    private CloseableHttpResponse mockResponse( Object responseObject ) throws JsonProcessingException, IOException
    {
        String responseString = jsonMapper.writeValueAsString(responseObject);
        return mockResponse(mockEntity(responseString), 200);
    }

    private CloseableHttpResponse mockResponse( HttpEntity entity, int statusCode ) throws IOException
    {
        CloseableHttpResponse response = mock(CloseableHttpResponse.class);

        when(response.getEntity()).thenReturn(entity);
        StatusLine statusLine = mock(StatusLine.class);
        when(response.getStatusLine()).thenReturn(statusLine);
        when(statusLine.getStatusCode()).thenReturn(statusCode);
        return response;
    }

    private HttpEntity mockEntity( String responseString ) throws IOException
    {
        HttpEntity entity = mock(HttpEntity.class);
        InputStream responseStream = new ByteArrayInputStream(responseString.getBytes());
        when(entity.getContent()).thenReturn(responseStream);
        return entity;
    }

    private HttpRequestFactory mockRequestFactoryForGet( HttpGet request )
    {
        HttpRequestFactory requestFactory = mock(HttpRequestFactory.class);
        when(requestFactory.newHttpGet(uri)).thenReturn(request);
        return requestFactory;
    }

    private HttpRequestFactory mockRequestFactoryForDelete( HttpDelete request )
    {
        HttpRequestFactory requestFactory = mock(HttpRequestFactory.class);
        when(requestFactory.newHttpDelete(uri)).thenReturn(request);
        return requestFactory;
    }

    private HttpRequestFactory mockRequestFactoryForPOST( String uri, TestClass requestObject, HttpPost request )
            throws JsonProcessingException
    {
        HttpRequestFactory requestFactory = mock(HttpRequestFactory.class);
        String requestAsJsonString = jsonMapper.writeValueAsString(requestObject);
        when(requestFactory.newHttpPost(uri, requestAsJsonString)).thenReturn(request);
        return requestFactory;
    }

    private HttpRequestFactory mockRequestFactoryForPut( String uri, TestClass requestObject, HttpPut request )
            throws JsonProcessingException
    {
        HttpRequestFactory requestFactory = mock(HttpRequestFactory.class);
        String requestAsJsonString = jsonMapper.writeValueAsString(requestObject);
        when(requestFactory.newHttpPut(uri, requestAsJsonString)).thenReturn(request);
        return requestFactory;
    }

}
