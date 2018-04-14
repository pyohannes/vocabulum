package org.vocabulum.question;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.fail;
import static org.vocabulum.TestUtil.makeRelations;

import org.junit.Test;
import junit.framework.TestCase;
import java.util.List;

import org.vocabulum.data.Relation;
import org.vocabulum.question.PlainTextQuestioner;
import org.vocabulum.report.PlainTextReporter;


public class PlainTextReporterTest extends TestCase {

    public void testQuestionSimpleEmpty() throws Exception {
        List<Relation> rs = makeRelations(
                "a <-> a\n" +
                "a <-> a\n" +
                "a <-> a\n");

        PlainTextReporter reporter = new PlainTextReporter();
        Questioner qu = new PlainTextQuestioner()
            .addRelations(rs)
            .registerReporter(reporter);

        qu.next();
        assertFalse(qu.answer("b"));
        qu.next();
        assertTrue(qu.answer("a"));
        qu.next();
        assertTrue(qu.answer("a"));

        assertEquals(reporter.getAnswerCount(), 3);
        assertEquals(reporter.getRightAnswerCount(), 2);
        assertEquals(reporter.getWrongAnswerCount(), 1);
        assertEquals(reporter.getRightAnswerPercentage(), 66);
        assertEquals(reporter.getWrongAnswerPercentage(), 33);
    }
}
