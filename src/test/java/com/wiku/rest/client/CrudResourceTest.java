/*
 * Copyright (c) 2017 Wiku. All rights reserved.
 */
package com.wiku.rest.client;

import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(MockitoJUnitRunner.class)
public class CrudResourceTest
{

    private static final String URI = "http://localhost/api/test";
    private static final TestClass REQUEST_OBJECT = new TestClass(1, "a");
    private static final TestClass RESPONSE_OBJECT = new TestClass(2, "b");
    private static final Collection<TestClass> ALL_RESOURCES = Collections.singletonList(RESPONSE_OBJECT);
    private static ObjectMapper jsonMapper = new ObjectMapper();

    @Mock
    private RestClient restClient;

    private CrudResource<TestClass> crudResource;

    @Before
    public void before()
    {
        crudResource = new CrudResource<>(restClient, URI, TestClass.class);
    }

    @Test
    public void canReadAllResources() throws RestClientException, InstantiationException, IllegalAccessException
    {
        when(restClient.get(URI, getListClass())).thenReturn(ALL_RESOURCES);

        Collection<TestClass> allResources = crudResource.read();

        Assert.assertEquals(ALL_RESOURCES, allResources);
    }

    @Test
    public void canReadResourceById() throws RestClientException, InstantiationException, IllegalAccessException
    {
        when(restClient.get(URI + "/1", TestClass.class)).thenReturn(RESPONSE_OBJECT);

        TestClass returnedResource = crudResource.read(1);

        Assert.assertEquals(RESPONSE_OBJECT, returnedResource);
    }

    @Test
    public void canCreateResource() throws RestClientException, InstantiationException, IllegalAccessException
    {
        when(restClient.post(URI, REQUEST_OBJECT, TestClass.class)).thenReturn(RESPONSE_OBJECT);

        TestClass returnedResource = crudResource.create(REQUEST_OBJECT);

        Assert.assertEquals(RESPONSE_OBJECT, returnedResource);
    }

    @Test
    public void canUpdateResource() throws RestClientException, InstantiationException, IllegalAccessException
    {
        when(restClient.put(URI + "/2", REQUEST_OBJECT, TestClass.class)).thenReturn(RESPONSE_OBJECT);

        TestClass returnedResource = crudResource.update(2, REQUEST_OBJECT);

        Assert.assertEquals(RESPONSE_OBJECT, returnedResource);
    }

    @Test
    public void canDeleteResource() throws RestClientException, InstantiationException, IllegalAccessException
    {
        when(restClient.delete(URI + "/2", TestClass.class)).thenReturn(RESPONSE_OBJECT);

        TestClass returnedResource = crudResource.delete(2);

        Assert.assertEquals(RESPONSE_OBJECT, returnedResource);
    }

    @SuppressWarnings("unchecked")
    private Class<Collection<TestClass>> getListClass()
    {
        Class<?> rawClass = jsonMapper.getTypeFactory()
                .constructCollectionType(List.class, TestClass.class)
                .getRawClass();
        Class<Collection<TestClass>> collectionClass = (Class<Collection<TestClass>>)rawClass;
        return collectionClass;
    }
}
