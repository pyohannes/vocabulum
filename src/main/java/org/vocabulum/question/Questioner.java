package org.vocabulum.question;

import java.util.Iterator;

import org.vocabulum.data.Relation;


public abstract class Questioner<T> implements Iterator<T> {
    public abstract boolean answer(String ans);
}
