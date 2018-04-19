package org.vocabulum.data;

import static org.junit.Assert.assertEquals;

import java.util.stream.Stream;
import java.util.stream.Collectors;

import org.junit.Test;

import org.vocabulum.data.Word;
import org.vocabulum.data.Relation;

public class RelationTest {

    private Relation createAndAssertSimpleUnidirectionalRelation() {
        Word left = new Word("regnare");
        Word right = new Word("regieren");

        Relation r = new Relation()
            .addLeft(left)
            .addRight(right);

        assertEquals(
                r.getLeft(), 
                Stream.of(left).collect(Collectors.toSet()));
        assertEquals(
                r.getRight(), 
                Stream.of(right).collect(Collectors.toSet()));
        assertEquals(r.getDirection(), Relation.LEFT_TO_RIGHT);

        return r;
    }

    @Test
    public void createsSimpleUnidirectionalRelation() {
        createAndAssertSimpleUnidirectionalRelation();
    }

    @Test
    public void createsSimpleUnidirectionalReversedRelation() {
        Relation r = createAndAssertSimpleUnidirectionalRelation()
            .setDirection(Relation.RIGHT_TO_LEFT);

        assertEquals(r.getDirection(), Relation.RIGHT_TO_LEFT);
    }

    @Test
    public void createsSimpleBidirectionalReversedRelation() {
        Relation r = createAndAssertSimpleUnidirectionalRelation()
            .setDirection(Relation.BOTH);

        assertEquals(r.getDirection(), Relation.BOTH);
    }
}
