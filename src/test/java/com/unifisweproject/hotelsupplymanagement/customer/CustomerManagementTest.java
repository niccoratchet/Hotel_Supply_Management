package com.unifisweproject.hotelsupplymanagement.customer;

import com.unifisweproject.hotelsupplymanagement.main.HotelSupplyManagementMain;
import com.unifisweproject.hotelsupplymanagement.model.customer.Customer;
import com.unifisweproject.hotelsupplymanagement.model.customer.CustomerManagementModel;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.sql.*;
import java.util.ArrayList;

import static com.unifisweproject.hotelsupplymanagement.main.HotelSupplyManagementMain.conn;
import static org.junit.jupiter.api.Assertions.*;

class CustomerManagementTest {

    Customer customerTest = new Customer(99, 10, "Giuseppe", "Verdi", "2024-04-09", "RSSMRA80A01H501A",
            "01234567890", "Ristorante Il Ritrovo", "Via Parigi", "50085", "3");
    String modifyString = "Via Londra";

    @BeforeAll
    static void setUp() {               // Metodo per la connessione al DB e per l'inserimento di un elemento di test

        String url = "jdbc:sqlite:src/test/DBTest.db";
        try {
            HotelSupplyManagementMain.connectToDB(url);
        }
        catch (Exception e) {
            fail("Errore durante l'apertura del DB di test: " + e.getMessage());
        }
        try {
            String insertContactDetailsQuery = "INSERT INTO Recapito (Indirizzo, CAP, Civico, Localita, Provincia, Telefono, Mail) " +
                    "VALUES ('Via Roma', '00100', '1', 'Roma', 'Roma', '01234567890', 'mariorossi@gmail.com')";
            String insertCompanyDetailsQuery = "INSERT INTO Dati_Azienda (Ragione_Sociale, P_IVA) " +
                    "VALUES ('Rossi Mario', '01234567890')";
            String insertCustomerQuery = "INSERT INTO Cliente (Codice_Cliente, Data_Inserimento, Sconto, Nome, Cognome, Codice_Fiscale, P_IVA, Ragione_Sociale, Indirizzo, Civico, CAP) " +
                    "VALUES (99, '2024-04-09', 10, 'Giuseppe', 'Verdi', 'RSSMRA80A01H501A', '01234567890', 'Ristorante Il Ritrovo', 'Via Parigi', '3', '50085')";
            Statement contactStatement = conn.createStatement();
            contactStatement.executeUpdate(insertContactDetailsQuery);
            Statement companyStatement = conn.createStatement();
            companyStatement.executeUpdate(insertCompanyDetailsQuery);
            Statement customerStatement = conn.createStatement();
            customerStatement.executeUpdate(insertCustomerQuery);
        }
        catch (SQLException e) {
            fail("Errore durante l'inserimento del cliente nel DB: " + e.getMessage());
        }

    }

    @Test
    void testAddCustomer() {                // Test per il metodo addCustomer in CustomManagement: effettuo l'aggiunta di un cliente e successivamente verifico che i dati siano stati inseriti correttamente

        CustomerManagementModel customerManagement = CustomerManagementModel.getInstance();
        customerManagement.add(new Customer(10, "Mario", "Rossi", "2024-04-09", "RSSMRA80A01H501A",
                "01234567890", "Rossi Mario", "Via Roma", "00100", "1"));
        String query = "SELECT * FROM Cliente WHERE Nome = 'Mario' AND Cognome = 'Rossi' AND Data_Inserimento = '2024-04-09' AND " +
                "Codice_Fiscale = 'RSSMRA80A01H501A' AND Ragione_Sociale = 'Rossi Mario' AND P_IVA = '01234567890' AND " +
                "Indirizzo = 'Via Roma' AND CAP = '00100' AND Civico = '1'";
        try {
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            int resultCount = 0;
            while (resultSet.next()) {
                assertEquals("Mario", resultSet.getString("Nome"));
                assertEquals("Rossi", resultSet.getString("Cognome"));
                assertEquals("2024-04-09", resultSet.getString("Data_Inserimento"));
                assertEquals("RSSMRA80A01H501A", resultSet.getString("Codice_Fiscale"));
                assertEquals("Rossi Mario", resultSet.getString("Ragione_Sociale"));
                assertEquals("01234567890", resultSet.getString("P_IVA"));
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
    void testModifyCustomer() {                 // Test per il metodo modifyCustomer in CustomerManagementModel: il test si basa sulla modifica dell'indirizzo di un cliente e sulla successiva verifica della modifica effettuando una query al DB

        customerTest.setIndirizzo(modifyString);
        CustomerManagementModel customerManagement = CustomerManagementModel.getInstance();
        customerManagement.modify(customerTest);
        String verificationQuery = "SELECT * FROM Cliente WHERE Codice_Cliente = 99";
        try {
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(verificationQuery);
            while (resultSet.next()) {
                assertEquals(modifyString, resultSet.getString("Indirizzo"));
            }
        }
        catch (SQLException e) {
            fail("Errore durante la creazione dello Statement: " + e.getMessage());
        }

    }

    @Test
    void testSearchCustomer() {

        Customer toBeSearched = new Customer(-1, -1, "Giuseppe", "Verdi", null, null, null, null, null, null, null);
        CustomerManagementModel customerManagement = CustomerManagementModel.getInstance();
        customerManagement.add(customerTest);
        ArrayList<Object> searchResults = customerManagement.search(toBeSearched);
        assertEquals(1, searchResults.size());

    }

    @Test
    void testDeleteCustomer() {                 // Test per il metodo deleteCustomer in CustomerManagementModel: il test si basa sulla cancellazione di un cliente e sulla successiva verifica della cancellazione effettuando una query al DB

        CustomerManagementModel customerManagement = CustomerManagementModel.getInstance();
        String insertCustomerQuery = "INSERT INTO Cliente (Codice_Cliente, Data_Inserimento, Sconto, Nome, Cognome, Codice_Fiscale, P_IVA, Ragione_Sociale, Indirizzo, Civico, CAP) " +
                "VALUES (102, '2024-04-09', 10, 'Giuseppe', 'Verdi', 'RSSMRA80A01H501A', '01234567890', 'Ristorante Il Ritrovo', 'Via Parigi', '3', '50085')";
        try {
            Statement statement = conn.createStatement();
            statement.executeUpdate(insertCustomerQuery);
        }
        catch (SQLException e) {
            fail("Errore durante la creazione dello Statement: " + e.getMessage());
            return;
        }
        customerManagement.delete(new Customer(100, -1, null, null, null, null, null, null, null, null, null));
        String searchQuery = "SELECT * FROM Cliente WHERE Codice_Cliente = 100";
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
            fail("Errore durante la creazione dello Statement: " + e.getMessage());
        }

    }

    @AfterAll
    static void clearDB() {             // Metodo per la cancellazione dei dati di test dal DB

        String deleteContactDetailsQuery = "DELETE FROM Recapito";
        String deleteCompanyDetailsQuery = "DELETE FROM Dati_Azienda";
        String deleteCustomerQuery = "DELETE FROM Cliente";
        String updateSqLiteSequence = "UPDATE sqlite_sequence SET seq = 0 WHERE name = 'Cliente'";
        try {
            Statement contactStatement = conn.createStatement();
            contactStatement.executeUpdate(deleteContactDetailsQuery);
            Statement companyStatement = conn.createStatement();
            companyStatement.executeUpdate(deleteCompanyDetailsQuery);
            Statement customerStatement = conn.createStatement();
            customerStatement.executeUpdate(deleteCustomerQuery);
            Statement sequenceStatement = conn.createStatement();
            sequenceStatement.executeUpdate(updateSqLiteSequence);
        }
        catch (SQLException e) {
            fail("Errore durante la cancellazione dei dati di test dal DB: " + e.getMessage());
        }

    }

}