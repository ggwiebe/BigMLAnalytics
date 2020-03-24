package com.gridgain.titanic.load;

import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;

/** This file was generated by Ignite Web Console (03/23/2020, 18:23) **/
public class LoadCaches {
    /**
     * <p>
     * Utility to com.gridgain.titanic.load caches from database.
     * <p>
     * How to use:
     * <ul>
     *     <li>Start cluster.</li>
     *     <li>Start this utility and wait while com.gridgain.titanic.load complete.</li>
     * </ul>
     * 
     * @param args Command line arguments, none required.
     * @throws Exception If failed.
     **/
    public static void main(String[] args) throws Exception {
        try (Ignite ignite = Ignition.start("Titanic-client.xml")) {
            System.out.println(">>> Loading caches...");

            System.out.println(">>> Loading cache: TitanicCache");
            ignite.cache("TitanicCache").loadCache(null);

            System.out.println(">>> All caches loaded!");
        }
    }
}