/*
 * Copyright (c) 2017 Wiku. All rights reserved.
 */
package com.wiku.rest.client;

import java.util.Collection;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class CrudResource<T>
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
        Class<?> responseClass = jsonMapper.getTypeFactory()
                .constructCollectionType(List.class, resourceClass)
                .getRawClass();
        return (Collection<T>)restClient.get(uri, responseClass);
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
