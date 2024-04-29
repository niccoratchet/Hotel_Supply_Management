package com.unifisweproject.hotelsupplymanagement.main;


import com.unifisweproject.hotelsupplymanagement.FXMLWindowLoader;
import com.unifisweproject.hotelsupplymanagement.login.FirstAccessWindowController;
import com.unifisweproject.hotelsupplymanagement.login.LoginWindowController;
import javafx.application.Application;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextFormatter;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import java.util.function.UnaryOperator;

public class HotelSupplyManagementMain extends Application {

    public static Connection conn;
    public static Image icon;

    public static UnaryOperator<TextFormatter.Change> filterDouble = change -> {           // Creazione del Formatter che garantisce l'inserimento corretto di un numero decimale
        String text = change.getText();
        if (text.matches("[0-9]*\\.?[0-9]*")) {
            return change;
        }
        return null;
    };
    public static UnaryOperator<TextFormatter.Change> filterInt = change -> {             // Creazione del Formatter che garantisce l'inserimento corretto di un numero intero
        String text = change.getText();
        if (text.matches("[0-9]*")) {
            return change;
        }
        return null;
    };
    public static UnaryOperator<TextFormatter.Change> filterFiscalCodeFormatter = change -> {             // Creazione del Formatter per lo sconto: contiene unicamente numeri e al massimo 2 cifre
        String newText = change.getControlNewText().toUpperCase();
        change.setText(newText);
        change.setRange(0, change.getControlText().length());
        String text = change.getText();
        if (text.matches("[a-zA-Z0-9]*") && change.getControlNewText().length() <= 16) {
            return change;
        }
        return null;
    };
    public static UnaryOperator<TextFormatter.Change> filterDiscount = change -> {             // Creazione del Formatter per lo sconto: contiene unicamente numeri e al massimo 2 cifre
        String text = change.getText();
        if (text.matches("[0-9]*") && change.getControlNewText().length() <= 2) {
            return change;
        }
        return null;
    };
    public static UnaryOperator<TextFormatter.Change> filterCivicNumber = change -> {
        String newText = change.getControlNewText().toUpperCase();
        change.setText(newText);
        change.setRange(0, change.getControlText().length());
        String text = change.getText();
        if (text.matches("[a-zA-Z0-9]*") && change.getControlNewText().length() <= 5)
            return change;
        return null;
    };

    @Override
    public void start(Stage stage) {

        conn = null;
        try {
            String url = "jdbc:sqlite:hotel_supply_management.db";
            connectToDB(url);
            icon = new Image(Objects.requireNonNull(HotelSupplyManagementMain.class.getResourceAsStream("/com/unifisweproject/hotelsupplymanagement/Icon/HotelSupplyManagementIcon.png")));
            if(!isFirstAccess()) {
                try {
                    FXMLWindowLoader.loadFXML(getClass().getResource("/com/unifisweproject/hotelsupplymanagement/login/LoginWindow.fxml"), new LoginWindowController(), false, null, "Hotel Supply Management", false);
                }
                catch(IOException e) {
                    System.err.println("Errore durante il caricamento della pagine del file LoginWindow.fxml: " + e.getMessage());
                }
            }
        }
        catch (SQLException e) {
            System.out.println("Errore durante l'apertura del DataBase: " + e.getMessage());
        }

    }

    public static void main(String[] args) {

        launch(args);
        try {
            if(conn != null)
                conn.close();
        }
        catch(SQLException e) {
            System.out.println("Errore durante la chiusura della connessione al DataBase: " + e.getMessage());
        }

    }

    public static <newType, oldType> ArrayList<newType> castArrayList(ArrayList<oldType> list){             // Il metodo permette di fare il casting di una lista generica di Object in un altro tipo specificato
        ArrayList<newType> newlyCastedArrayList = new ArrayList<>();
        for(oldType listObject : list){
            newlyCastedArrayList.add((newType)listObject);
        }
        return newlyCastedArrayList;
    }

    public boolean isFirstAccess() {

        boolean isFirstAccess = false;
        String filePath = "password.txt";
        File file = new File(filePath);
        if(file.length() == 0) {
            isFirstAccess = true;
            try {
                FXMLWindowLoader.loadFXML(getClass().getResource("/com/unifisweproject/hotelsupplymanagement/login/FirstAccessWindow.fxml"), new FirstAccessWindowController(), false, null, "Hotel Supply Management", false);
            }
            catch (IOException e) {
                System.err.println("Errore durante il caricamento della pagina del file FirstAccessWindow.fxml: " + e.getMessage());
            }
        }
        return isFirstAccess;

    }

    public static void connectToDB(String url) throws SQLException {             // Il metodo permette di creare la connessione sia al DB principale che al DB di test
        conn = DriverManager.getConnection(url);
        System.out.println("Connessione al DB effettuata con successo!");
    }

    public static void generateAlert(Alert.AlertType alertType, String messageTitle, String messageType, String messageContent) {

        Alert alert = new Alert(alertType);
        alert.setTitle(messageTitle);
        alert.setHeaderText(messageType);
        alert.setContentText(messageContent);
        alert.showAndWait();

    }

    public static void openCredits() {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Credits");
        alert.setHeaderText("Hotel Supply Management");
        alert.setContentText("Software developed by Niccolò Redi, Lorenzo Gazzini and Edoardo Cravegni. \n For the SWE Exam of UNIFI (A.A.2023/2024.)");
        alert.showAndWait();

    }

    public static boolean displayConfirmationAlert(String title, String header, String content) {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        ButtonType buttonTypeYes = new ButtonType("Sì");
        ButtonType buttonTypeNo = new ButtonType("No");
        alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == buttonTypeYes;

    }

    public static TextFormatter<String> getDoubleFormatter() {
        return new TextFormatter<>(filterDouble);
    }

    public static TextFormatter<String> getIntFormatter() {
        return new TextFormatter<>(filterInt);
    }

    public static TextFormatter<String> getFiscalCodeFormatter() {
        return new TextFormatter<>(filterFiscalCodeFormatter);
    }
    public static TextFormatter<String> getDiscountFormatter() {
        return new TextFormatter<>(filterDiscount);
    }

    public static TextFormatter<String> getNumberOnlyStringFormatter(int numberOfCharacters) {

        UnaryOperator<TextFormatter.Change> filterNumberOnlyString = change -> {             // Creazione del Formatter per inserimento della partita IVA (contiene solo numeri e non può essere più lungo di 11 caratteri)
            String text = change.getText();
            if (text.matches("[0-9]*") && change.getControlNewText().length() <= numberOfCharacters) {
                return change;
            }
            return null;
        };
        return new TextFormatter<>(filterNumberOnlyString);

    }
    public static TextFormatter<String> getCivicNumberFormatter() {
        return new TextFormatter<>(filterCivicNumber);
    }

}