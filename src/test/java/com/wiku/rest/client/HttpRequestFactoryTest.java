/*
 * Copyright (c) 2017 Wiku. All rights reserved.
 */
package com.wiku.rest.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.junit.Test;

import com.wiku.rest.client.HttpRequestFactory;

public class HttpRequestFactoryTest
{

    private static final String ENTITY_STRING = "entity";

    private final String URI = "http://localhost:8080/api/v1/assets";

    private HttpRequestFactory factory = new HttpRequestFactory();

    @Test
    public void canCreateHttpGet()
    {
        HttpGet httpGet = factory.newHttpGet(URI);
        assertNotNull(httpGet);
        assertEquals(URI, httpGet.getURI().toString());
    }

    @Test
    public void canCreateHttpPost() throws IOException
    {
        HttpPost post = factory.newHttpPost(URI, ENTITY_STRING);
        assertEquals(URI, post.getURI().toString());
        assertEquals(ENTITY_STRING, IOUtils.readLines(getContent(post), Charset.defaultCharset()).get(0));
    }

    @Test
    public void canCreateHttpPut() throws IOException
    {
        HttpPut post = factory.newHttpPut(URI, ENTITY_STRING);
        assertEquals(URI, post.getURI().toString());
        assertEquals(ENTITY_STRING, IOUtils.readLines(getContent(post), Charset.defaultCharset()).get(0));
    }

    @Test
    public void canCreateHttpDelete() throws IOException
    {
        HttpDelete delete = factory.newHttpDelete(URI);
        assertEquals(URI, delete.getURI().toString());
    }

    private InputStream getContent( HttpEntityEnclosingRequest request ) throws IOException
    {
        return request.getEntity().getContent();
    }

}
