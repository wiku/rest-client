/*
 * Copyright (c) 2017 Wiku. All rights reserved.
 */
package com.wiku.rest.client;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CrudResource<T>
{

    private static ObjectMapper jsonMapper = new ObjectMapper();

    private final RestClient restClient;
    private final String uri;
    private final Class<T> resourceClass;

    public T create( T entity ) throws RestClientException
    {
        return restClient.post(uri, entity, resourceClass);
    }

    @SuppressWarnings("unchecked")
    public Collection<T> read() throws RestClientException
    {
        Class<?> arrayClass = Array.newInstance(resourceClass, 0).getClass();
        T[] array = (T[])restClient.get(uri, arrayClass);
        return Arrays.asList(array);
    }

    public T read( int id ) throws RestClientException
    {
        return restClient.get(getResourceURI(id), resourceClass);
    }

    public T update( int id, T entity ) throws RestClientException
    {
        return restClient.put(getResourceURI(id), entity, resourceClass);
    }

    public T delete( int id ) throws RestClientException
    {
        return restClient.delete(getResourceURI(id), resourceClass);
    }

    private String getResourceURI( int id )
    {
        return uri + "/" + id;
    }

}
