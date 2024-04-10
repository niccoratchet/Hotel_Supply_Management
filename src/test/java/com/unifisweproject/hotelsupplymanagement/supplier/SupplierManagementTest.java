package com.unifisweproject.hotelsupplymanagement.supplier;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class SupplierManagementTest {

    static Connection conn = null;

    @BeforeAll
    static void setUp() {

        System.out.println("Setting up...");
        String url = "jdbc:sqlite:/src/test/DBTest.db";
        System.out.println("Connessione al DB effettuata con successo!");
        try {
            conn = DriverManager.getConnection(url);
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    @Test
    void addSupplier() {

        SupplierManagement supplierManagement = new SupplierManagement();
        supplierManagement.add(new Supplier(10,"2024-04-09", "00000100000",
                "Rossi Mario", "Via Roma", "00100", "1"));
        String query = "SELECT * FROM Fornitore WHERE  Codice_Fornitore = 10 AND"+
                " Data_Inserimento = '2024-04-09' AND P_IVA = '00000100000' AND"+
                "Ragione_Sociale = 'Rossi Mario' AND Indirizzo = 'Via Roma' AND CAP = '00100'"+
                "AND Numero_Civico = '1'";

        try {
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            int resultCount = 0;
            while (resultSet.next()) {
                assertEquals(10, resultSet.getInt("Codice_Fornitore"));
                assertEquals("2024-04-09", resultSet.getString("Data_Inserimento"));
                assertEquals("00000100000", resultSet.getString("P_IVA"));
                assertEquals("Rossi Mario", resultSet.getString("Ragione_Sociale"));
                assertEquals("Via Roma", resultSet.getString("Indirizzo"));
                assertEquals("00100", resultSet.getString("CAP"));
                assertEquals("1", resultSet.getString("Numero_Civico"));
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

        SupplierManagement supplierManagement = new SupplierManagement();
        supplierManagement.modify(new Supplier(10, "2024-04-10",
                "00000200000", "Rossi Mauro", "Via Firenze",
                "00200", "2"));
        String query = "SELECT * FROM Fornitore WHERE  Codice_Fornitore = 10";
        try {
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            int resultCount = 0;
            while (resultSet.next()) {
                assertEquals(10, resultSet.getInt("Codice_Fornitore"));
                assertEquals("2024-04-10", resultSet.getString("Data_Inserimento"));
                assertEquals("00000200000", resultSet.getString("P_IVA"));
                assertEquals("Rossi Mauro", resultSet.getString("Ragione_Sociale"));
                assertEquals("Via Firenze", resultSet.getString("Indirizzo"));
                assertEquals("00200", resultSet.getString("CAP"));
                assertEquals("2", resultSet.getString("Numero_Civico"));
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

        SupplierManagement supplierManagement = new SupplierManagement();
        
    }

    @Test
    void deleteSupplier() {

        SupplierManagement supplierManagement = new SupplierManagement();
        supplierManagement.delete(10);
        String query = "SELECT * FROM Fornitore WHERE  Codice_Fornitore = 10";

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

}
