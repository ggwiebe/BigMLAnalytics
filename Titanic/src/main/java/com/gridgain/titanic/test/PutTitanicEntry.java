package com.gridgain.titanic.test;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;

import com.gridgain.titanic.model.Titanic;
import com.gridgain.titanic.model.TitanicKey;

public class PutTitanicEntry {

	public static void main(String[] args) {
        try (Ignite ignite = Ignition.start("Titanic-client.xml")) {
            System.out.println(">>> Starting PutTitanicEntry...");

            System.out.println(">>> getOrCreateCache...");
            IgniteCache<TitanicKey, Titanic> cache = ignite.getOrCreateCache("TitanicCache");

            System.out.println(">>> Create new entry...");
            TitanicKey k = new TitanicKey(666);
            Titanic    v = new Titanic(1, 4, "Glenn Wiebe", "male", 53.0, 1, 1, "ABCDEF123456", 116.50, "F", "E");
            
            cache.getAndPut(k, v);
            System.out.println(">>> Entry Create for:" + cache.get(new TitanicKey(666)).getName());
        }

	}

}
