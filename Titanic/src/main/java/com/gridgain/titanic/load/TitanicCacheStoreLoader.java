/*
 * Copyright 2019 GridGain Systems, Inc. and Contributors.
 *
 * Licensed under the GridGain Community Edition License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.gridgain.com/products/software/community-edition/gridgain-community-edition-license
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gridgain.titanic.load;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Scanner;
import javax.cache.configuration.FactoryBuilder;
import javax.cache.integration.CacheLoaderException;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteException;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.cache.CachePeekMode;
import org.apache.ignite.cache.store.CacheLoadOnlyStoreAdapter;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.internal.util.IgniteUtils;
import org.apache.ignite.internal.util.typedef.T2;
import org.apache.ignite.lang.IgniteBiTuple;
import org.jetbrains.annotations.Nullable;

import com.gridgain.titanic.model.Titanic;
import com.gridgain.titanic.model.TitanicKey;
import com.gridgain.titanic.load.TitanicLoadOnlyStore;
import com.gridgain.titanic.util.ParseTypes;

/**
 * Load data from CSV file using {@link CacheLoadOnlyStoreAdapter}.
 *
 * The adapter is intended to be used in cases when you need to pre-load a cache from text or file of any other format.
 * 
 * NOT WORKING AT PRESENT!!!
 */
public class TitanicCacheStoreLoader {

	/** Cache name. */
    private static final String CACHE_NAME = "TitanicCache";

    /**
     * Executes example.
     *
     * @param args Command line arguments, none required.
     * @throws IgniteException If load execution failed.
     */
    public static void main(String[] args) throws IgniteException {
        System.out.println();
        System.out.println(">>> TitanicCacheStoreLoader started...");

        System.out.println(">>> TitanicCacheStoreLoader create a TitanicLoadOnlyStore and initialize with file to load: " + args[0] + "...");
    	TitanicLoadOnlyStore<TitanicKey, Titanic> los = new TitanicLoadOnlyStore();
        los.setThreadsCount(2);
        los.setBatchSize(100);
        los.setBatchQueueSize(10);
    	
        System.out.println(">>> TitanicCacheStoreLoader start Ignite client and do...");
        try (Ignite ignite = Ignition.start("Titanic-client.xml")) {

            // if we are able to create the cache, then load it
            System.out.println(">>> TitanicCacheStoreLoader get or create cache...");
            try (IgniteCache<TitanicKey, Titanic> cache = ignite.getOrCreateCache(cacheConfiguration(los))) {

            	// have Cache's CacheStore load the data...
                System.out.println(">>> TitanicCacheStoreLoader; with cache, load using CacheStore...");
                cache.loadCache(null, args[0]);

                System.out.println(">>> TitanicCacheStoreLoader call to cache.loadCache() loaded number of items: " + cache.size(CachePeekMode.PRIMARY));
                System.out.println();
            }
            catch(Exception e) {
            	System.out.println(">>> TitanicCacheStoreLoader; trying to load cache threw exception: " + e);
            }
            finally {
                // Distributed cache could be removed from cluster only by #destroyCache() call.
                //ignite.destroyCache(CACHE_NAME);
            }
        }
    }

    /**
     * Creates cache configurations for the loader.
     *
     * @return {@link CacheConfiguration}.
     */
    private static CacheConfiguration cacheConfiguration(TitanicLoadOnlyStore skopyFeatureLoadOnlyStore) {
        System.out.println(">>> TitanicCacheStoreLoader; creating CacheConfiguration with TitanicLoadOnlyStore...");
        CacheConfiguration cacheCfg = new CacheConfiguration();

        cacheCfg.setCacheMode(CacheMode.PARTITIONED);
        cacheCfg.setName(CACHE_NAME);

        // provide the loader.
        cacheCfg.setCacheStoreFactory(new FactoryBuilder.SingletonFactory(skopyFeatureLoadOnlyStore));

        return cacheCfg;
    }

}
