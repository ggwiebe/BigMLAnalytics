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
//import org.apache.ignite.binary.BinaryObject;
import org.apache.ignite.cache.query.QueryCursor;
import org.apache.ignite.cache.query.ScanQuery;
import org.apache.ignite.ml.clustering.kmeans.KMeansModel;
import org.apache.ignite.ml.clustering.kmeans.KMeansTrainer;
// import org.apache.ignite.ml.dataset.impl.cache.CacheBasedDatasetBuilder;
// import org.apache.ignite.ml.dataset.feature.extractor.Vectorizer;
// import org.apache.ignite.ml.dataset.feature.extractor.impl.DummyVectorizer;
// import org.apache.ignite.ml.dataset.feature.extractor.impl.LabeledDummyVectorizer;
import org.apache.ignite.ml.math.Tracer;
// import org.apache.ignite.ml.math.primitives.vector.Vector;
import org.apache.ignite.ml.math.primitives.vector.impl.DenseVector;
// import org.apache.ignite.ml.preprocessing.Preprocessor;
import org.apache.ignite.ml.sql.SqlDatasetBuilder;
// import org.apache.ignite.ml.tree.DecisionTreeClassificationTrainer;
// import org.apache.ignite.ml.tree.DecisionTreeNode;
//import org.apache.ignite.examples.ml.util.MLSandboxDatasets;
//import org.apache.ignite.examples.ml.util.SandboxMLCache;
//import org.apache.ignite.ml.util.MLSandboxDatasets;
//import org.apache.ignite.ml.util.SandboxMLCache;

import org.apache.ignite.ml.dataset.feature.extractor.impl.DenseBinaryObjectVectorizer;
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
            	/*
            	 * 1. Access Data
            	 * 
            	 *    - SqlDatasetBuilder prepares a class to pull data from a SQL-based cache
            	 *    
            	 *    NOTE: this dataset builder can be done directly during fit() as below
            	 */
                //dataCache = ignite.getOrCreateCache(CACHE_NAME).withKeepBinary();
                //SqlDatasetBuilder sqlDS = new SqlDatasetBuilder(ignite, CACHE_NAME);
                //CacheBasedDatasetBuilder<Integer, BinaryObject> datasetBuilder = new CacheBasedDatasetBuilder<Titanic,TitanicKey>(ignite, dataCache).withKeepBinary(true);
                
            	/*
            	 * 2. Preprocess Data
            	 * 
            	 *    BinaryObjectVectorizer will:
            	 *    - use coordinates to select features
            	 *    - map features from String literals to numeric values
            	 *    - set the Label of the vector to a particular field 
            	 *    
            	 *    NOTE: can be done directly during fit() call as below
            	 */
//                //Vectorizer<Integer, Vector, Integer, Double> vectorizer = new DummyVectorizer<Integer>().labeled(Vectorizer.LabelCoordinate.FIRST);
//                LabeledDummyVectorizer<Integer, Double> vectorizer = new LabeledDummyVectorizer(1);
//                // a pre-processor that converts Cache BinaryObjects into a Vector


                /*
            	 * 3. Fit Data with trainer to create model
            	 * 
            	 *    - Create a particular type of Trainer
            	 *    - Fit the trainer by supplying:
            	 *        a. the dataset (or an object that create a dataset; in this case the DatasetBuilder
            	 *        b. a vectorizer - i.e. something that can take the dataset and convert to the selected, filtered, mapped Vector the trainer needs
            	 */
                // Create trainer.
                System.out.println(">>> Prepare trainer...");
                //DecisionTreeClassificationTrainer trainer = new DecisionTreeClassificationTrainer(4, 0);
                KMeansTrainer trainer = new KMeansTrainer();

                // With trainer, run fit(), supplying data (a SQL/cache-based Dataset Builder) and preprocessor (here a BinaryObjectVectorizer)
                KMeansModel mdl = trainer.fit(
                    new SqlDatasetBuilder(ignite, CACHE_NAME),  //ignite, CACHE_NAME, // Alternate fit() signature
                    new DenseBinaryObjectVectorizer<>("pclass", "sex", "age", "sibsp", "parch", "fare", "cabin", "embarked")
	                .withFeature("sex", DenseBinaryObjectVectorizer.Mapping.create().map("male", 1.0).defaultValue(0.0))
	                .withFeature("embarked", DenseBinaryObjectVectorizer.Mapping.create().map("C", 1.0).defaultValue(0.0))
	                .withFeature("cabin", DenseBinaryObjectVectorizer.Mapping.create().map("", 0.0).defaultValue(1.0))
	                .labeled("survived")
                );

                // Print out Model metadata
                System.out.println(">>> KMeans centroids");
                Tracer.showAscii(mdl.getCenters()[0]);
                Tracer.showAscii(mdl.getCenters()[1]);
                System.out.println(">>>");


                /*
            	 * 4. Predict new data from trained model
            	 * 
            	 *    Create an ML Vector with a new data point and predict from model   
            	 */
                System.out.println(">>> --------------------------------------------");
                System.out.println(">>> | Predicted cluster\t| Erased class label\t|");
                System.out.println(">>> --------------------------------------------");

                // now that we trained the model, let's compare against the ground truth results
                dataCache = ignite.getOrCreateCache(CACHE_NAME);
                try (QueryCursor<Cache.Entry<TitanicKey, Titanic>> observations = dataCache.query(new ScanQuery<>())) {
                	int totalEntries = 0;
                	int totalCorrect = 0;
                	int totalWrong = 0;
                	for (Cache.Entry<TitanicKey, Titanic> observation : observations) {
                    	Titanic val = observation.getValue();
                    	totalEntries++;
                    	
                    	double[] obsVals = new double[8];
                    	obsVals[0] = val.getPclass();
                    	obsVals[1] = (val.getSex().equalsIgnoreCase("male")) ? 1.0 : 0.0;
                    	obsVals[2] = val.getAge();
                    	obsVals[3] = val.getSibsp();
                    	obsVals[4] = val.getParch();
                    	obsVals[5] = val.getFare();
                    	obsVals[6] = (val.getCabin().equalsIgnoreCase("")) ? 0.0 : (1.0);
                    	obsVals[7] = (val.getEmbarked().equalsIgnoreCase("C")) ? 1.0 : 0.0;
                    	int preVal = mdl.predict(new DenseVector(obsVals));

                    	if ( preVal==1 && val.getSurvived()==1) {
                    		// predict survived and did survive
                    		totalCorrect++;
                    	} else if ( preVal==0 && val.getSurvived()==0) {
                    		// predict !survived and did not survive
                    		totalCorrect++;
                    	} else {
                    		// wrong
                    		totalWrong++;
                    	}
                    	
                    }
                    System.out.printf(">>> K Means model: %d correct vs %d total entries, or %.2f%% correct.\n", totalCorrect, totalEntries,(100.0*totalCorrect/totalEntries));
                    System.out.println(">>> ---------------------------------");
                    System.out.println(">>> KMeans clustering algorithm over cached dataset usage example completed.");
                } catch(Exception e) {
                	System.out.println("Query Cursor: Caught Exception: " + e);
                }
            } finally {
                //dataCache.destroy();
            }
        } finally {
            System.out.flush();
        }
    }
}
