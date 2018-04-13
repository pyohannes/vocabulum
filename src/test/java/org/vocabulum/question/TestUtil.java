package org.vocabulum.question;

import java.util.List;
import java.io.BufferedReader;
import java.io.StringReader;

import org.vocabulum.data.Relation;
import org.vocabulum.parser.VokParser;
import org.vocabulum.parser.VokParserError;
import org.vocabulum.question.PlainTextQuestioner;


public class TestUtil {

    public static List<Relation> makeRelations(String vokStr) throws Exception {
        VokParser parser = new VokParser();
        List<Relation> rs = null;
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
