package org.vocabulum;

import java.util.Set;
import java.io.BufferedReader;
import java.io.StringReader;

import org.vocabulum.data.Relation;
import org.vocabulum.parser.VokParser;
import org.vocabulum.parser.VokParserError;
import org.vocabulum.question.PlainTextQuestioner;


public class TestUtil {

    public static Set<Relation> makeRelations(String vokStr) throws Exception {
        VokParser parser = new VokParser();
        Set<Relation> rs = null;
        try (StringReader sReader = new StringReader(vokStr);
             BufferedReader reader = new BufferedReader(sReader)
        ) {
            rs = parser.parse(reader);
        } catch (Exception e) {
            throw e;
        }
        return rs;
    }
}
