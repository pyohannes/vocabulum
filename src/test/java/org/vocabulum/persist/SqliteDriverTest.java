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
import java.util.List;
import java.util.Arrays;

import org.vocabulum.data.Unit;
import org.vocabulum.data.Relation;
import org.vocabulum.parser.VokParser;
import org.vocabulum.parser.VokParserError;
import org.vocabulum.persist.PersistFactory;
import org.vocabulum.persist.PersistDriver;
import org.vocabulum.report.PlainTextReporter;

public class SqliteDriverTest extends TestCase {

    private String databasePath = null;

    @Override
    protected void setUp() throws Exception {
        File tempFile = File.createTempFile("vocabulum-", ".db");
        tempFile.deleteOnExit();
        databasePath = tempFile.getPath();
    }

    private List<Relation> persistUnit(String name, String vokStr) throws Exception {
        VokParser parser = new VokParser();
        List<Relation> rs = null;
        try (StringReader sReader = new StringReader(vokStr);
             BufferedReader reader = new BufferedReader(sReader)
        ) {
            rs = parser.parse(reader);
        } catch (Exception e) {
            throw e;
        }
        Unit unit = new Unit(name, rs);
        
        PersistDriver driver = PersistFactory.createSqlite(databasePath);
        driver.storeUnit(unit);

        return rs;
    }

    public void testPersistSimpleUnit() throws Exception {
        List<Relation> rs = persistUnit("Lectio I",
                "incola,ae (f) <-> der Einwohner");

        PersistDriver driver = PersistFactory.createSqlite(databasePath);

        Unit u = driver.retrieveUnitByName("Lectio I");

        assertEquals(u.getRelations(), rs);
    }

    public void testGetUnitNames() throws Exception {
        persistUnit("Lectio I",
                "incola,ae (f) <-> der Einwohner");
        persistUnit("Lectio II",
                "populus,i (m) <-> das Volk");

        PersistDriver driver = PersistFactory.createSqlite(databasePath);

        assertEquals(
                driver.getUnitNames(), 
                Arrays.asList(new String[]{ "Lectio I", "Lectio II" }));
    }

    public void testGetRelationsWithWorstAssessment() throws Exception {
        List<Relation> unit1 = persistUnit("Lectio I",
                "incola,ae (f) <-> der Einwohner");
        List<Relation> unit2 = persistUnit("Lectio II",
                "populus,i (m) <-> das Volk");

        PersistDriver driver = PersistFactory.createSqlite(databasePath);

        PlainTextReporter reporter = new PlainTextReporter();
        reporter.addAnswer(unit1.get(0), true);
        reporter.addAnswer(unit2.get(0), false);

        driver.storeReporter(reporter);

        List<Relation> worst = driver.getRelationsWithWorstAssessment(1);

        assertEquals(worst.get(0), unit2.get(0));
    }

    public void testGetRelationsWithWorstAssessmentNoAssessment() throws Exception {
        List<Relation> unit1 = persistUnit("Lectio I",
                "incola,ae (f) <-> der Einwohner");
        List<Relation> unit2 = persistUnit("Lectio II",
                "populus,i (m) <-> das Volk");

        PersistDriver driver = PersistFactory.createSqlite(databasePath);

        List<Relation> worst = driver.getRelationsWithWorstAssessment(2);
        assertEquals(worst.size(), 2);
    }
}
