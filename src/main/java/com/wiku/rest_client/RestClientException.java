package com.wiku.rest_client;

import java.io.IOException;

public class RestClientException extends Exception
{

    public RestClientException(String message)
    {
        super(message);
    }

    public RestClientException(String message, Throwable e)
    {
        super(message, e);
    }

    private static final long serialVersionUID = 7865345558828866050L;
}
