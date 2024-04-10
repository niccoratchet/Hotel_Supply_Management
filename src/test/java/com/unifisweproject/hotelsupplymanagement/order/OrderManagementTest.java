package com.unifisweproject.hotelsupplymanagement.order;

import com.unifisweproject.hotelsupplymanagement.customer.Customer;
import com.unifisweproject.hotelsupplymanagement.customer.CustomerManagement;
import com.unifisweproject.hotelsupplymanagement.item.Item;
import com.unifisweproject.hotelsupplymanagement.itemsInOderAndSupply.ItemInOrder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class OrderManagementTest {

    static Connection conn = null;

    @BeforeAll
    static void setUp() {

        String url = "jdbc:sqlite:src/test/DBTest.db";
        System.out.println("Connessione al DB effettuata con successo!");

        try {
            conn = DriverManager.getConnection(url);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            fail("Errore durante l'apertura del DB di test: " + e.getMessage());
        }

        /*try {
            String insertContactDetailsQuery = "INSERT INTO Recapito (Indirizzo, CAP, Civico, Localita, Provincia, Telefono, Mail) " +
                    "VALUES ('Via Roma', '00100', '1', 'Roma', 'Roma', '01234567890', 'mariorossi@gmail.com')";
            String insertCompanyDetailsQuery = "INSERT INTO Dati_Azienda (Ragione_Sociale, P_IVA) " +
                    "VALUES ('Rossi Mario', '01234567890')";
            String insertCustomerQuery = "INSERT INTO Cliente (Codice_Cliente, Data_Inserimento, Sconto, Nome, Cognome, Codice_Fiscale, P_IVA, Ragione_Sociale, Indirizzo, Civico, CAP) " +
                    "VALUES (99, '2024-04-09', 10, 'Mario', 'Rossi', 'RSSMRA80A01H501A', '01234567890', 'Rossi Mario', 'Via Roma', '1', '00100')";
            Statement contactStatement = conn.createStatement();
            contactStatement.executeUpdate(insertContactDetailsQuery);
            Statement companyStatement = conn.createStatement();
            companyStatement.executeUpdate(insertCompanyDetailsQuery);
            Statement customerStatement = conn.createStatement();
            customerStatement.executeUpdate(insertCustomerQuery);
        }
        catch (SQLException e) {
            fail("Errore durante l'inserimento del cliente nel DB: " + e.getMessage());
        }*/

    }

    @Test
    void testAddOrder() {

        OrderManagement orderManagement = new OrderManagement();
        orderManagement.add(new Order(1, true, "Bonifico Bancario", "2024-04-09"));
        String query = "SELECT * FROM Ordine WHERE Codice_Cliente = 1 AND BF = '1' AND Data_Inserimento = '2024-04-09' AND " +
                "Tipo_Pagamento = 'Bonifico Bancario'";

        try {
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            int resultCount = 0;

            while (resultSet.next()) {
                assertEquals(1, resultSet.getInt("Codice_Cliente"));
                assertEquals("1", resultSet.getString("BF"));
                assertEquals("2024-04-09", resultSet.getString("Data_Inserimento"));
                assertEquals("Bonifico Bancario", resultSet.getString("Tipo_Pagamento"));
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
        OrderAddWindowController orderAddWindowController = new OrderAddWindowController();

        ItemInOrder itemInOrder = new ItemInOrder();
        itemInOrder.setCodice_Ordine(1);
        itemInOrder.addCodice_Articolo(1);
        itemInOrder.addQuantita(1);

        orderAddWindowController.updateItemInOrder();

        String query = "SELECT * FROM ArticoloInOrdine WHERE Codice_Ordine = 1 AND Codice_Articolo = 1 AND Quantita = 1";

        try {
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            int resultCount = 0;

            while (resultSet.next()) {
                assertEquals(1, resultSet.getInt("Codice_Ordine"));
                assertEquals(1, resultSet.getInt("Codice_Articolo"));
                assertEquals(1, resultSet.getInt("Quantita"));
                resultCount++;
            }
            assertEquals(1, resultCount);
        }
        catch (SQLException e) {
            System.err.println("Errore durante la creazione dello Statement: " + e.getMessage());
        }

    }

    void testDeleteOrder() {
        OrderManagement orderManagement = new OrderManagement();

        orderManagement.delete(1);

        String query = "SELECT * FROM Ordine WHERE Codice_Ordine = 1";

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

    void testSearchOrder() {
        OrderManagement orderManagement = new OrderManagement();
        Order order = new Order(2, 1, true, "Bonifico Bancario", "2024-04-09");

        ArrayList<Object> orderList = orderManagement.search(order);

        Order orderToTest = (Order) orderList.get(0);
        assertEquals(2, orderToTest.getCodice_ordine());
        assertEquals(1, orderToTest.getCodice_cliente());
        assertTrue(orderToTest.isBolla());
        assertEquals("Bonifico Bancario", orderToTest.getTipo_pagamento());
        assertEquals("2024-04-09", orderToTest.getData_ordine());
    }
}
