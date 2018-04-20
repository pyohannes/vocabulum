package org.vocabulum.persist;

import java.util.Objects;

import org.vocabulum.persist.PersistDriver;
import org.vocabulum.persist.SqliteDriver;
import org.vocabulum.persist.XStreamDriver;


public class PersistFactory {
    public static PersistDriver createSqlite(String path) throws PersistError {
        return new SqliteDriver(path);
    }

    public static PersistDriver createXStream(String path) throws PersistError {
        return new XStreamDriver(path);
    }
}
