package org.vocabulum.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;

import org.vocabulum.data.Word;

public class WordTest {

    @Test
    public void createsWord() {
        Word w = new Word("pugnare");

        assertEquals(w.getText(), "pugnare");
        assertEquals(w.getAnnotation(), null);
    }

    @Test
    public void createsAnnotatedWord() {
        Word w = new Word("ancilla,ae", "f");

        assertEquals(w.getText(), "ancilla,ae");
        assertEquals(w.getAnnotation(), "f");
    }

    @Test
    public void compareWords() {
        Word w1 = new Word("incola,ae", "f");
        Word w2 = new Word("incola,ae", "f");
        Word w3 = new Word("incola", "f");
        Word w4 = new Word("incola", "m");

        assertEquals(w1, w2);
        assertNotEquals(w1, w3);
        assertNotEquals(w1, "x");
        assertNotEquals(w3, w4);
    }

}
