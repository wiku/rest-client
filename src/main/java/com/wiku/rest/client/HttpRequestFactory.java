/*
 * Copyright (c) 2017 Wiku. All rights reserved.
 */
package com.wiku.rest.client;

import java.nio.charset.Charset;

import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;

public class HttpRequestFactory
{

    public HttpGet newHttpGet( String uri )
    {
        return new HttpGet(uri);
    }

    public HttpPost newHttpPost( String uri, String entityString )
    {
        HttpPost httpPost = new HttpPost(uri);
        httpPost.setEntity(createEntity(entityString));
        return httpPost;
    }

    public HttpPut newHttpPut( String uri, String entityString )
    {
        HttpPut httpPut = new HttpPut(uri);
        httpPut.setEntity(createEntity(entityString));
        return httpPut;
    }

    public HttpDelete newHttpDelete( String uri )
    {
        return new HttpDelete(uri);
    }

    private StringEntity createEntity( String entityString )
    {
        StringEntity input = new StringEntity(entityString, Charset.defaultCharset());
        input.setContentType("application/json");
        return input;
    }
}
