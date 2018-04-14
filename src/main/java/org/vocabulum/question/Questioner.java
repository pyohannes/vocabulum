package org.vocabulum.question;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import org.vocabulum.data.Relation;
import org.vocabulum.report.Reporter;


public abstract class Questioner<T> implements Iterator<T> {
    private List<Reporter> reporters;
    private List<Relation> relations;

    public Questioner() {
        reporters = new ArrayList<>();
        relations = new ArrayList<>();
    }

    public Questioner<T> registerReporter(Reporter r) {
        reporters.add(r);
        return this;
    }

    public Questioner<T> addRelations(List<Relation> r) {
        relations.addAll(r);
        Collections.shuffle(relations);

        return this;
    }

    protected List<Relation> getRelations() {
        return relations;
    }

    protected void reportAnswer(Relation r, boolean correct) {
        for (Reporter reporter : reporters) {
            reporter.addAnswer(r, correct);
        }
    }

    public abstract boolean answer(String ans);
}
