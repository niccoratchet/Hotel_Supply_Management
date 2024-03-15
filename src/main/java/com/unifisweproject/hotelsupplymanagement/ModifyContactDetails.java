package com.unifisweproject.hotelsupplymanagement;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

public class ModifyContactDetails implements Initializable {

    @FXML
    private TextField addressField;
    @FXML
    private TextField civicNumberField;
    @FXML
    private TextField CAPField;
    @FXML
    private TextField locationField;
    @FXML
    private TextField provinceField;
    @FXML
    private TextField phoneNumberField;
    @FXML
    private TextField mailField;

    private SupplierViewController supplierViewController;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        Platform.runLater(() -> {

            int capCharacters = 5, maxCivicCharacters = 5, maxPhoneNumberCharacter = 10;

            UnaryOperator<TextFormatter.Change> filterCivicNumber = change -> {
                String newText = change.getControlNewText().toUpperCase();
                change.setText(newText);
                change.setRange(0, change.getControlText().length());
                String text = change.getText();
                if (text.matches("[a-zA-Z0-9]*") && change.getControlNewText().length() <= maxCivicCharacters)
                    return change;
                return null;
            };
            TextFormatter<String> civicNumberFormatter = new TextFormatter<>(filterCivicNumber);
            civicNumberField.setTextFormatter(civicNumberFormatter);

            UnaryOperator<TextFormatter.Change> filterCAP = change -> {             // Creazione del Formatter per inserimento della partita IVA (contiene solo numeri e non può essere più lungo di 11 caratteri)
                String text = change.getText();
                if (text.matches("[0-9]*") && change.getControlNewText().length() <= capCharacters) {
                    return change;
                }
                return null;
            };
            TextFormatter<String> CAPFormatter = new TextFormatter<>(filterCAP);
            CAPField.setTextFormatter(CAPFormatter);

            UnaryOperator<TextFormatter.Change> filterPhoneNumber = change -> {             // Creazione del Formatter per inserimento della partita IVA (contiene solo numeri e non può essere più lungo di 11 caratteri)
                String text = change.getText();
                if (text.matches("[0-9]*") && change.getControlNewText().length() <= maxPhoneNumberCharacter) {
                    return change;
                }
                return null;
            };
            TextFormatter<String> phoneNumberFormatter = new TextFormatter<>(filterPhoneNumber);
            phoneNumberField.setTextFormatter(phoneNumberFormatter);

            System.out.println(supplierViewController.getDisplayedSupplier().getCAP() + " " + supplierViewController.getDisplayedSupplier().getCivico());

            setInitialFields(supplierViewController.getDisplayedSupplier().getIndirizzo(), supplierViewController.getDisplayedSupplier().getCivico(), supplierViewController.getDisplayedSupplier().getCAP());

        });

    }

    public void setInitialFields(String address, String civicNumber, String cap) {

        try {
            String searchContactQuery = "SELECT * FROM Recapito WHERE Indirizzo = ? AND CAP = ? AND Civico = ?";
            PreparedStatement statement = HotelSupplyManagementMain.conn.prepareStatement(searchContactQuery);
            statement.setString(1, address);
            statement.setString(2, cap);
            statement.setString(3, civicNumber);
            System.out.println(statement);
            ResultSet resultset = statement.executeQuery();
            addressField.setText(resultset.getString(1));
            civicNumberField.setText(resultset.getString(3));
            CAPField.setText(resultset.getString(2));
            locationField.setText(resultset.getString(4));
            provinceField.setText(resultset.getString(5));
            phoneNumberField.setText(resultset.getString(6));
            mailField.setText(resultset.getString(7));
        }
        catch (SQLException e){
            System.err.println("Errore durante la query di estrapolazione informazioni su Recapito: " + e.getMessage());
        }
    }

    private boolean isValidEmail(String email) {                // una volta confermate delle modifiche viene effettuato il controllo sulla mail

        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        return pattern.matcher(email).matches();

    }

    public void confirmData(ActionEvent event) {

        try {
            verifyCAPLength();
            verifyEmptyField();
            if (!isValidEmail(mailField.getText())) {
                throw new RuntimeException("E-mail");
            }
            if (supplierViewController != null)                                                                                          // Serve per capire quale finestra ha richiamato quella di aggiunta dei dettagli sul recapito
                supplierViewController.setContactDetails(addressField.getText(), CAPField.getText(), civicNumberField.getText());
            ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
        }
        catch (RuntimeException emptyFields) {
            generateAlert(emptyFields.getMessage());
        }

    }

    public void generateAlert(String alertType) {

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Errore");
        switch (alertType) {
            case "Parametro mancante" -> {
                alert.setHeaderText("Parametri assenti");
                alert.setContentText("E' necessario inserire tutti i parametri richiesti");
            }
            case "CAP non valido" -> {
                alert.setHeaderText("Formato CAP errato");
                alert.setContentText("Il CAP deve contenere necessariamente 5 cifre");
            }
            case "E-mail" -> {
                alert.setHeaderText("Formato e-mail errato");
                alert.setContentText("Inserisci una e-mail valida");
            }
        }
        alert.showAndWait();

    }

    public void verifyCAPLength() throws RuntimeException {
        if (CAPField.getText().length() != 5)
            throw new RuntimeException("CAP non valido");
    }

    public void executeQuery(String address, String civicNumber, String CAP) {                    // Una volta validati tutti i dati del Cliente e di Dati Azienda, viene effettuata la query di aggiunta al database

        String insertContactQuery = "UPDATE Recapito SET Indirizzo = ?, CAP = ?, Civico = ?, Localita = ?, Provincia = ?," +
                " Telefono = ?, Mail = ? WHERE Indirizzo = ? AND CAP = ? AND Civico = ?";
        try {
            PreparedStatement preparedStatement = HotelSupplyManagementMain.conn.prepareStatement(insertContactQuery);
            substituteValueInQuery(preparedStatement, address, civicNumber, CAP);
            try {
                System.out.println(preparedStatement);
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

    public void substituteValueInQuery(PreparedStatement preparedStatement, String address, String civicNumber, String CAP) throws SQLException {

        preparedStatement.setString(1, addressField.getText());
        preparedStatement.setString(2, CAPField.getText());
        preparedStatement.setString(3, civicNumberField.getText());
        preparedStatement.setString(4, locationField.getText());
        preparedStatement.setString(5, provinceField.getText());
        preparedStatement.setString(6, phoneNumberField.getText());
        preparedStatement.setString(7, mailField.getText());
        preparedStatement.setString(8, address);
        preparedStatement.setString(9, CAP);
        preparedStatement.setString(10, civicNumber);

    }

    public void verifyEmptyField() throws RuntimeException {

        String errorMessage = "Parametro mancante";

        int i = 0;
        while (i < 7) {
            switch (i) {
                case 0 -> {
                    if ("".equals(addressField.getText()))
                        throw new RuntimeException(errorMessage);
                }
                case 1 -> {
                    if ("".equals(CAPField.getText()))
                        throw new RuntimeException(errorMessage);
                }
                case 2 -> {
                    if ("".equals(civicNumberField.getText()))
                        throw new RuntimeException(errorMessage);
                }
                case 3 -> {
                    if ("".equals(locationField.getText()))
                        throw new RuntimeException(errorMessage);
                }
                case 4 -> {
                    if ("".equals(provinceField.getText()))
                        throw new RuntimeException(errorMessage);
                }
                case 5 -> {
                    if ("".equals(phoneNumberField.getText()))
                        throw new RuntimeException(errorMessage);
                }
                case 6 -> {
                    if ("".equals(mailField.getText()))
                        throw new RuntimeException(errorMessage);
                }
            }
            i++;
        }

    }

    public void setSupplierViewController(SupplierViewController supplierViewController) {
        this.supplierViewController = supplierViewController;
    }

}
