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

    private Set<Relation> parseLines(String line) throws VokParserError {
        VokParser p = new VokParser();
        Set<Relation> rs = null;

        try (StringReader sReader = new StringReader(line);
             BufferedReader reader = new BufferedReader(sReader)
        ) {
            rs = p.parse(reader);
        } catch (IOException e) {
            fail("IOException");
        }

        return rs;
    }

    @Test
    public void parseSimpleBidirectionalRelation() throws Exception {
        Set<Relation> rs = parseLines("regieren <-> regnare");
        assertEquals(rs.size(), 1);

        Relation r = rs.iterator().next();
        assertEquals(r.getDirection(), Relation.BOTH);
        assertEquals(
                r.getLeft(),
                Stream.of(new Word("regieren")).collect(Collectors.toSet()));
        assertEquals(
                r.getRight(),
                Stream.of(new Word("regnare")).collect(Collectors.toSet()));
    }

    @Test
    public void parseMultiBidirectionalRelation() throws Exception {
        Set<Relation> rs = parseLines("regieren | herrschen <-> regnare");
        assertEquals(rs.size(), 1);

        Relation r = rs.iterator().next();
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
    public void parseSimpleBidirectionalAnnotatedRelation() throws Exception {
        Set<Relation> rs = parseLines("ancilla,ae (f) <-> Sklavin");
        assertEquals(rs.size(), 1);

        Relation r = rs.iterator().next();
        assertEquals(r.getDirection(), Relation.BOTH);
        assertEquals(
                r.getLeft(),
                Stream.of(new Word("ancilla,ae", "f")).collect(Collectors.toSet()));
        assertEquals(
                r.getRight(),
                Stream.of(new Word("Sklavin")).collect(Collectors.toSet()));
    }

    @Test
    public void parseMultiBidirectionalAnnotatedRelation() throws Exception {
        Set<Relation> rs = parseLines("ancilla,ae (f) <-> Sklavin | Magd");
        assertEquals(rs.size(), 1);

        Relation r = rs.iterator().next();
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

    @Test
    public void parseEmptyLines() throws Exception {
        Set<Relation> rs = parseLines("ancilla,ae (f) <-> Sklavin | Magd\n\n");
        assertEquals(rs.size(), 1);
    }

    @Test
    public void parseComment() throws Exception {
        Set<Relation> rs = parseLines("ancilla,ae (f) <-> Sklavin | Magd # <-> comment");
        assertEquals(rs.size(), 1);
    }

    @Test
    public void parseCommentLines() throws Exception {
        Set<Relation> rs = parseLines("#ancilla,ae (f) <-> Sklavin | Magd");
        assertEquals(rs.size(), 0);
    }

    @Test(expected = VokParserError.class)
    public void parseSyntaxRelationError() throws Exception {
        parseLines("ancilla,ae (f) - Sklavin | Magd");
    }

    @Test(expected = VokParserError.class)
    public void parseSyntaxWordError() throws Exception {
        parseLines("ancilla,ae (f) <->(Sklavin)");
    }
}
