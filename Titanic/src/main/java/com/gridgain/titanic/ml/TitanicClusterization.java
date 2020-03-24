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

package com.gridgain.titanic.ml;

import java.io.IOException;
import javax.cache.Cache;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.apache.ignite.binary.BinaryObject;
import org.apache.ignite.cache.query.QueryCursor;
import org.apache.ignite.cache.query.ScanQuery;
import org.apache.ignite.ml.clustering.kmeans.KMeansModel;
import org.apache.ignite.ml.clustering.kmeans.KMeansTrainer;
import org.apache.ignite.ml.dataset.impl.cache.CacheBasedDatasetBuilder;
import org.apache.ignite.ml.dataset.feature.extractor.Vectorizer;
import org.apache.ignite.ml.dataset.feature.extractor.impl.DummyVectorizer;
import org.apache.ignite.ml.dataset.feature.extractor.impl.LabeledDummyVectorizer;
import org.apache.ignite.ml.math.Tracer;
import org.apache.ignite.ml.math.primitives.vector.Vector;
import org.apache.ignite.ml.preprocessing.Preprocessor;
import org.apache.ignite.ml.sql.SqlDatasetBuilder;
import org.apache.ignite.ml.tree.DecisionTreeClassificationTrainer;
import org.apache.ignite.ml.tree.DecisionTreeNode;
import org.apache.ignite.ml.util.MLSandboxDatasets;
import org.apache.ignite.ml.util.SandboxMLCache;

import com.gridgain.titanic.ml.dataset.feature.extractor.impl.BinaryObjectVectorizer;
import com.gridgain.titanic.model.Titanic;
import com.gridgain.titanic.model.TitanicKey;

/**
 * Run KMeans clustering algorithm ({@link KMeansTrainer}) over distributed dataset.
 * <p>
 * Code in this example launches Ignite grid and fills the cache with test data points (based on the
 * <a href="https://en.wikipedia.org/wiki/Iris_flower_data_set"></a>Iris dataset</a>).</p>
 * <p>
 * After that it trains the model based on the specified data using
 * <a href="https://en.wikipedia.org/wiki/K-means_clustering">KMeans</a> algorithm.</p>
 * <p>
 * Finally, this example loops over the test set of data points, applies the trained model to predict what cluster
 * does this point belong to, and compares prediction to expected outcome (ground truth).</p>
 * <p>
 * You can change the test data used in this example and re-run it to explore this algorithm further.</p>
 */
public class TitanicClusterization {
	
	private static String CACHE_NAME = "TitanicCache";
	
    /** Run example. */
    public static void main(String[] args) throws IOException {
        System.out.println();
        System.out.println(">>> K Means Model clustering algorithm over cached dataset usage example started.");

        // Start Ignite node
        try (Ignite ignite = Ignition.start("Titanic-client.xml")) {
            System.out.println(">>> Ignite client node started.");

            IgniteCache<Titanic, TitanicKey> dataCache = null;
            try {
                dataCache = ignite.getOrCreateCache(CACHE_NAME);

//                SqlDatasetBuilder sqlDS = new SqlDatasetBuilder(ignite, CACHE_NAME);
//                CacheBasedDatasetBuilder<Integer, BinaryObject> datasetBuilder = new CacheBasedDatasetBuilder<Titanic,TitanicKey>(ignite, dataCache).withKeepBinary(true);
//                
//                //Vectorizer<Integer, Vector, Integer, Double> vectorizer = new DummyVectorizer<Integer>().labeled(Vectorizer.LabelCoordinate.FIRST);
//                LabeledDummyVectorizer<Integer, Double> vectorizer = new LabeledDummyVectorizer(1);
//
//                // Create trainer.
//                //DecisionTreeClassificationTrainer trainer = new DecisionTreeClassificationTrainer(4, 0);
//                KMeansTrainer trainer = new KMeansTrainer();
//
//                //DecisionTreeNode mdl = trainer.fit(
//                //KMeansModel mdl = trainer.fit(
//                KMeansModel mdl = trainer.fit(
//                    ignite,
//                    dataCache,
//                    (Preprocessor)vectorizer
//                );
                
                System.out.println(">>> Prepare trainer...");
                KMeansTrainer trainer = new KMeansTrainer();
                //DecisionTreeClassificationTrainer trainer = new DecisionTreeClassificationTrainer(4, 0);

                //DecisionTreeNode mdl = trainer.fit(
                KMeansModel mdl = trainer.fit(
                    new SqlDatasetBuilder(ignite, "TitanicCache"),
                    new BinaryObjectVectorizer<>("pclass", "age", "sibsp", "parch", "fare")
	                    .withFeature("sex", BinaryObjectVectorizer.Mapping.create().map("male", 1.0).defaultValue(0.0))
	                    .withFeature("embarked", BinaryObjectVectorizer.Mapping.create().map("C", 1.0).defaultValue(0.0))
	                    .withFeature("cabin", BinaryObjectVectorizer.Mapping.create().map("", 0.0).defaultValue(1.0))
                        .labeled("survived")
                );

                System.out.println(">>> KMeans centroids");
                Tracer.showAscii(mdl.getCenters()[0]);
                Tracer.showAscii(mdl.getCenters()[1]);
                System.out.println(">>>");

                System.out.println(">>> --------------------------------------------");
                System.out.println(">>> | Predicted cluster\t| Erased class label\t|");
                System.out.println(">>> --------------------------------------------");

                try (QueryCursor<Cache.Entry<TitanicKey, Titanic>> observations = dataCache.query(new ScanQuery<>())) {
                    for (Cache.Entry<TitanicKey, Titanic> observation : observations) {
                    	Titanic val = observation.getValue();
                        //Titanic inputs = val.copyOfRange(1, val.size());

//                        double groundTruth = val.get(0);
//
//                        double prediction = mdl.predict(inputs);
//
//                        System.out.printf(">>> | %.4f\t\t\t| %.4f\t\t|\n", prediction, groundTruth);
                    }

                    System.out.println(">>> ---------------------------------");
                    System.out.println(">>> KMeans clustering algorithm over cached dataset usage example completed.");
                }
            } finally {
                //dataCache.destroy();
            }
        } finally {
            System.out.flush();
        }
    }
}
