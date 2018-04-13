package org.vocabulum.persist;

import java.util.List;

import org.vocabulum.data.Unit;
import org.vocabulum.persist.PersistError;


public abstract class PersistDriver {
    public abstract Unit retrieveUnitByName(String name) throws PersistError;
    public abstract void storeUnit(Unit u) throws PersistError;
    public abstract List<String> getUnitNames() throws PersistError;
}
