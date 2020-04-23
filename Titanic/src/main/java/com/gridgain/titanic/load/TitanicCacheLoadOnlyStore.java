package com.gridgain.titanic.load;

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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.UUID;

import javax.cache.CacheException;
import javax.cache.configuration.FactoryBuilder;
import javax.cache.integration.CacheLoaderException;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteException;
import org.apache.ignite.Ignition;
import org.apache.ignite.binary.BinaryObject;
import org.apache.ignite.binary.BinaryObjectBuilder;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.cache.CachePeekMode;
import org.apache.ignite.cache.store.CacheLoadOnlyStoreAdapter;
import org.apache.ignite.cache.store.jdbc.JdbcTypeField;
import org.apache.ignite.cache.store.jdbc.CacheJdbcPojoStore;
//import org.apache.ignite.cache.store.jdbc.CacheAbstractJdbcStore.TypeKind;
import org.apache.ignite.cache.store.jdbc.CacheJdbcPojoStore.ClassProperty;
import org.apache.ignite.cache.store.jdbc.CacheJdbcPojoStore.PojoPropertiesCache;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.internal.util.IgniteUtils;
import org.apache.ignite.internal.util.typedef.T2;
import org.apache.ignite.internal.util.typedef.internal.U;
import org.apache.ignite.lang.IgniteBiTuple;
import org.jetbrains.annotations.Nullable;

import com.gridgain.titanic.model.Titanic;
import com.gridgain.titanic.model.TitanicKey;
import com.gridgain.titanic.util.ParseTypes;


/**
 * Load data from CSV file using {@link CacheLoadOnlyStoreAdapter}.
 * <p>
 * The adapter is intended to be used in cases when you need to pre-load a cache from text or file of any other format.
 * <p>
 */
public class TitanicCacheLoadOnlyStore {
    /** Cache name. */
    private static final String CACHE_NAME = "TitanicCache";
    private static String csvFilename = "./titanic.csv";

    /**
     * Executes loader.
     *
     * @param args Command line arguments, none required.
     * @throws IgniteException If example execution failed.
     */
    public static void main(String[] args) throws IgniteException {
        try (Ignite ignite = Ignition.start("Titanic-client.xml")) {
            System.out.println();
            System.out.println(">>> TitanicCacheLoadOnlyStore main starting, args: " + args);

            if (args.length > 0) {
                csvFilename = args[0];
            }

            TitanicLoader titanicLoader = new TitanicLoader(csvFilename);

            titanicLoader.setThreadsCount(2);
            titanicLoader.setBatchSize(10);
            titanicLoader.setBatchQueueSize(1);

            try (IgniteCache<TitanicKey, Titanic> cache = ignite.getOrCreateCache(CACHE_NAME)) {
                // load data.
                cache.loadCache(null);

                System.out.println(">>> Loaded number of items: " + cache.size(CachePeekMode.PRIMARY));

                System.out.println(">>> Data for the Titanic by TitanicKey(1): " + cache.get(new TitanicKey(1)));
            }
            finally {
                // Distributed cache could be removed from cluster only by #destroyCache() call.
                ignite.destroyCache(CACHE_NAME);
            }
        }
    }

    /**
     * Creates cache configurations for the loader.
     *
     * @return {@link CacheConfiguration}.
     */
    private static CacheConfiguration cacheConfiguration(TitanicLoader titanicLoader) {
        CacheConfiguration cacheCfg = new CacheConfiguration();

        cacheCfg.setCacheMode(CacheMode.PARTITIONED);
        cacheCfg.setName(CACHE_NAME);

        // provide the loader.
        cacheCfg.setCacheStoreFactory(new FactoryBuilder.SingletonFactory(titanicLoader));

        return cacheCfg;
    }

    /**
     * Csv data loader for Titanic data.
     */
    private static class TitanicLoader extends CacheJdbcPojoStore<TitanicKey, Titanic> implements Serializable {
        /** Csv file name. */
        final String csvFileName;

        /** Constructor. */
        TitanicLoader(String csvFileName) {
            this.csvFileName = csvFileName;
        }

        /** {@inheritDoc} */
        @Override protected Iterator<String> inputIterator(@Nullable Object... args) throws CacheLoaderException {
            final Scanner scanner;

            try {
                File path = IgniteUtils.resolveIgnitePath(csvFileName);

                if (path == null)
                    throw new CacheLoaderException("Failed to open the source file: " + csvFileName);

                scanner = new Scanner(path);

                scanner.useDelimiter("\\n");
            }
            catch (FileNotFoundException e) {
                throw new CacheLoaderException("Failed to open the source file " + csvFileName, e);
            }

            /**
             * Iterator for text input. The scanner is implicitly closed when there's nothing to scan.
             */
            return new Iterator<String>() {
                /** {@inheritDoc} */
                @Override public boolean hasNext() {
                    if (!scanner.hasNext()) {
                        scanner.close();

                        return false;
                    }

                    return true;
                }

                /** {@inheritDoc} */
                @Override public String next() {
                    if (!hasNext())
                        throw new NoSuchElementException();

                    return scanner.next();
                }

                /** {@inheritDoc} */
                @Override public void remove() {
                    throw new UnsupportedOperationException();
                }
            };
        }

        /** {@inheritDoc} */
        @Nullable @Override protected IgniteBiTuple<TitanicKey, Titanic> parse(String rec, @Nullable Object... args) {
            String[] p = rec.split("\\s*,\\s*");
            return new T2<TitanicKey, Titanic>(
            	new TitanicKey(Integer.valueOf(p[0])),
            	new Titanic (
	        		Integer.valueOf(p[1]),        // Survived
	        		Integer.valueOf(p[2]),        // Pclass
	        		p[3],                         // Name
	        		p[4],                         // Sex
	        		ParseTypes.ParseDouble(p[5]), // Age
	        		Integer.valueOf(p[6]),        // SibSp
	        		Integer.valueOf(p[7]),        // Parch
	        		p[8],                         // Ticket
	        		ParseTypes.ParseDouble(p[9]), // Fare
	        		p[10],                        // Cabin
	        		p[11]                         // Embarked
	        	)
            );
        }
 
        /** {@inheritDoc} */
        @Override protected Object buildObject(@Nullable String cacheName, String typeName, TypeKind typeKind, JdbcTypeField[] flds, Map loadColIdxs, ResultSet rs) throws CacheLoaderException {
            Object o = super.buildObject(cacheName, typeName, typeKind, flds, loadColIdxs, rs);

            if (typeKind == TypeKind.BINARY) {
                BinaryObjectBuilder builder = ((BinaryObject) o).toBuilder();

                builder.setField("myKey", UUID.randomUUID());

                o = builder.build();
            }

            return o;
        }
    }

}
