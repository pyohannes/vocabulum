package org.vocabulum.persist;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.fail;

import org.junit.Test;
import junit.framework.TestCase;
import java.io.File;
import java.io.BufferedReader;
import java.io.StringReader;
import java.util.Set;
import java.util.List;
import java.util.Arrays;

import org.vocabulum.data.Unit;
import org.vocabulum.data.Relation;
import org.vocabulum.parser.VokParser;
import org.vocabulum.parser.VokParserError;
import org.vocabulum.persist.PersistFactory;
import org.vocabulum.persist.PersistDriver;
import org.vocabulum.report.PlainTextReporter;

public class SqliteDriverTest extends PersistDriverTest {

    private String databasePath = null;

    @Override
    protected void setUp() throws Exception {
        File tempFile = File.createTempFile("vocabulum-", ".db");
        tempFile.deleteOnExit();
        databasePath = tempFile.getPath();
    }

    protected PersistDriver getPersistDriver() throws Exception {
        return PersistFactory.createSqlite(databasePath);
    }
}
