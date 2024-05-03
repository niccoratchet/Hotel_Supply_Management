package com.unifisweproject.hotelsupplymanagement.order;

import com.unifisweproject.hotelsupplymanagement.controller.order.OrderManagementController;
import com.unifisweproject.hotelsupplymanagement.itemsInOrderAndSupply.ItemsInOrderManagement;
import com.unifisweproject.hotelsupplymanagement.main.HotelSupplyManagementMain;
import com.unifisweproject.hotelsupplymanagement.model.order.OrderManagementModel;
import com.unifisweproject.hotelsupplymanagement.model.order.Order;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;
import static com.unifisweproject.hotelsupplymanagement.main.HotelSupplyManagementMain.conn;

public class OrderManagementTest {

    static OrderManagementModel orderManagement;

    @BeforeAll
    static void setUp() {

        String url = "jdbc:sqlite:src/test/DBTest.db";
        try {
            HotelSupplyManagementMain.connectToDB(url);
        }
        catch (Exception e) {
            fail("Errore durante l'apertura del DB di test: " + e.getMessage());
        }
        try {
            String insertCustomerQuery = "INSERT INTO Cliente (Codice_Cliente, Data_Inserimento, Sconto, Nome, Cognome, Codice_Fiscale, P_IVA, Ragione_Sociale, Indirizzo, Civico, CAP) " +
                    "VALUES (50, '2024-04-09', 10, 'Franco', 'Lanciano', 'RSSRRA80B01A501A', '01234567890', 'Ristorante Da Peppino', 'Via Francia Corta', '3', '50011')";
            String insertOrderQuery = "INSERT INTO Ordine (Codice_Ordine, BF, Tipo_Pagamento, Data_Ordine, Codice_Cliente) " +
                    "VALUES (99, '1', 'Bonifico Bancario', '2024-04-09', 50)";
            String insertOrderQuery2 = "INSERT INTO Ordine (Codice_Ordine, BF, Tipo_Pagamento, Data_Ordine, Codice_Cliente) " +
                    "VALUES (15, '1', 'Bonifico Bancario', '2024-04-09', 50)";
            Statement orderStatement = conn.createStatement();
            orderStatement.executeUpdate(insertOrderQuery);
            Statement orderStatement2 = conn.createStatement();
            orderStatement2.executeUpdate(insertOrderQuery2);
            Statement customerStatement = conn.createStatement();
            customerStatement.executeUpdate(insertCustomerQuery);
        }
        catch (SQLException e) {
            fail("Errore durante l'inserimento del cliente nel DB: " + e.getMessage());
        }

    }

    @Test
    void testAddOrder() {

        orderManagement = OrderManagementModel.getInstance();
        orderManagement.add(new Order(50, true, "Rimessa Diretta", "2024-04-09"));
        String query = "SELECT * FROM Ordine WHERE Codice_Cliente = 50 AND BF = '1' AND Data_Ordine = '2024-04-09' AND " +
                "Tipo_Pagamento = 'Rimessa Diretta'";
        try {
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            int resultCount = 0;
            while (resultSet.next()) {
                assertEquals(50, resultSet.getInt("Codice_Cliente"));
                assertEquals("1", resultSet.getString("BF"));
                assertEquals("2024-04-09", resultSet.getString("Data_Ordine"));
                assertEquals("Rimessa Diretta", resultSet.getString("Tipo_Pagamento"));
                resultCount++;
            }
            assertEquals(1, resultCount);
        }
        catch (SQLException e) {
            System.err.println("Errore durante la creazione dello Statement: " + e.getMessage());
        }

    }


    @Test
    void testAddItemsInOrder() {

        OrderManagementController orderController = OrderManagementController.getInstance();
        orderController.setItemsInOrderManagement(new ItemsInOrderManagement());
        orderController.getItemsInOrderManagement().setCodice_Ordine(15);                     // Queste 3 istruzioni servono per impostare i dati dell'ordine nell'oggetto ItemsInOrderManagement che contiene la lista degli articoli ordinati
        orderController.getItemsInOrderManagement().addCodice_Articolo(50);
        orderController.getItemsInOrderManagement().addQuantita(1);
        orderController.getItemsInOrderManagement().addCodice_Articolo(35);
        orderController.getItemsInOrderManagement().addQuantita(5);
        orderController.getItemsInOrderManagement().insertItemInOrderRow();                       // Metodo che effettua la query per aggiungere le righe per un singolo ordine in ArticoloInOrdine
        String query = "SELECT * FROM ArticoloInOrdine WHERE Codice_Ordine = 15 AND Codice_Articolo = 50 AND Quantita = 1";
        try {
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            int resultCount = 0;
            while (resultSet.next()) {
                assertEquals(15, resultSet.getInt("Codice_Ordine"));
                assertEquals(50, resultSet.getInt("Codice_Articolo"));
                assertEquals(1, resultSet.getInt("Quantita"));
                resultCount++;
            }
            assertEquals(1, resultCount);
        }
        catch (SQLException e) {
            System.err.println("Errore durante la creazione dello Statement: " + e.getMessage());
        }

        query = "SELECT * FROM ArticoloInOrdine WHERE Codice_Ordine = 15";
        try {
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            int resultCount = 0;
            while (resultSet.next())
                resultCount++;

            assertEquals(2, resultCount);
        }
        catch (SQLException e) {
            System.err.println("Errore durante la creazione dello Statement: " + e.getMessage());
        }

    }


    @Test
    void testDeleteOrder() {

        orderManagement = OrderManagementModel.getInstance();
        orderManagement.delete(new Order(99, 50, true, "Bonifico Bancario", "2024-04-09"));
        String query = "SELECT * FROM Ordine WHERE Codice_Ordine = 99";
        try {
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            int resultCount = 0;
            while (resultSet.next())
                resultCount++;
            assertEquals(0, resultCount);
        }
        catch (SQLException e) {
            System.err.println("Errore durante la creazione dello Statement: " + e.getMessage());
        }

    }


    @Test
    void testSearchOrder() {

        orderManagement = OrderManagementModel.getInstance();
        Order order = new Order( 50, true, "Bonifico Bancario", "2024-04-09");
        orderManagement.add(order);
        Order orderToTest = (Order) orderManagement.search(order).get(0);
        assertEquals(50, orderToTest.getCodice_cliente());
        assertTrue(orderToTest.isBolla());
        assertEquals("Bonifico Bancario", orderToTest.getTipo_pagamento());
        assertEquals("2024-04-09", orderToTest.getData_ordine());

    }


    @AfterAll
    static void clearDB() {             // Metodo per la cancellazione dei dati di test dal DB

        String deleteCustomerQuery = "DELETE FROM Cliente";
        String deleteOrderQuery = "DELETE FROM Ordine";
        String deleteItemsInOrderQuery = "DELETE FROM ArticoloInOrdine";
        String updateSqLiteSequence = "UPDATE sqlite_sequence SET seq = 0 WHERE name = 'Cliente'";
        String updateSqLiteSequence2 = "UPDATE sqlite_sequence SET seq = 0 WHERE name = 'Ordine'";

        try {
            Statement companyStatement = conn.createStatement();
            companyStatement.executeUpdate(deleteOrderQuery);
            Statement customerStatement = conn.createStatement();
            customerStatement.executeUpdate(deleteCustomerQuery);
            Statement sequenceStatement = conn.createStatement();
            sequenceStatement.executeUpdate(updateSqLiteSequence);
            Statement sequenceStatement2 = conn.createStatement();
            sequenceStatement2.executeUpdate(updateSqLiteSequence2);
            Statement itemsInOrderStatement = conn.createStatement();
            itemsInOrderStatement.executeUpdate(deleteItemsInOrderQuery);
        }
        catch (SQLException e) {
            fail("Errore durante la cancellazione dei dati di test dal DB: " + e.getMessage());
        }

    }

}
