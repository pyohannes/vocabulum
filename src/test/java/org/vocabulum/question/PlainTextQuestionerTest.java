package org.vocabulum.question;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.fail;
import static org.vocabulum.question.TestUtil.makeRelations;

import org.junit.Test;
import junit.framework.TestCase;
import java.util.List;

import org.vocabulum.data.Relation;
import org.vocabulum.parser.VokParser;
import org.vocabulum.parser.VokParserError;
import org.vocabulum.question.PlainTextQuestioner;


public class PlainTextQuestionerTest extends TestCase {

    private String databasePath = null;

    @Override
    protected void setUp() throws Exception {
    }

    public void testQuestionSimpleRight() throws Exception {
        List<Relation> rs = makeRelations(
                "incola,ae (f) <-> der Einwohner");

        PlainTextQuestioner qu = new PlainTextQuestioner();
        qu.addRelations(rs);

        assertTrue(qu.hasNext());
        String question = qu.next();
        if (question.equals("incola,ae (f)")) {
            assertTrue(qu.answer("der Einwohner"));
        } else if (question.equals("der Einwohner")) {
            assertTrue(qu.answer("incola,ae"));
        } else {
            fail("Returned " + question);
        }
        assertFalse(qu.hasNext());
    }

    public void testQuestionSimpleWrong() throws Exception {
        List<Relation> rs = makeRelations(
                "incola,ae (f) <-> der Einwohner");

        PlainTextQuestioner qu = new PlainTextQuestioner();
        qu.addRelations(rs);

        assertTrue(qu.hasNext());
        String question = qu.next();
        if (question.equals("incola,ae (f)")) {
            assertFalse(qu.answer("das Haus"));
        } else if (question.equals("der Einwohner")) {
            assertFalse(qu.answer("domus,i"));
        } else {
            fail("Returned " + question);
        }
        assertFalse(qu.hasNext());
    }
}
