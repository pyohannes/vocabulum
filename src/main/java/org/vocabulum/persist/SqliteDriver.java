package org.vocabulum.persist;

import java.util.List;
import java.util.ArrayList;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.vocabulum.data.Data;
import org.vocabulum.data.Unit;
import org.vocabulum.data.Relation;
import org.vocabulum.data.Word;
import org.vocabulum.persist.PersistError;


public class SqliteDriver extends PersistDriver {

    private Connection conn;

    public SqliteDriver(String path) throws PersistError {
        String url = "jdbc:sqlite:" + path;

        try {
            conn = DriverManager.getConnection(url);
            initializeTables();
        } catch (SQLException e) {
            throw new PersistError(e.getMessage());
        }
    }

    private void initializeTables() throws SQLException {
        String[] sqlTables = {
              "CREATE TABLE IF NOT EXISTS unit ("
            + " id integer PRIMARY KEY,"
            + " name text NOT NULL"
            + ");",
              
              "CREATE TABLE IF NOT EXISTS relation (" 
            + " id integer PRIMARY KEY,"
            + " direction integer,"
            + " unit integer,"
            + " FOREIGN KEY(unit) REFERENCES unit(id)"
            + ");",

              "CREATE TABLE IF NOT EXISTS word ("
            + " id integer PRIMARY KEY,"
            + " word text NOT NULL,"
            + " annotation text,"
            + " leftright integer,"
            + " relation integer,"
            + " FOREIGN KEY(relation) REFERENCES relation(id)"
            + ");",
        };

        try (Statement stmt = conn.createStatement()) 
        {
            for (String sql : sqlTables) {
                stmt.execute(sql);
            }
        } catch (SQLException e) {
            throw e;
        }
    }

    public Unit retrieveUnitByName(String name) throws PersistError {
        String selectUnit = "SELECT id, name FROM unit WHERE name = ?";

        try (PreparedStatement pStmt = conn.prepareStatement(selectUnit)) 
        {
            pStmt.setString(1, name);
            ResultSet rs = pStmt.executeQuery();
            rs.next();
            Unit u = new Unit(name, retrieveRelations(rs.getInt("id")));
            u.setId(rs.getInt("id"));
            return u;
        } catch (SQLException e) {
            throw new PersistError(e.getMessage());
        }
    }

    private List<Relation> retrieveRelations(int unitId) throws SQLException {
        ArrayList<Relation> relations = new ArrayList<Relation>();

        String selectRelation = "SELECT * FROM relation where unit = ?";
        try (PreparedStatement pStmt = conn.prepareStatement(selectRelation)) 
        {
            pStmt.setInt(1, unitId);
            ResultSet rs = pStmt.executeQuery();

            while (rs.next()) {
                Relation r = new Relation();
                r.setId(rs.getInt("id"));
                r.setDirection(rs.getInt("direction"));
                retrieveWords(r);
                relations.add(r);
            }
        } catch (SQLException e) {
            throw e;
        }

        return relations;
    }

    private void retrieveWords(Relation r) throws SQLException {
        String selectRelation = "SELECT * FROM word where relation = ?";
        try (PreparedStatement pStmt = conn.prepareStatement(selectRelation)) 
        {
            pStmt.setInt(1, r.getId());
            ResultSet rs = pStmt.executeQuery();

            while (rs.next()) {
                Word w = new Word(rs.getString("word"), 
                        rs.getString("annotation"));
                w.setId(rs.getInt("id"));
                if (rs.getInt("leftright") == 0) {
                    r.addLeft(w);
                } else {
                    r.addRight(w);
                }

            }
        } catch (SQLException e) {
            throw e;
        }
    }

    public void storeUnit(Unit u) throws PersistError {
        String insertUnit = "INSERT INTO unit(name) VALUES (?)";
        try (PreparedStatement pStmt = conn.prepareStatement(insertUnit)) 
        {
            pStmt.setString(1, u.getName());
            pStmt.executeUpdate();
            setKeyFromStmt(pStmt, u);
        } catch (SQLException e) {
            throw new PersistError(e.getMessage());
        }

        String insertRelation = "INSERT INTO relation(direction,unit) "
                              + "VALUES (?,?)";
        String insertWord = "INSERT INTO word(word,annotation,relation,"
                          + "leftright) VALUES (?,?,?,?)";

        for (Relation r : u.getRelations()) {
            try (PreparedStatement pStmt = conn.prepareStatement(insertRelation)) 
            {
                pStmt.setInt(1, r.getDirection());
                pStmt.setInt(2, u.getId());
                pStmt.executeUpdate();
                setKeyFromStmt(pStmt, r);
            } catch (SQLException e) {
                throw new PersistError(e.getMessage());
            }

            for (Word w : r.getLeft()) {
                try (PreparedStatement pStmt = conn.prepareStatement(insertWord)) 
                {
                    pStmt.setString(1, w.getText());
                    pStmt.setString(2, w.getAnnotation());
                    pStmt.setInt(3, r.getId());
                    pStmt.setInt(4, 0);
                    pStmt.executeUpdate();
                    setKeyFromStmt(pStmt, w);
                } catch (SQLException e) {
                    throw new PersistError(e.getMessage());
                }
            }

            for (Word w : r.getRight()) {
                try (PreparedStatement pStmt = conn.prepareStatement(insertWord)) 
                {
                    pStmt.setString(1, w.getText());
                    pStmt.setString(2, w.getAnnotation());
                    pStmt.setInt(3, r.getId());
                    pStmt.setInt(4, 1);
                    pStmt.executeUpdate();
                    setKeyFromStmt(pStmt, w);
                } catch (SQLException e) {
                    throw new PersistError(e.getMessage());
                }
            }
        }
    }

    void setKeyFromStmt(Statement stmt, Data data) throws SQLException {
        ResultSet keys = stmt.getGeneratedKeys();
        keys.next();
        int newKey = keys.getInt(1);
        data.setId(newKey);
    }

    public List<String> getUnitNames() throws PersistError {
        String selectUnitNames = "SELECT name FROM unit";

        try (PreparedStatement pStmt = conn.prepareStatement(selectUnitNames)) 
        {
            ResultSet rs = pStmt.executeQuery();
            List<String> result = new ArrayList<String>();

            while (rs.next()) {
                result.add(rs.getString("name"));
            }

            return result;
        } catch (SQLException e) {
            throw new PersistError(e.getMessage());
        }
    }

}
