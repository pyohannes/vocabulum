package org.vocabulum.persist;

import junit.framework.TestCase;
import java.io.File;

import org.vocabulum.persist.PersistFactory;
import org.vocabulum.persist.PersistDriver;

public class XStreamDriverTest extends PersistDriverTest {

    private String databasePath = null;

    @Override
    protected void setUp() throws Exception {
        File tempFile = File.createTempFile("vocabulum-", ".xml");
        tempFile.delete();
        tempFile.deleteOnExit();
        databasePath = tempFile.getPath();
    }

    protected PersistDriver getPersistDriver() throws Exception {
        return PersistFactory.createXStream(databasePath);
    }
}
