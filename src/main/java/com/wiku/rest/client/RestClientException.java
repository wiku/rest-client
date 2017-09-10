/*
 * Copyright (c) 2017 Wiku. All rights reserved.
 */
package com.wiku.rest.client;

public class RestClientException extends Exception
{

    public RestClientException( String message )
    {
        super(message);
    }

    public RestClientException( String message, Throwable e )
    {
        super(message, e);
    }

    private static final long serialVersionUID = 7865345558828866050L;
}
