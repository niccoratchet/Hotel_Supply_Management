package com.unifisweproject.hotelsupplymanagement.supply;

import com.unifisweproject.hotelsupplymanagement.main.HotelSupplyManagementMain;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.sql.*;

import static com.unifisweproject.hotelsupplymanagement.main.HotelSupplyManagementMain.conn;
import static org.junit.jupiter.api.Assertions.*;

public class SupplyManagementTest {

    static SupplyDataManagementModel supplyManagement;
    static String insertSupplyTestQuery = "INSERT INTO Fornitura (Codice_Fornitura, Codice_Articolo, Codice_Fornitore, Data_Fornitura, Prezzo, Quantita) " +
            "VALUES (99, 99, 99, '2024-04-09', 1.50, 100)";
    static Supply supplyTest = new Supply(99, 99, 99, "2024-04-09", 100, 1.50);

    @BeforeAll
    static void setUp() {           // Codice di set-up per la connessione al DB e l'inserimento di una fornitura di test

        String url = "jdbc:sqlite:src/test/DBTest.db";
        try {
            HotelSupplyManagementMain.connectToDB(url);
            supplyManagement = SupplyDataManagementModel.getInstance();
            supplyManagement.getSupplyList().add(supplyTest);
        }
        catch (Exception e) {
            fail("Errore durante l'apertura del DB di test: " + e.getMessage());
        }
        try {
            Statement supplyStatement = conn.createStatement();
            supplyStatement.executeUpdate(insertSupplyTestQuery);
        }
        catch (SQLException e) {
            fail("Errore durante l'inserimento del fornitore nel DB in fase di set-up: " + e.getMessage());
        }

    }

    @Test
    void testAddSupply() {              // Test per il metodo add in SupplyDataManagementModel: il test si basa sull'aggiunta di una fornitura nel DB

        supplyManagement.add(new Supply(99,99, "2024-04-09", 63, 10.00));
        String query = "SELECT * FROM Fornitura WHERE Codice_Articolo = 99 AND Codice_Fornitore = 99 AND Data_Fornitura = '2024-04-09' AND " +
                "Prezzo = 10.00 AND Quantita = 63";
        try {
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            int resultCount = 0;
            while (resultSet.next()) {
                assertEquals(99, resultSet.getInt("Codice_Articolo"));
                assertEquals(99, resultSet.getInt("Codice_Fornitore"));
                assertEquals("2024-04-09", resultSet.getString("Data_Fornitura"));
                assertEquals(10.00, resultSet.getDouble("Prezzo"));
                assertEquals(63, resultSet.getInt("Quantita"));
                resultCount++;
            }
            assertEquals(1, resultCount);
        }
        catch (SQLException e) {
            fail("Errore durante il test di aggiunta di una fornitura: " + e.getMessage());
        }

    }

    @Test
    void testDeleteSupply() {               // Test per il metodo delete in SupplyDataManagementModel: il test si basa sulla cancellazione di una fornitura dal DB

        String insertSupply = "INSERT INTO Fornitura (Codice_Fornitura, Codice_Articolo, Codice_Fornitore, Data_Fornitura, Prezzo, Quantita) " +
                "VALUES (102, 99, 99, '2024-04-09', 1.50, 100)";
        try {
            Statement supplyStatement = conn.createStatement();
            supplyStatement.executeUpdate(insertSupply);
        }
        catch (SQLException e) {
            fail("Errore durante l'inserimento della fornitura nel DB in fase di set-up: " + e.getMessage());
        }
        supplyManagement.delete(new Supply(102, 99, 99, "2024-04-09", 100, 1.50));
        String searchQuery = "SELECT * FROM Fornitura WHERE Codice_Fornitura = 101";
        try {
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(searchQuery);
            int resultCount = 0;
            while (resultSet.next()) {
                resultCount++;
            }
            assertEquals(0, resultCount);
        }
        catch (SQLException e) {
            fail("Errore durante il test di cancellazione di una fornitura: " + e.getMessage());
        }

    }

    @Test
    void testSearchSupply() {               // Test per il metodo search in SupplyDataManagementModel: il test si basa sulla ricerca di una fornitura nel DB

        Supply toBeSearched = new Supply(-1, 99, 99, null, -1, -1);
        assertEquals(1, supplyManagement.search(toBeSearched).size());

    }

    @AfterAll
    static void clearDB() {                 // Metodo per la pulizia del DB di test

        String deleteSupplier = "DELETE FROM Fornitore";
        String deleteItem = "DELETE FROM Articolo";
        String deleteSupply = "DELETE FROM Fornitura";
        String updateSQLLiteSequenceSupplier = "UPDATE sqlite_sequence SET seq = 0 WHERE name = 'Fornitore'";
        String updateSQLLiteSequenceItem = "UPDATE sqlite_sequence SET seq = 0 WHERE name = 'Articolo'";
        String updateSQLLiteSequenceSupply = "UPDATE sqlite_sequence SET seq = 0 WHERE name = 'Fornitura'";
        try {
            Statement supplierStatement = conn.createStatement();
            supplierStatement.executeUpdate(deleteSupplier);
            Statement itemStatement = conn.createStatement();
            itemStatement.executeUpdate(deleteItem);
            Statement supplyStatement = conn.createStatement();
            supplyStatement.executeUpdate(deleteSupply);
            Statement updateSupplierSequence = conn.createStatement();
            updateSupplierSequence.executeUpdate(updateSQLLiteSequenceSupplier);
            Statement updateItemSequence = conn.createStatement();
            updateItemSequence.executeUpdate(updateSQLLiteSequenceItem);
            Statement updateSupplySequence = conn.createStatement();
            updateSupplySequence.executeUpdate(updateSQLLiteSequenceSupply);
        }
        catch (SQLException e) {
            fail("Errore durante la pulizia del DB di test: " + e.getMessage());
        }

    }


}
