package com.unifisweproject.hotelsupplymanagement.supplier;

import com.unifisweproject.hotelsupplymanagement.main.HotelSupplyManagementMain;
import com.unifisweproject.hotelsupplymanagement.model.supplier.Supplier;
import com.unifisweproject.hotelsupplymanagement.model.supplier.SupplierManagementModel;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.*;
import java.util.ArrayList;

import static com.unifisweproject.hotelsupplymanagement.main.HotelSupplyManagementMain.conn;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class SupplierManagementTest {

    @BeforeAll
    static void setUp() {

        String url = "jdbc:sqlite:src/test/DBTest.db";
        try {
            HotelSupplyManagementMain.connectToDB(url);
        }
        catch (SQLException e) {
            fail("Errore durante l'apertura del DB di test: " + e.getMessage());
        }
        try {
            String insertSupplierQuery = "INSERT INTO Fornitore (Codice_Fornitore, Data_Inserimento, P_IVA, Ragione_Sociale, Indirizzo, Civico, CAP) " +
                    "VALUES (99, '2024-04-09', '01234567890', 'Ristorante Il Ritrovo', 'Via Parigi', '3', '50085')";
            Statement supplierStatement1 = conn.createStatement();
            supplierStatement1.executeUpdate(insertSupplierQuery);
            insertSupplierQuery = "INSERT INTO Fornitore (Codice_Fornitore, Data_Inserimento, P_IVA, Ragione_Sociale, Indirizzo, Civico, CAP) " +
                    "VALUES (98, '2024-04-09', '12345678910', 'Bottega Veneta', 'Viale Caselli', '98', '50987')";
            Statement supplierStatement2 = conn.createStatement();
            supplierStatement2.executeUpdate(insertSupplierQuery);
            insertSupplierQuery = "INSERT INTO Fornitore (Codice_Fornitore, Data_Inserimento, P_IVA, Ragione_Sociale, Indirizzo, Civico, CAP) " +
                    "VALUES (97, '2024-04-09', '00000000000', 'Hotel Bogazzi', 'Piazza Rufina', '1', '50000')";
            Statement supplierStatement3 = conn.createStatement();
            supplierStatement3.executeUpdate(insertSupplierQuery);
        }
        catch (SQLException e) {
            fail("Errore durante l'inserimento del fornitore nel DB all'interno della fase di set-up: " + e.getMessage());
        }

    }

    @Test
    void addSupplier() {

        SupplierManagementModel supplierManagement = SupplierManagementModel.getInstance();
        supplierManagement.add(new Supplier("2024-04-09", "01234567890",
                "Rossi Mario", "Via Roma", "00100", "1"));
        String query = "SELECT * FROM Fornitore WHERE"+
                " Data_Inserimento = '2024-04-09' AND P_IVA = '01234567890' AND"+
                " Ragione_Sociale = 'Rossi Mario' AND Indirizzo = 'Via Roma' AND CAP = '00100'"+
                " AND Civico = '1'";
        try {
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            int resultCount = 0;
            while (resultSet.next()) {
                assertEquals("2024-04-09", resultSet.getString("Data_Inserimento"));
                assertEquals("01234567890", resultSet.getString("P_IVA"));
                assertEquals("Rossi Mario", resultSet.getString("Ragione_Sociale"));
                assertEquals("Via Roma", resultSet.getString("Indirizzo"));
                assertEquals("00100", resultSet.getString("CAP"));
                assertEquals("1", resultSet.getString("Civico"));
                resultCount++;
            }
            assertEquals(1, resultCount);
        }
        catch (SQLException e) {
            fail("Errore durante la creazione dello Statement: " + e.getMessage());
        }

    }

    @Test
    void modifySupplier() {

        SupplierManagementModel supplierManagement = SupplierManagementModel.getInstance();
        supplierManagement.modify(new Supplier(99, "2024-04-10", "00000200000",
                "Rossi Mauro", "Via Firenze", "00200", "2"));
        String query = "SELECT * FROM Fornitore WHERE  Codice_Fornitore = 99";
        try {
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            int resultCount = 0;
            while (resultSet.next()) {
                assertEquals(99, resultSet.getInt("Codice_Fornitore"));
                assertEquals("2024-04-10", resultSet.getString("Data_Inserimento"));
                assertEquals("00000200000", resultSet.getString("P_IVA"));
                assertEquals("Rossi Mauro", resultSet.getString("Ragione_Sociale"));
                assertEquals("Via Firenze", resultSet.getString("Indirizzo"));
                assertEquals("00200", resultSet.getString("CAP"));
                assertEquals("2", resultSet.getString("Civico"));
                resultCount++;
            }
            assertEquals(1, resultCount);
        }
        catch (SQLException e) {
            fail("Errore durante la creazione dello Statement: " + e.getMessage());
        }
    }

    @Test
    void searchSupplier() {

        SupplierManagementModel supplierManagement = SupplierManagementModel.getInstance();
        Supplier toBeSearch = new Supplier("2024-04-09",
                "12345678910", "Bottega Veneta", "Viale Caselli", "50987", "98");
        supplierManagement.add(toBeSearch);
        ArrayList<Object> list = supplierManagement.search(toBeSearch);
        Supplier test = (Supplier) list.get(0);
        assertEquals("2024-04-09", test.getData_inserimento());
        assertEquals("12345678910", test.getP_IVA());
        assertEquals("Bottega Veneta", test.getRagione_sociale());
        assertEquals("Viale Caselli", test.getIndirizzo());
        assertEquals("50987", test.getCAP());
        assertEquals("98", test.getCivico());

    }

    @Test
    void deleteSupplier() {

        SupplierManagementModel supplierManagement = SupplierManagementModel.getInstance();
        supplierManagement.delete(new Supplier(97, "2024-04-09", "00000000000",
                "Hotel Bogazzi", "Piazza Rufina", "50000", "1"));
        String query = "SELECT * FROM Fornitore WHERE  Codice_Fornitore = 97";
        try {
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            int resultCount = 0;
            while (resultSet.next())
                resultCount++;
            assertEquals(0, resultCount);
        }
        catch (SQLException e) {
            fail("Errore: " + e.getMessage());
        }

    }

    @AfterAll
    static void clearDB() {             // Metodo per la cancellazione dei dati di test dal DB

        String deleteSupplierQuery = "DELETE FROM Fornitore";
        String updateSqLiteSequence = "UPDATE sqlite_sequence SET seq = 0 WHERE name = 'Fornitore'";
        try {
            Statement contactStatement = conn.createStatement();
            contactStatement.executeUpdate(deleteSupplierQuery);
            Statement sequenceStatement = conn.createStatement();
            sequenceStatement.executeUpdate(updateSqLiteSequence);
        }
        catch (SQLException e) {
            fail("Errore durante la cancellazione dei dati di test dal DB: " + e.getMessage());
        }

    }
}
