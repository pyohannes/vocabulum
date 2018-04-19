package org.vocabulum.data;

import java.util.Set;
import java.util.HashSet;
import java.util.Objects;

import org.vocabulum.data.Word;
import org.vocabulum.data.Data;

public class Relation extends Data {

    public final static short LEFT_TO_RIGHT = 0, RIGHT_TO_LEFT = 1, BOTH = 2;

    private HashSet<Word> left;
    private HashSet<Word> right;
    private int direction;

    public Relation() {
        super(0);
        left = new HashSet<>();
        right = new HashSet<>();
        direction = LEFT_TO_RIGHT;
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof Relation) 
            && Objects.equals(((Relation) o).getDirection(), direction)
            && Objects.equals(((Relation) o).getLeft(), left)
            && Objects.equals(((Relation) o).getRight(), right);
    }

    @Override
    public String toString() {
        String s = "<Relation \"";
        for (Word w : left) {
            s += w.toString() + " ";
        }
        s += " <-> ";
        for (Word w : right) {
            s += w.toString() + " ";
        }
        s += ">";
        return s;
    }

    public Relation addLeft(Word w) {
        left.add(w);
        return this;
    }

    public Relation addRight(Word w) {
        right.add(w);
        return this;
    }

    public Relation setDirection(int d) {
        direction = d;
        return this;
    }

    public Set<Word> getLeft() {
        return left;
    }

    public Set<Word> getRight() {
        return right;
    }

    public int getDirection() {
        return direction;
    }
}
