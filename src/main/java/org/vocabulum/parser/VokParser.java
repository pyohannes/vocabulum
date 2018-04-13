package org.vocabulum.parser;

import java.util.List;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.io.BufferedReader;
import java.io.IOException;

import org.vocabulum.data.Word;
import org.vocabulum.data.Relation;
import org.vocabulum.parser.VokParserError;


public class VokParser {

    private Pattern wordPattern = Pattern.compile(
            //"(.*)");
            //"([^)(]*)");
            //"([^(]*)([^)]*).*");
            "([^(]*)(?:\\(([^)]*)\\))?");

    public List<Relation> parse(BufferedReader reader) 
        throws VokParserError
    {
        String line;
        List<Relation> relations = new ArrayList<Relation>();

        try {
            while ((line = reader.readLine()) != null) {
                Relation r = parseLine(line);
    
                if (r != null) {
                    relations.add(r);
                }
            }
        } catch (IOException e) {
            throw new VokParserError(e.getMessage());
        }

        return relations;
    }

    private Relation parseLine(String line) throws VokParserError {
        String[] parts = line.split("<->");
        if (parts.length != 2) {
            throw new VokParserError("No <-> in: " + line);
        }

        Relation r = new Relation()
            .setDirection(Relation.BOTH);

        for (Word w : parseWords(parts[0])) {
            r.addLeft(w);
        }
        for (Word w : parseWords(parts[1])) {
            r.addRight(w);
        }

        return r;
    }

    private List<Word> parseWords(String s) throws VokParserError {
        List<Word> words = new ArrayList<Word>();

        for (String wordFull : s.split("\\|")) {
            Matcher m = wordPattern.matcher(wordFull);

            if (!m.find()) {
                throw new VokParserError("Syntax error in: " + wordFull);
            }

            String word = m.group(1).trim();
            String annotation = null;
           
            if (m.group(2) != null) {
                annotation = m.group(2).trim();
            }

            words.add(new Word(word, annotation));
        }

        return words;
    }
}
