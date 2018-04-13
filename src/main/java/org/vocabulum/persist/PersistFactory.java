package org.vocabulum.persist;

import java.util.Objects;

import org.vocabulum.persist.PersistDriver;
import org.vocabulum.persist.SqliteDriver;


public class PersistFactory {
    public static PersistDriver createSqlite(String path) throws PersistError {
        return new SqliteDriver(path);
    }
}
