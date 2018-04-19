package org.vocabulum.question;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.fail;
import static org.vocabulum.TestUtil.makeRelations;

import org.junit.Test;
import junit.framework.TestCase;
import java.util.Set;

import org.vocabulum.data.Relation;
import org.vocabulum.question.PlainTextQuestioner;
import org.vocabulum.report.PlainTextReporter;


public class PlainTextReporterTest extends TestCase {

    public void testQuestionSimpleEmpty() throws Exception {
        Set<Relation> rs = makeRelations(
                "a <-> a\n" +
                "b <-> b\n" +
                "c <-> c\n");

        PlainTextReporter reporter = new PlainTextReporter();
        Questioner qu = new PlainTextQuestioner()
            .addRelations(rs)
            .registerReporter(reporter);

        qu.next();
        qu.answer("a");
        qu.next();
        qu.answer("a");
        qu.next();
        qu.answer("a");

        assertEquals(reporter.getAnswerCount(), 3);
        assertEquals(reporter.getRightAnswerCount(), 1);
        assertEquals(reporter.getWrongAnswerCount(), 2);
        assertEquals(reporter.getRightAnswerPercentage(), 33);
        assertEquals(reporter.getWrongAnswerPercentage(), 66);
    }
}
