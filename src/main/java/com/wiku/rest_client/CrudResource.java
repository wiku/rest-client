package com.wiku.rest_client;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.List;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class CrudResource<T>
{

    private static ObjectMapper jsonMapper = new ObjectMapper();

    private final CloseableHttpClient client;
    private final String uri;
    private final Class<T> resourceClass;

    public T create( T entity ) throws RestClientException
    {
        HttpPost post = new HttpPost(uri);

        try
        {
            addEntityToRequest(post, entity);
            CloseableHttpResponse response = client.execute(post);
            checkStatusCode(response);
            return getEntityFromResponse(response);

        } catch (IOException e)
        {
            throw new RestClientException("Failed to execute post", e);
        }
    }

    public Collection<T> read() throws RestClientException
    {
        HttpGet get = new HttpGet(uri);

        try
        {
            CloseableHttpResponse response = client.execute(get);
            checkStatusCode(response);
            return getEntityCollectionFromResponse(response);
        } catch (IOException e)
        {
            throw new RestClientException("Failed to execute get", e);
        }
    }

    
    public T read( int id ) throws RestClientException
    {
        HttpGet get = new HttpGet(uri + "/" + id);

        try
        {
            CloseableHttpResponse response = client.execute(get);
            checkStatusCode(response);
            return getEntityFromResponse(response);
        } catch (IOException e)
        {
            throw new RestClientException("Failed to execute get", e);
        }

    }
    
    public T update( int id, T entity ) throws RestClientException
    {
        HttpPut put = new HttpPut(uri + "/" + id);

        try
        {
            addEntityToRequest(put, entity);
            CloseableHttpResponse response = client.execute(put);
            checkStatusCode(response);
            return getEntityFromResponse(response);
        } 
        catch (IOException e)
        {
            throw new RestClientException("Failed to execute put", e);
        }
    }

    
    public T delete( int id) throws RestClientException
    {
        HttpDelete delete = new HttpDelete(uri + "/" + id);

        try
        {
            CloseableHttpResponse response = client.execute(delete);
            checkStatusCode(response);
            return getEntityFromResponse(response);
        } 
        catch (IOException e)
        {
            throw new RestClientException("Failed to execute delete", e);
        }
            
    }

    private void addEntityToRequest( HttpEntityEnclosingRequest request, T entity ) throws JsonProcessingException
    {
        String entityAsString = entityToJsonString(entity);
        StringEntity requestBody = createRequestBody(entityAsString);
        request.setEntity(requestBody);
    }

    private StringEntity createRequestBody( String entityAsString )
    {
        StringEntity input = new StringEntity(entityAsString, Charset.defaultCharset());
        input.setContentType("application/json");
        return input;
    }

    private String entityToJsonString( T entity ) throws JsonProcessingException
    {
        return jsonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(entity);
    }

    private T getEntityFromResponse( CloseableHttpResponse response )
            throws IOException, JsonParseException, JsonMappingException
    {
        return jsonMapper.readValue(response.getEntity().getContent(), resourceClass);
    }

    private Collection<T> getEntityCollectionFromResponse( CloseableHttpResponse response )
            throws IOException, JsonParseException, JsonMappingException
    {
        return jsonMapper.readValue(response.getEntity().getContent(),
                jsonMapper.getTypeFactory().constructCollectionType(List.class, resourceClass));
    }

    private void checkStatusCode( CloseableHttpResponse response ) throws RestClientException
    {
        if (response.getStatusLine().getStatusCode() != 200)
        {
            throw new RestClientException("Failed : HTTP error code : " + response.getStatusLine().getStatusCode());
        }
    }

}
