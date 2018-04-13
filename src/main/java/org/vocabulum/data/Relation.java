package org.vocabulum.data;

import java.util.List;
import java.util.ArrayList;
import java.util.Objects;

import org.vocabulum.data.Word;
import org.vocabulum.data.Data;

public class Relation extends Data {

    public final static short LEFT_TO_RIGHT = 0, RIGHT_TO_LEFT = 1, BOTH = 2;

    private ArrayList<Word> left;
    private ArrayList<Word> right;
    private int direction;

    public Relation() {
        super(0);
        left = new ArrayList<Word>();
        right = new ArrayList<Word>();
        direction = LEFT_TO_RIGHT;
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

    public List<Word> getLeft() {
        return left;
    }

    public List<Word> getRight() {
        return right;
    }

    public int getDirection() {
        return direction;
    }
}
