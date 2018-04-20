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
import org.vocabulum.persist.PersistDriver;
import org.vocabulum.report.PlainTextReporter;

public abstract class PersistDriverTest extends TestCase {

    protected abstract PersistDriver getPersistDriver() throws Exception;

    private Set<Relation> persistUnit(String name, String vokStr) throws Exception {
        VokParser parser = new VokParser();
        Set<Relation> rs = null;
        try (StringReader sReader = new StringReader(vokStr);
             BufferedReader reader = new BufferedReader(sReader)
        ) {
            rs = parser.parse(reader);
        } catch (Exception e) {
            throw e;
        }
        Unit unit = new Unit(name, rs);
        
        PersistDriver driver = getPersistDriver();
        driver.storeUnit(unit);

        return rs;
    }

    public void testPersistSimpleUnit() throws Exception {
        Set<Relation> rs = persistUnit("Lectio I",
                "incola,ae (f) <-> der Einwohner");

        PersistDriver driver = getPersistDriver();

        Unit u = driver.retrieveUnitByName("Lectio I");

        assertEquals(u.getRelations(), rs);
    }

    public void testGetUnitNames() throws Exception {
        persistUnit("Lectio I",
                "incola,ae (f) <-> der Einwohner");
        persistUnit("Lectio II",
                "populus,i (m) <-> das Volk");

        PersistDriver driver = getPersistDriver();

        assertEquals(
                driver.getUnitNames(), 
                Arrays.asList(new String[]{ "Lectio I", "Lectio II" }));
    }

    public void testGetRelationsWithWorstAssessment() throws Exception {
        Set<Relation> unit1 = persistUnit("Lectio I",
                "incola,ae (f) <-> der Einwohner");
        Set<Relation> unit2 = persistUnit("Lectio II",
                "populus,i (m) <-> das Volk");

        PersistDriver driver = getPersistDriver();

        PlainTextReporter reporter = new PlainTextReporter();
        unit1.forEach(r -> { reporter.addAnswer(r, true); });
        unit2.forEach(r -> { reporter.addAnswer(r, false); });

        driver.storeReporter(reporter);

        Set<Relation> worst = driver.getRelationsWithWorstAssessment(1);

        assertEquals(worst, unit2);
    }

    public void testGetRelationsWithWorstAssessmentNoAssessment() throws Exception {
        Set<Relation> unit1 = persistUnit("Lectio I",
                "incola,ae (f) <-> der Einwohner");
        Set<Relation> unit2 = persistUnit("Lectio II",
                "populus,i (m) <-> das Volk");

        PersistDriver driver = getPersistDriver();

        Set<Relation> worst = driver.getRelationsWithWorstAssessment(2);
        assertEquals(worst.size(), 2);
    }
}
