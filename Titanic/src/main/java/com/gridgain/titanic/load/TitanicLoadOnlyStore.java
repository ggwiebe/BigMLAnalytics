package com.gridgain.titanic.load;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Scanner;

import javax.cache.integration.CacheLoaderException;

import org.apache.ignite.cache.store.CacheLoadOnlyStoreAdapter;
import org.apache.ignite.internal.util.IgniteUtils;
import org.apache.ignite.internal.util.typedef.T2;
import org.apache.ignite.lang.IgniteBiInClosure;
import org.apache.ignite.lang.IgniteBiTuple;
import org.jetbrains.annotations.Nullable;

import com.gridgain.titanic.model.Titanic;
import com.gridgain.titanic.model.TitanicKey;
import com.gridgain.titanic.util.ParseTypes;

/**
 * Csv data loader for product data.
 * @param <K>
 * @param <V>
 */
public class TitanicLoadOnlyStore<K, V> extends CacheLoadOnlyStoreAdapter<TitanicKey, Titanic, String> implements Serializable {

	///** Csv file name. */
    //String csvFileName;

    ///** Constructor. */
    //public TitanicLoadOnlyStore(String csvFileName) {
    //    this.csvFileName = csvFileName;
    //    System.out.println(">>> TitanicLoadOnlyStore (LoadOnly type) constructed with csvFileName=" + this.csvFileName);
    //}

    /** Empty Constructor. */
    public TitanicLoadOnlyStore() {
        System.out.println(">>> TitanicLoadOnlyStore (LoadOnly type) constructed");
    }

    /** {@inheritDoc} */
    @Override protected Iterator<String> inputIterator(@Nullable Object... args) throws CacheLoaderException {
        System.out.println(">>> TitanicLoadOnlyStore: inputIterator(" + args + ") called...");
        String csvFileName = (String)args[0];
        
        final Scanner scanner;

        try {
            File path = IgniteUtils.resolveIgnitePath(csvFileName);
            if (path == null)
                throw new CacheLoaderException("Failed to open the source file: " + csvFileName);

            System.out.println(">>> TitanicLoadOnlyStore.inputIterator() create scanner...");
            scanner = new Scanner(path);
            scanner.useDelimiter("\\n");

            // Remove header line
            if(scanner.hasNext()==true)
            {
               scanner.nextLine();
            }
            else
            {
                System.out.println("Error: File is empty");
                return null;
            }            }
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
        System.out.println(">>> Parse record: " + rec);
        String[] p = rec.split("\\s*,\\s*");
        System.out.println(">>> Parse record via split; String[] p.length= " + p.length);

        Titanic sf = new Titanic (
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
    	);


        return new T2<TitanicKey, Titanic>(
        	new TitanicKey(
        			Integer.valueOf(p[0])
            ),
        	sf
        );

    }

    
}
