package org.vocabulum.persist;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashMap;
import java.util.Map;
import java.util.Collections;
import java.util.stream.Collectors;

import org.vocabulum.data.Unit;
import org.vocabulum.data.Relation;
import org.vocabulum.report.Reporter;
import org.vocabulum.persist.PersistDriver;
import org.vocabulum.persist.PersistError;


public abstract class CompletePersistDriver extends PersistDriver {
    static class CompleteData {
        public ArrayList<Unit> units = new ArrayList<>();
        public HashMap<Relation, Integer> assessments = new HashMap<>();
        //public Set<Assessment> assessments;
    }
    private CompleteData completeData;

    public CompletePersistDriver() throws PersistError {
        completeData = new CompleteData();
    }

    public CompleteData getCompleteData() {
        return completeData;
    }

    public void setCompleteData(CompleteData data) {
        this.completeData = data;
    }

    public Unit retrieveUnitByName(String name) throws PersistError {
        for (Unit u : completeData.units) {
            if (u.getName().equals(name)) {
                return u;
            }
        }
        throw new PersistError("No unit named '" + name + "'");
    }

    public void storeUnit(Unit u) throws PersistError {
        completeData.units.add(u);
        for (Relation r : u.getRelations()) {
            completeData.assessments.put(r, 0);
        }
        store();
    }

    public void storeReporter(Reporter r) throws PersistError {
        for (Reporter.Answer answer : r.getAnswers()) {
            Integer rate = completeData.assessments.getOrDefault(answer.relation, 0);
            rate = rate + (answer.correct ? 1 : -6);

            completeData.assessments.put(answer.relation, rate);
        }

        store();
    }

    public List<String> getUnitNames() throws PersistError {
        return completeData.units
            .stream()
            .map(u -> { return u.getName(); })
            .collect(Collectors.toList());
    }



    public Set<Relation> getRelationsWithWorstAssessment(int maxRet) throws PersistError {
        List<Map.Entry<Relation, Integer>> entries = new ArrayList<>(completeData.assessments.entrySet());
        Collections.sort(
            entries,
            (e1, e2) -> {
                return e1.getValue().compareTo(e2.getValue());
            });

        return entries
            .subList(0, Math.min(maxRet, completeData.assessments.size()))
            .stream()
            .map(e -> { return e.getKey(); })
            .collect(Collectors.toSet());
    }

    protected abstract void store() throws PersistError;
    protected abstract void load() throws PersistError;
}
