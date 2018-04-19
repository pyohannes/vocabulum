package org.vocabulum.data;

import java.util.Objects;
import java.util.Set;
import java.util.HashSet;
import java.util.Collection;

import org.vocabulum.data.Relation;
import org.vocabulum.data.Data;


public class Unit extends Data {
    private String name;
    private Set<Relation> relations;

    public Unit(String name, Collection<Relation> relations) {
        super(0);
        this.name = name;
        this.relations = new HashSet<>(relations);
    }

    public String getName() {
        return name;
    }

    public Set<Relation> getRelations() {
        return relations;
    }
}
