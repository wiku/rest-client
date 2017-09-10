/*
 * Copyright (c) 2017 Wiku. All rights reserved.
 */
package com.wiku.rest.client;

import java.util.Collection;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Learning tests that require with spring simple spring boot rest repository. 
 * No longer needed, but left as an example.
 * 
 * @author wiku
 */
@Ignore
public class CrudResourceLearningTest
{

    private final static Asset ASSET1 = new Asset(1, "Asset 11", "type", 1, 100, "X5");

    private RestClient client = new RestClient();
    CrudResource<Asset> assets = client.getResource("http://localhost:8080/api/v1/assets", Asset.class);

    @Test
    public void whenPostNewAsset_ShouldAddNewAssetAndReturn() throws RestClientException
    {
        Asset asset = assets.create(ASSET1);
        Assert.assertEquals(ASSET1, asset);
    }

    @Test
    public void whenGetAssets_ShouldReturnAllAssets() throws RestClientException
    {
        Collection<Asset> assetsReturned = assets.read();
        System.out.println(assetsReturned);
        Assert.assertNotEquals(0, assetsReturned.size());
    }

    @Test
    public void whenGetAssetById_ShouldReturnGivenAsset() throws RestClientException
    {
        Asset asset = assets.read(1);
        Assert.assertEquals(ASSET1, asset);
    }

    @Test
    public void whenPutAssetById_ShouldUpdateAndReturnAsset() throws RestClientException
    {

        Asset newAsset1 = new Asset(1, "test2", "test2", 1, 1000, "AX1BC");
        Asset asset = assets.update(1, newAsset1);

        System.out.println(assets.read(1));
        Assert.assertEquals(newAsset1, asset);
    }

    @Test
    public void whenDeleteAssetById_ShouldDeleteAndReturnAsset() throws RestClientException
    {

        Asset newAsset1 = new Asset(0, "asset to delete", "test2", 1, 1000, "XXX");
        Asset asset = assets.create(newAsset1);

        Asset deleted = assets.delete(asset.getId());

        Assert.assertEquals(asset, deleted);

    }

}
