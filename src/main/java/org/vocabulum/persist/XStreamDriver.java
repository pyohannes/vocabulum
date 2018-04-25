package org.vocabulum.persist;

import java.util.HashMap;
import java.util.ArrayList;

import java.io.IOException;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Files;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.extended.NamedMapConverter;

import org.vocabulum.data.Unit;
import org.vocabulum.data.Relation;
import org.vocabulum.data.Word;
import org.vocabulum.persist.CompletePersistDriver;
import org.vocabulum.persist.PersistError;


public class XStreamDriver extends CompletePersistDriver {
    private Path file;
    private XStream xstream;

    public XStreamDriver(String filename) throws PersistError {
        super();
        file = new File(filename).toPath();
        xstream = new XStream();
        xstream.alias("data", CompletePersistDriver.CompleteData.class);
        xstream.alias("map", HashMap.class);
        xstream.alias("unit", Unit.class);
        xstream.alias("relation", Relation.class);
        xstream.alias("word", Word.class);
        load();
    }

    protected void store() throws PersistError {
        try {
            Files.write(
                    file,
                    xstream.toXML(getCompleteData()).getBytes());
        } catch (IOException e) {
            throw new PersistError(e.getMessage());
        }
    }

    protected void load() throws PersistError {
        try {
            if (file.toFile().exists()) {
                String xml = new String(Files.readAllBytes(file));
                setCompleteData((CompleteData)xstream.fromXML(xml));
            }
        } catch (IOException e) {
            throw new PersistError(e.getMessage());
        }
    }
}
