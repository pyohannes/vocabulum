package org.vocabulum.persist;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;

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
import org.vocabulum.report.Reporter;


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

              "CREATE TABLE IF NOT EXISTS assessment ("
            + " id integer PRIMARY KEY,"
            + " rate integer,"
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

    private String getStringQArray(int size) {
        List<String> s = new ArrayList<>();
        while (size > 0) {
            s.add("?");
            size -= 1;
        }

        return "(" + String.join(",", s) + ")";
    }

    public void storeReporter(Reporter r) throws PersistError {
        List<Integer> relationIds = new ArrayList<Integer>();
        for (Reporter.Answer answer : r.getAnswers()) {
            relationIds.add(answer.relation.getId());
        }

        HashMap<Integer, Integer> assessments = new HashMap<>();

        String selectAssessments = "SELECT * FROM assessment WHERE id IN " + getStringQArray(relationIds.size());
        try (PreparedStatement pStmt = conn.prepareStatement(selectAssessments)) 
        {
            for (int i = 0; i < relationIds.size(); i++) {
                pStmt.setInt(i + 1, relationIds.get(i));
            }
            ResultSet rs = pStmt.executeQuery();
            while (rs.next()) {
                assessments.put(rs.getInt("id"), rs.getInt("rate"));
            }
        } catch (SQLException e) {
            throw new PersistError(e.getMessage());
        }

        String updateAssessment = "UPDATE assessment SET rate = ? WHERE relation = ?";
        String insertAssessment = "INSERT INTO assessment(relation, rate) VALUES (?, ?)";
        for (Reporter.Answer answer : r.getAnswers()) {
            int id = answer.relation.getId();
            if (assessments.containsKey(id)) {
                Integer rate = assessments.get(id);
                if (answer.correct) {
                    rate = rate + 1;
                } else {
                    rate = rate - 6;
                }
                assessments.put(id, rate);
                try (PreparedStatement pStmt = conn.prepareStatement(updateAssessment)) 
                {
                    pStmt.setInt(1, assessments.get(id));
                    pStmt.setInt(2, id);
                    pStmt.executeUpdate();
                } catch (SQLException e) {
                    throw new PersistError(e.getMessage());
                }
            } else {
                try (PreparedStatement pStmt = conn.prepareStatement(insertAssessment)) 
                {
                    pStmt.setInt(1, id);
                    if (answer.correct) {
                        pStmt.setInt(2, 1);
                    } else {
                        pStmt.setInt(2, 0);
                    } 
                    pStmt.executeUpdate();
                } catch (SQLException e) {
                    throw new PersistError(e.getMessage());
                }
            }
        }
    }

    public Set<Relation> getRelationsWithWorstAssessment(int maxRet) throws PersistError {
        String selectAssessments = "SELECT relation, rate FROM assessment ORDER BY rate ASC LIMIT ?";

        List<Integer> relationIds = new ArrayList<>();
        try (PreparedStatement pStmt = conn.prepareStatement(selectAssessments)) 
        {
            pStmt.setInt(1, maxRet);
            ResultSet rs = pStmt.executeQuery();

            while (rs.next()) {
                relationIds.add(rs.getInt("relation"));
            }
        } catch (SQLException e) {
            throw new PersistError(e.getMessage());
        }

        String selectRelation = "SELECT * FROM relation WHERE id IN " + getStringQArray(relationIds.size());
        Set<Relation> relations = new HashSet<>();
        try (PreparedStatement pStmt = conn.prepareStatement(selectRelation)) 
        {
            for (int i = 0; i < relationIds.size(); i++) {
                pStmt.setInt(i + 1, relationIds.get(i));
            }
            ResultSet rs = pStmt.executeQuery();

            while (rs.next()) {
                Relation r = new Relation();
                r.setId(rs.getInt("id"));
                r.setDirection(rs.getInt("direction"));
                retrieveWords(r);
                relations.add(r);
            }
        } catch (SQLException e) {
            throw new PersistError(e.getMessage());
        }

        if (relations.size() < maxRet) {
            selectRelation = "SELECT * FROM relation WHERE id NOT IN " + getStringQArray(relationIds.size()) + "LIMIT ?";
            try (PreparedStatement pStmt = conn.prepareStatement(selectRelation)) 
            {
                int i;
                for (i = 0; i < relationIds.size(); i++) {
                    pStmt.setInt(i + 1, relationIds.get(i));
                }
                pStmt.setInt(i + 1, maxRet - relations.size());
                ResultSet rs = pStmt.executeQuery();
    
                while (rs.next()) {
                    Relation r = new Relation();
                    r.setId(rs.getInt("id"));
                    r.setDirection(rs.getInt("direction"));
                    retrieveWords(r);
                    relations.add(r);
                }
            } catch (SQLException e) {
                throw new PersistError(e.getMessage());
            }
        }

        return relations;
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
        String insertAssessment = "INSERT INTO assessment(relation,rate) "
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
            try (PreparedStatement pStmt = conn.prepareStatement(insertAssessment)) 
            {
                pStmt.setInt(1, r.getId());
                pStmt.setInt(2, 0);
                pStmt.executeUpdate();
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
