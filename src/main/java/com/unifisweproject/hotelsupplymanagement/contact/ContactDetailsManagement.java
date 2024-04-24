package com.unifisweproject.hotelsupplymanagement.contact;
import com.unifisweproject.hotelsupplymanagement.main.HotelSupplyManagementMain;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ContactDetailsManagement {

    public static void executeAddQuery(String address, String CAP, String civicNumber, String location, String province, String phone, String mail) {            // Una volta validati tutti i dati del Cliente e di Dati Azienda, viene effettuata la query di aggiunta al database nella tabella Recapito

        String insertContactQuery = "INSERT INTO Recapito (Indirizzo, CAP, Civico, Localita, Provincia, Telefono, Mail) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try {
            PreparedStatement preparedStatement = HotelSupplyManagementMain.conn.prepareStatement(insertContactQuery);
            setValuesInQuery(preparedStatement, address, CAP, civicNumber, location, province, phone, mail);
            try {
                preparedStatement.executeUpdate();
            }
            catch (SQLException e) {
                System.out.println("Elemento già presente nel DB");
            }
        }
        catch (SQLException e) {
            System.err.println("Errore durante la generazione della query di inserimento in Recapito: " + e.getMessage());
        }

    }

    public static ResultSet setInitialFields(String address, String civicNumber, String cap) throws SQLException {

        String searchContactQuery = "SELECT * FROM Recapito WHERE Indirizzo = ? AND CAP = ? AND Civico = ?";
        PreparedStatement statement = HotelSupplyManagementMain.conn.prepareStatement(searchContactQuery);
        statement.setString(1, address);
        statement.setString(2, cap);
        statement.setString(3, civicNumber);
        return statement.executeQuery();

    }

    public static void executeUpdateQuery(String address, String civicNumber, String CAP, String location, String province, String phone, String mail,
                                          String originalAddress, String originalCivicNumber, String originalCAP) {                    // Una volta validati tutti i dati del Cliente e di Dati Azienda, viene effettuata la query di aggiunta al database

        String updateContactQuery = "UPDATE Recapito SET Indirizzo = ?, CAP = ?, Civico = ?, Localita = ?, Provincia = ?," +
                " Telefono = ?, Mail = ? WHERE Indirizzo = ? AND CAP = ? AND Civico = ?";
        try {
            PreparedStatement preparedStatement = HotelSupplyManagementMain.conn.prepareStatement(updateContactQuery);
            setValuesInQuery(preparedStatement, address, civicNumber, CAP, location, province, phone, mail);
            preparedStatement.setString(8, originalAddress);
            preparedStatement.setString(9, originalCAP);
            preparedStatement.setString(10, originalCivicNumber);
            try {
                preparedStatement.executeUpdate();
            }
            catch (SQLException e) {
                System.out.println("Elemento già presente nel DB");
            }
        }
        catch (SQLException e) {
            System.err.println("Errore durante la generazione della query di modifica in Recapito: " + e.getMessage());
        }

    }

    private static void setValuesInQuery(PreparedStatement preparedStatement, String address, String civicNumber, String CAP,
                                         String location, String province, String phone, String mail) throws SQLException {

        preparedStatement.setString(1, address);
        preparedStatement.setString(2, CAP);
        preparedStatement.setString(3, civicNumber);
        preparedStatement.setString(4, location);
        preparedStatement.setString(5, province);
        preparedStatement.setString(6, phone);
        preparedStatement.setString(7, mail);

    }

}
