package com.wiku.rest.client;

public class ResourceNotFoundRestClientException extends RestClientException
{
    public ResourceNotFoundRestClientException( String reasonPhrase )
    {
        super(reasonPhrase);
    }
}
