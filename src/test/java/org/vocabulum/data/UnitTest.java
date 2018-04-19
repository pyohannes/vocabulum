package org.vocabulum.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotSame;

import org.junit.Test;
import java.util.Set;
import java.util.HashSet;

import org.vocabulum.data.Word;
import org.vocabulum.data.Relation;
import org.vocabulum.data.Unit;

public class UnitTest {

    @Test
    public void createsUnit() {
        Word w1 = new Word("incola,ae", "f");
        Word w2 = new Word("der Einwohner");
        Relation r = new Relation()
            .addLeft(w1)
            .addRight(w2);
        Set<Relation> rs = new HashSet<>();
        rs.add(r);

        Unit u = new Unit("Lectio I", rs);

        assertEquals(u.getName(), "Lectio I");

        Set<Relation> uRs = u.getRelations();
        assertEquals(uRs, rs);
        assertNotSame(uRs, rs);
    }
}
