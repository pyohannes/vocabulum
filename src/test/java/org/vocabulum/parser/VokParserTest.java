package org.vocabulum.parser;

import java.util.Set;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.StringReader;
import java.io.IOException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Test;

import org.vocabulum.data.Word;
import org.vocabulum.data.Relation;
import org.vocabulum.parser.VokParser;

public class VokParserTest {

    private Relation parseLine(String line) {
        VokParser p = new VokParser();

        Set<Relation> rs = null;
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

        return new ArrayList<Relation>(rs).get(0);
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
        assertEquals(
                r.getLeft(),
                Stream.of(new Word("regieren")).collect(Collectors.toSet()));
        assertEquals(
                r.getRight(),
                Stream.of(new Word("regnare")).collect(Collectors.toSet()));
    }

    @Test
    public void parseMultiBidirectionalRelation() {
        Relation r = parseLine("regieren | herrschen <-> regnare");

        assertEquals(r.getDirection(), Relation.BOTH);
        assertEquals(
                r.getLeft(),
                Stream
                    .of(new Word("regieren"), new Word("herrschen"))
                    .collect(Collectors.toSet()));
        assertEquals(
                r.getRight(),
                Stream.of(new Word("regnare")).collect(Collectors.toSet()));
    }

    @Test
    public void parseSimpleBidirectionalAnnotatedRelation() {
        Relation r = parseLine("ancilla,ae (f) <-> Sklavin");

        assertEquals(r.getDirection(), Relation.BOTH);
        assertEquals(
                r.getLeft(),
                Stream.of(new Word("ancilla,ae", "f")).collect(Collectors.toSet()));
        assertEquals(
                r.getRight(),
                Stream.of(new Word("Sklavin")).collect(Collectors.toSet()));
    }

    @Test
    public void parseMultiBidirectionalAnnotatedRelation() {
        Relation r = parseLine("ancilla,ae (f) <-> Sklavin | Magd");

        assertEquals(r.getDirection(), Relation.BOTH);
        assertEquals(
                r.getLeft(),
                Stream.of(new Word("ancilla,ae", "f")).collect(Collectors.toSet()));
        assertEquals(
                r.getRight(),
                Stream
                    .of(new Word("Sklavin"), new Word("Magd"))
                    .collect(Collectors.toSet()));
    }
}
