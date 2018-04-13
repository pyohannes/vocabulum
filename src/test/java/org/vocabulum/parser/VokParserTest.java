package org.vocabulum.parser;

import java.util.List;
import java.io.BufferedReader;
import java.io.StringReader;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Test;

import org.vocabulum.data.Word;
import org.vocabulum.data.Relation;
import org.vocabulum.parser.VokParser;

public class VokParserTest {

    private Relation parseLine(String line) {
        VokParser p = new VokParser();

        List<Relation> rs = null;
        try (StringReader sReader = new StringReader(line);
             BufferedReader reader = new BufferedReader(sReader)
        ) {
            rs = p.parse(reader);
        } catch (VokParserError e) {
            fail("VokParserError");
        } catch (IOException e) {
            fail("IOException");
        }
        assertEquals(rs.size(), 1);

        return rs.get(0);
    }

    /*
    @Test
    public void parseSimpleUnidirectionalRelation() {
        Relation r = parseLine("regnare -> regieren");

        assertEquals(r.getDirection(), Relation.Direction.LEFT_TO_RIGHT);
        assertEquals(r.getLeft().size(), 1);
        assertEquals(r.getRight().size(), 1);
        assertEquals(r.getLeft().get(0), new Word("regnare"));
        assertEquals(r.getRight().get(0), new Word("regieren"));
    }

    @Test
    public void createsSimpleUnidirectionalReversedRelation() {
        Relation r = parseLine("regieren <- regnare");

        assertEquals(r.getDirection(), Relation.Direction.RIGHT_TO_LEFT);
        assertEquals(r.getLeft().size(), 1);
        assertEquals(r.getRight().size(), 1);
        assertEquals(r.getLeft().get(0), new Word("regieren"));
        assertEquals(r.getRight().get(0), new Word("regnare"));
    }
    */

    @Test
    public void parseSimpleBidirectionalRelation() {
        Relation r = parseLine("regieren <-> regnare");

        assertEquals(r.getDirection(), Relation.BOTH);
        assertEquals(r.getLeft().size(), 1);
        assertEquals(r.getRight().size(), 1);
        assertEquals(r.getLeft().get(0), new Word("regieren"));
        assertEquals(r.getRight().get(0), new Word("regnare"));
    }

    @Test
    public void parseMultiBidirectionalRelation() {
        Relation r = parseLine("regieren | herrschen <-> regnare");

        assertEquals(r.getDirection(), Relation.BOTH);
        assertEquals(r.getLeft().size(), 2);
        assertEquals(r.getRight().size(), 1);
        assertEquals(r.getLeft().get(0), new Word("regieren"));
        assertEquals(r.getLeft().get(1), new Word("herrschen"));
        assertEquals(r.getRight().get(0), new Word("regnare"));
    }

    @Test
    public void parseSimpleBidirectionalAnnotatedRelation() {
        Relation r = parseLine("ancilla,ae (f) <-> Sklavin");

        assertEquals(r.getDirection(), Relation.BOTH);
        assertEquals(r.getLeft().size(), 1);
        assertEquals(r.getRight().size(), 1);
        assertEquals(r.getLeft().get(0), new Word("ancilla,ae", "f"));
        assertEquals(r.getRight().get(0), new Word("Sklavin"));
    }

    @Test
    public void parseMultiBidirectionalAnnotatedRelation() {
        Relation r = parseLine("ancilla,ae (f) <-> Sklavin | Magd");

        assertEquals(r.getDirection(), Relation.BOTH);
        assertEquals(r.getLeft().size(), 1);
        assertEquals(r.getRight().size(), 2);
        assertEquals(r.getLeft().get(0), new Word("ancilla,ae", "f"));
        assertEquals(r.getRight().get(0), new Word("Sklavin"));
        assertEquals(r.getRight().get(1), new Word("Magd"));
    }
}
