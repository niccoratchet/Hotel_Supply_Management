package com.unifisweproject.hotelsupplymanagement.item;

import com.unifisweproject.hotelsupplymanagement.main.HotelSupplyManagementMain;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.sql.*;
import java.util.ArrayList;

import static com.unifisweproject.hotelsupplymanagement.main.HotelSupplyManagementMain.conn;
import static org.junit.jupiter.api.Assertions.*;

public class ItemManagementTest {

    static String insertItemQuery1 = "INSERT INTO Articolo (Codice_Articolo, Nome, Prezzo, Quantita, Descrizione, Data_Inserimento)"
            + " VALUES (99, 'Piatto', 1.50, 100, 'Piatto piano in ceramica', '2024-04-09')";
    static Item testItem1 = new Item(99, 100, 1.50, "Piatto", "Piatto piano in ceramica", "2024-04-09");
    static String insertItemQuery2 = "INSERT INTO Articolo (Codice_Articolo, Nome, Prezzo, Quantita, Descrizione, Data_Inserimento)"
            + " VALUES (98, 'Bicchiere', 2.00, 50, 'Bicchiere in vetro', '2024-04-09')";
    static Item testItem2 = new Item(98, 50, 2.00, "Bicchiere", "Bicchiere in vetro", "2024-04-09");
    static ItemManagement itemManagement;

    @BeforeAll
    static void setUp() {                   // Codice di set-up per la connessione al DB e l'inserimento di un articolo di test

        String url = "jdbc:sqlite:src/test/DBTest.db";
        try {
            HotelSupplyManagementMain.connectToDB(url);
            itemManagement = ItemManagement.getInstance();
            itemManagement.getItemList().add(testItem1);
            itemManagement.getItemList().add(testItem2);
        }
        catch (Exception e) {
            fail("Errore durante l'apertura del DB di test: " + e.getMessage());
        }
        try {
            Statement itemStatement1 = conn.createStatement();
            itemStatement1.executeUpdate(insertItemQuery1);
            Statement itemStatement2 = conn.createStatement();
            itemStatement2.executeUpdate(insertItemQuery2);
        }
        catch (SQLException e) {
            fail("Errore durante l'inserimento dell'articolo nel DB in fase di set-up: " + e.getMessage());
        }

    }

    @Test
    void testAddItem() {                // Test per il metodo addItem in ItemManagement: effettuo l'aggiunta di un articolo e successivamente verifico che i dati siano stati inseriti correttamente

        Item newItem = new Item(200, 0.50, "Bicchiere", "Bicchiere in vetro", "2024-04-09");
        itemManagement.add(newItem);
        itemManagement.getItemList().add(newItem);
        String query = "SELECT * FROM Articolo WHERE Nome = 'Bicchiere' AND Prezzo = 0.50 AND Quantita = 200 AND Descrizione = 'Bicchiere in vetro' AND Data_Inserimento = '2024-04-09'";
        try {
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            int resultCount = 0;
            while (resultSet.next()) {
                assertEquals(200, resultSet.getInt("Quantita"));
                assertEquals(0.50, resultSet.getDouble("Prezzo"));
                assertEquals("Bicchiere", resultSet.getString("Nome"));
                assertEquals("Bicchiere in vetro", resultSet.getString("Descrizione"));
                assertEquals("2024-04-09", resultSet.getString("Data_Inserimento"));
                resultCount++;
            }
            assertEquals(1, resultCount);
        }
        catch (SQLException e) {
            fail("Errore durante il recupero dell'articolo dal DB: " + e.getMessage());
        }

    }

    @Test
    void testModifyItem() {         // Test per il metodo modifyItem in ItemManagement: effettuo la modifica di un articolo e successivamente verifico che i dati siano stati modificati correttamente

        testItem1.setNome("Piatto fondo");
        testItem1.setPrezzo(2.50);
        testItem1.setQuantita(50);
        testItem1.setDescrizione("Piatto fondo in ceramica");
        itemManagement.modify(testItem1);
        String query = "SELECT * FROM Articolo WHERE Codice_Articolo = 99";
        try {
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                assertEquals(50, resultSet.getInt("Quantita"));
                assertEquals(2.50, resultSet.getDouble("Prezzo"));
                assertEquals("Piatto fondo", resultSet.getString("Nome"));
                assertEquals("Piatto fondo in ceramica", resultSet.getString("Descrizione"));
                assertEquals("2024-04-09", resultSet.getString("Data_Inserimento"));
            }
        }
        catch (SQLException e) {
            fail("Errore durante il recupero dell'articolo dal DB: " + e.getMessage());
        }

    }

    @Test
    void testSearchItem() {             // Test per il metodo search in ItemManagement: effettuo una ricerca di un articolo e verifico che il risultato sia corretto

        Item toBeSearched = new Item(-1, -1, -1, "Bicchiere", null, "2024-04-09");
        ArrayList<Object> searchResults = itemManagement.search(toBeSearched);
        assertEquals(1, searchResults.size());

    }

    @Test
    void testDeleteItem() {

        String insertItemToDeleteQuery = "INSERT INTO Articolo (Codice_Articolo, Nome, Prezzo, Quantita, Descrizione, Data_Inserimento)"
                + " VALUES (97, 'Tovagliolo', 0.10, 200, 'Tovagliolo di carta', '2024-04-09')";
        try {
            Statement itemStatement = conn.createStatement();
            itemStatement.executeUpdate(insertItemToDeleteQuery);
        }
        catch (SQLException e) {
            fail("Errore durante l'inserimento dell'articolo da cancellare nel DB: " + e.getMessage());
        }
        itemManagement.delete(97);
        String query = "SELECT * FROM Articolo WHERE Codice_Articolo = 97";
        try {
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            int resultCount = 0;
            while (resultSet.next()) {
                resultCount++;
            }
            assertEquals(0, resultCount);
        }
        catch (SQLException e) {
            fail("Errore durante il recupero dell'articolo dal DB: " + e.getMessage());
        }


    }

    @AfterAll
    static void clearDB() {             // Metodo per la pulizia del DB dopo aver effettuato i test

        String deleteItemQuery = "DELETE FROM Articolo";
        String updateSqLiteSequence = "UPDATE sqlite_sequence SET seq = 0 WHERE name = 'Articolo'";
        try {
            Statement itemStatement = conn.createStatement();
            itemStatement.executeUpdate(deleteItemQuery);
            Statement updateStatement = conn.createStatement();
            updateStatement.executeUpdate(updateSqLiteSequence);
        }
        catch (SQLException e) {
            fail("Errore durante la cancellazione dell'articolo nel DB in fase di pulizia: " + e.getMessage());
        }

    }


}
