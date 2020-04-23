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

import javax.cache.Cache;
import javax.cache.integration.CacheLoaderException;
import javax.cache.integration.CacheWriterException;
import org.apache.ignite.cache.store.CacheStore;
import org.apache.ignite.cache.store.CacheStoreAdapter;
import org.apache.ignite.cache.store.CacheStoreSession;
import org.apache.ignite.lang.IgniteBiInClosure;
import org.apache.ignite.resources.CacheStoreSessionResource;

import com.gridgain.titanic.model.TitanicKey;
import com.gridgain.titanic.model.Titanic;

/**
 * A CacheStore implementation that uses CSV to load
 */
public class TitanicCacheStore extends CacheStoreAdapter<TitanicKey, Titanic> {
    /** Store session. */
    @CacheStoreSessionResource
    private CacheStoreSession ses;

    /** {@inheritDoc} */
    public Titanic loadAll() {
        System.out.println(">>> Store loadAll...");

        return rs.next() ? new Titanic(rs.getTitanicKey(1), rs.getString(2), rs.getString(3)) : null;

//        Connection conn = ses.attachment();
//        try (PreparedStatement st = conn.prepareStatement("select * from PERSON where id = ?")) {
//            st.setString(1, key.toString());
//            ResultSet rs = st.executeQuery();
//            return rs.next() ? new Titanic(rs.getTitanicKey(1), rs.getString(2), rs.getString(3)) : null;
//        }
//        catch (SQLException e) {
//            throw new CacheLoaderException("Failed to load object [key=" + key + ']', e);
//        }
    }

    /** {@inheritDoc} */
    @Override public void write(Cache.Entry<? extends TitanicKey, ? extends Titanic> entry) {
        TitanicKey key = entry.getKey();
        Titanic val = entry.getValue();

        System.out.println(">>> Store write [key=" + key + ", val=" + val + ']');

        try {
            Connection conn = ses.attachment();

            int updated;

            // Try update first. If it does not work, then try insert.
            // Some databases would allow these to be done in one 'upsert' operation.
            try (PreparedStatement st = conn.prepareStatement(
                "update PERSON set first_name = ?, last_name = ? where id = ?")) {
                st.setString(1, val.firstName);
                st.setString(2, val.lastName);
                st.setTitanicKey(3, val.id);

                updated = st.executeUpdate();
            }

            // If update failed, try to insert.
            if (updated == 0) {
                try (PreparedStatement st = conn.prepareStatement(
                    "insert into PERSON (id, first_name, last_name) values (?, ?, ?)")) {
                    st.setTitanicKey(1, val.id);
                    st.setString(2, val.firstName);
                    st.setString(3, val.lastName);

                    st.executeUpdate();
                }
            }
        }
        catch (SQLException e) {
            throw new CacheWriterException("Failed to write object [key=" + key + ", val=" + val + ']', e);
        }
    }

    /** {@inheritDoc} */
    @Override public void delete(Object key) {
        System.out.println(">>> Store delete [key=" + key + ']');

        Connection conn = ses.attachment();

        try (PreparedStatement st = conn.prepareStatement("delete from PERSON where id=?")) {
            st.setTitanicKey(1, (TitanicKey)key);

            st.executeUpdate();
        }
        catch (SQLException e) {
            throw new CacheWriterException("Failed to delete object [key=" + key + ']', e);
        }
    }

    /** {@inheritDoc} */
    @Override public void loadCache(IgniteBiInClosure<TitanicKey, Titanic> clo, Object... args) {
        if (args == null || args.length == 0 || args[0] == null)
            throw new CacheLoaderException("Expected entry count parameter is not provided.");

        final int entryCnt = (Integer)args[0];

        Connection conn = ses.attachment();

        try (PreparedStatement stmt = conn.prepareStatement("select * from PERSON limit ?")) {
            stmt.setInt(1, entryCnt);

            ResultSet rs = stmt.executeQuery();

            int cnt = 0;

            while (rs.next()) {
                Titanic person = new Titanic(rs.getTitanicKey(1), rs.getString(2), rs.getString(3));

                clo.apply(person.id, person);

                cnt++;
            }

            System.out.println(">>> Loaded " + cnt + " values into cache.");
        }
        catch (SQLException e) {
            throw new CacheLoaderException("Failed to load values from cache store.", e);
        }
    }
}
