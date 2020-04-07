package com.gridgain.titanic.util;

import java.util.Iterator;

import javax.cache.Cache.Entry;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.CacheEntry;
import org.apache.ignite.ml.math.Tracer;
import org.apache.ignite.ml.math.primitives.vector.impl.DenseVector;
import org.apache.ignite.ml.structures.DatasetRow;
import org.apache.ignite.ml.structures.LabeledVector;

public class CacheView {

	private static String cacheName = "DefaultCache";
	
	public static void main(String[] args) {

		cacheName = args[0];
		
        try (Ignite ignite = Ignition.start("Titanic-client.xml")) {
            System.out.println(">>> Ignite Client started.");

            display(ignite, cacheName);

//            IgniteCache<Integer, LabeledVector<DenseVector>> cache = null;
//            try {
//            	cache = ignite.getOrCreateCache(cacheName);
//
//	    		cache.forEach(entry -> {
//	            	Integer eKey = entry.getKey();
//	            	//Object[] eVal = entry.getValue();
//	            	//System.out.println("entry.key: " + eKey + "; value:Entry:" + Arrays.deepToString(eVal));
//	            	//DatasetRow eVal = entry.getValue();
//	            	LabeledVector eVal = entry.getValue();
//	            	System.out.print("entry.key: " + eKey + "; value:Entry:"); Tracer.showAscii(eVal.features());
//
//	    		});
//	    		
//	        } finally {
//	            //cache.destroy();
//	            System.out.println(">>> CacheView with Ignite (named: " + ignite.name() + ") finished!");
//	        }

	    } finally {
	        System.out.flush();
	    }

	}

	public static void display(Ignite ignite, String cacheName) {
        System.out.println(">>> display cache entries using cache named: " + cacheName + "...");

        //IgniteCache<Integer, LabeledVector<DenseVector>> cache = null;
        IgniteCache cache = null;
        try {
        	cache = ignite.getOrCreateCache(cacheName);

//    		cache.forEach(entry -> {
//            	Integer eKey = entry.getKey();
//            	//Object[] eVal = entry.getValue();
//            	//System.out.println("entry.key: " + eKey + "; value:Entry:" + Arrays.deepToString(eVal));
//            	//DatasetRow eVal = entry.getValue();
//            	LabeledVector eVal = entry.getValue();
//            	System.out.print("entry.key: " + eKey + "; value:Entry:"); Tracer.showAscii(eVal.features());
//    		});
        	display(ignite,cache);
    		
        } finally {
            //cache.destroy();
            System.out.println(">>> CacheView with Ignite (named: " + ignite.name() + ") finished!");
        }
		
	}

	//public static void display(Ignite ignite, IgniteCache<Integer, LabeledVector<DenseVector>> cache) {
	public static void display(Ignite ignite, IgniteCache cache) {
        System.out.println(">>> display cache entries for cache (ignite:" + ignite.name() + ", cache:" + cache.getName() + ")");

        Iterator<Entry<Integer, LabeledVector>> itr = cache.iterator();                
        while(itr.hasNext()) {
        	LabeledVector object = itr.next().getValue();
           System.out.println(object);
        }
        
        try {
    		cache.forEach(entry -> {
            	//Integer eKey = ((CacheEntry<Integer,DenseVector>)entry).getKey();
            	////Integer eKey = (Integer)((CacheEntry)entry).getKey(); // DELETEME !!!!!!!
            	Integer eKey = (Integer) ((CacheEntry)entry).getKey();
            	//Object[] eVal = entry.getValue();
            	//System.out.println("entry.key: " + eKey + "; value:Entry:" + Arrays.deepToString(eVal));
            	//DatasetRow eVal = entry.getValue();
            	////LabeledVector eVal = (LabeledVector) ((CacheEntry)entry).getValue(); ///DELETE ME WHEN NEXT WORKING!!!
            	LabeledVector eVal = (LabeledVector) ((CacheEntry)entry).getValue();
            	//System.out.println("entry.key: " + eKey + "; value:Entry:" + eVal.toString());
            	System.out.println("entry.key: " + eKey + "; value:Entry:"); Tracer.showAscii(eVal.features());
    		});
        } catch(Exception e) {
        	System.out.print("Exception: "); e.printStackTrace();
        } finally {
            //cache.destroy();
            System.out.println(">>> CacheView with Ignite (named: " + ignite.name() + ") finished!");
        }
		
	}

}
