
package sovelluslogiikka;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

/**
 *Hallinnoi yhteydenottoa SQL-tietokantaan ja suorittaa
 *Viesti-olioiden SQL-kyselyt.
 */
public class ViestiDAO implements Dao<Integer, Viesti> {
    private Database database;
    private Connection yhteys;
    
    public ViestiDAO(Database database) throws SQLException {
        this.database = database;
    }

    public void muodostaYhteys() throws SQLException {
        yhteys = database.getConnection();
    }
    
    public void suljeYhteys() throws SQLException {
        yhteys.close();
    }
    
    @Override
    public void delete(Viesti key) throws SQLException {
        PreparedStatement stmt = yhteys.prepareStatement("");
    }

    @Override
    public void add(Viesti viesti) throws SQLException {
        muodostaYhteys();
        PreparedStatement stmt = yhteys.prepareStatement(
                "INSERT INTO Viesti(Viesti, Nimimerkki, Pvm, KetjuId) "
                                            + "VALUES (?, ?, ?, ?);");
        stmt.setString(1, viesti.getViesti());
        stmt.setString(2, viesti.getNimimerkki());
        
        // Viestioliota luodessa jätä pvm null-arvoon
        long pvm = System.currentTimeMillis();
        stmt.setLong(3, pvm);
        stmt.setInt(4, viesti.getKetjuId());
        stmt.execute();
        stmt.close();
        suljeYhteys();
    }

    @Override
    public void update(Viesti key) throws SQLException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Viesti> getAll(Integer ketjuId) throws SQLException {
        muodostaYhteys();
        PreparedStatement stmt = yhteys.prepareStatement(
                "SELECT * FROM Ketju, Viesti "
                    + "WHERE Ketju.Id = Viesti.KetjuId "
                    + "AND Viesti.KetjuId = ?;");
        stmt.setInt(1, ketjuId);
        ResultSet rs = stmt.executeQuery();
        List<Viesti> viestit = new LinkedList<>();
        
        while (rs.next()) {
            int id = rs.getInt("Viesti.Id");
            String viesti = rs.getString("Viesti.viesti");
            String nimimerkki = rs.getString("Viesti.nimimerkki");
            int ketjuhaeId = rs.getInt("Viesti.ketjuid");
            
            Timestamp pvmTimestamp = rs.getTimestamp("viesti.pvm");
            LocalDateTime pvm = pvmTimestamp.toLocalDateTime();
            Viesti uusiViesti = new Viesti(id, viesti, nimimerkki, pvm, ketjuhaeId);
            viestit.add(uusiViesti);
        }
        rs.close();
        stmt.close();
        suljeYhteys();
        return viestit;
    }
    
}
