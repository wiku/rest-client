/*
 * Copyright (c) 2017 Wiku. All rights reserved.
 */
package com.wiku.rest.client;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit.WireMockRule;

/**
 * Learning tests that require with spring simple spring boot rest repository. No longer needed, but left as an example.
 * 
 * @author wiku
 */
public class CrudResourceWiremockTest
{
    private static final String ASSETS_URL = "/api/v1/assets";
    private static final Asset ASSET1 = new Asset(0, "Asset 11", "type", 1, 100, "X5");
    private static final Asset ASSET2 = new Asset(1, "Asset 12", "type", 1, 100, "X5");
    private static final List<Asset> ALL_ASSETS = Arrays.asList(ASSET1, ASSET2);
    private static ObjectMapper jsonMapper = new ObjectMapper();

    @Rule
    public WireMockRule mockedService = new WireMockRule();

    private RestClient client = new RestClient();
    private CrudResource<Asset> assetResource;

    @Before
    public void beforeTests()
    {
        assetResource = client.getResource(getAssetsResourceUrl(), Asset.class);
    }

    @Test
    public void whenPostNewAsset_ShouldAddNewAssetAndReturn() throws RestClientException, JsonProcessingException
    {
        stubFor(post(urlEqualTo(ASSETS_URL)).willReturn(
                aResponse().withStatus(200).withBody(jsonFrom(ASSET2)).withHeader("Content-Type", "application-json")));

        Asset asset = assetResource.create(ASSET1);

        Assert.assertEquals(ASSET2, asset);
        verify(postRequestedFor(urlMatching(ASSETS_URL)).withRequestBody(equalTo(( jsonFrom(ASSET1) ))));
    }

    @Test
    public void whenGetAssets_ShouldReturnAllAssets() throws RestClientException, JsonProcessingException
    {
        stubFor(get(urlEqualTo(ASSETS_URL)).willReturn(aResponse().withStatus(200)
                .withBody(jsonFrom(ALL_ASSETS))
                .withHeader("Content-Type", "application-json")));

        Collection<Asset> assetsReturned = assetResource.read();

        assertEquals(ALL_ASSETS, assetsReturned);
        verify(getRequestedFor(urlMatching(ASSETS_URL)));
    }
    
//    @Test
//    public void whenGetAssetsWithBasicAuth_ShouldReturnAllAssets() throws RestClientException, JsonProcessingException
//    {
//        stubFor(get(urlEqualTo(ASSETS_URL)).withBasicAuth("user", "password").willReturn(aResponse().withStatus(200)
//                .withBody(jsonFrom(ALL_ASSETS))
//                .withHeader("Content-Type", "application-json")));
//
//        client = new RestClient("user", "password");
//        assetResource = client.getResource(getAssetsResourceUrl(), Asset.class);
//        Collection<Asset> assetsReturned = assetResource.read();
//
//        assertEquals(ALL_ASSETS, assetsReturned);
//        verify(getRequestedFor(urlMatching(ASSETS_URL)));
//    }

    @Test(expected = RestClientException.class)
    public void whenGetAssetsReturnsBrokenJson_ShouldThrowRestClientException()
            throws RestClientException, JsonProcessingException
    {
        stubFor(get(urlEqualTo(ASSETS_URL)).willReturn(aResponse().withStatus(200)
                .withBody("{broken json string}")
                .withHeader("Content-Type", "application-json")));

        assetResource.read();
    }

    @Test
    public void whenGetAssetById_ShouldReturnGivenAsset() throws RestClientException, JsonProcessingException
    {
        stubFor(get(urlEqualTo(ASSETS_URL + "/1")).willReturn(
                aResponse().withStatus(200).withBody(jsonFrom(ASSET1)).withHeader("Content-Type", "application-json")));

        Asset asset = assetResource.read(1);

        Assert.assertEquals(ASSET1, asset);
        verify(getRequestedFor(urlMatching(ASSETS_URL + "/1")));
    }

    @Test
    public void whenPutAssetById_ShouldUpdateAndReturnAsset() throws RestClientException, JsonProcessingException
    {
        stubFor(put(urlEqualTo(ASSETS_URL + "/1")).willReturn(
                aResponse().withStatus(200).withBody(jsonFrom(ASSET2)).withHeader("Content-Type", "application-json")));

        Asset asset = assetResource.update(1, ASSET1);

        Assert.assertEquals(ASSET2, asset);
        verify(putRequestedFor(urlMatching(ASSETS_URL + "/1")).withRequestBody(equalTo(( jsonFrom(ASSET1) ))));
    }

    @Test
    public void whenDeleteAssetById_ShouldDeleteAndReturnAsset() throws RestClientException, JsonProcessingException
    {
        stubFor(delete(urlEqualTo(ASSETS_URL + "/1")).willReturn(
                aResponse().withStatus(200).withBody(jsonFrom(ASSET1)).withHeader("Content-Type", "application-json")));

        Asset deleted = assetResource.delete(1);

        Assert.assertEquals(ASSET1, deleted);
        verify(deleteRequestedFor(urlMatching(ASSETS_URL + "/1")));
    }

    @Test(expected = ResourceNotFoundRestClientException.class)
    public void whenGetNonExistingAsset_ShouldThrowNotFoundException() throws RestClientException
    {
        stubFor(get(urlEqualTo(ASSETS_URL + "/100")).willReturn(
                aResponse().withStatus(404).withBody("resource not found").withHeader("Content-Type", "text/plain; charset=utf-8")));
        Asset asset = assetResource.read(100);
    }

    private String getAssetsResourceUrl()
    {
        return "http://localhost:" + mockedService.port() + ASSETS_URL;
    }

    private String jsonFrom( Object objectToJson ) throws JsonProcessingException
    {
        return jsonMapper.writeValueAsString(objectToJson);
    }

}
