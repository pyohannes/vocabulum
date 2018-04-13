package org.vocabulum.data;

import java.util.Objects;
import java.util.List;
import java.util.ArrayList;

import org.vocabulum.data.Relation;
import org.vocabulum.data.Data;


public class Unit extends Data {
    private String name;
    private List<Relation> relations;

    public Unit(String name, List<Relation> relations) {
        super(0);
        this.name = name;
        this.relations = new ArrayList(relations);
    }

    public String getName() {
        return name;
    }

    public List<Relation> getRelations() {
        return relations;
    }
}
