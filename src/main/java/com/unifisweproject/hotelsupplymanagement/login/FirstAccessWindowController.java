package com.unifisweproject.hotelsupplymanagement.login;

import com.unifisweproject.hotelsupplymanagement.FXMLWindowLoader;
import com.unifisweproject.hotelsupplymanagement.main.HotelSupplyManagementMain;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * La classe si occupa di gestire la creazione della password iniziale per l'accesso all'applicazione
 * */

public class FirstAccessWindowController implements Initializable {

    private static final FirstAccessWindowController instance = new FirstAccessWindowController();          // Applicazione SingleTon per la finestra di primo accesso
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private Button confirmButton;
    @FXML
    private Button creditsButton;

    private FirstAccessWindowController() {         // Costruttore privato per evitare la creazione di nuove istanze (SingleTon)
    }

    public static FirstAccessWindowController getInstance() {           // Metodo per ottenere l'istanza della classe (SingleTon)
        return instance;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        Platform.runLater(() -> {
            confirmButton.setOnAction(event -> {
                try {
                    confirmPassword(event);
                } catch (IOException e) {
                    System.err.println("Errore durante la conferma della password: " + e.getMessage());
                }
            });
            creditsButton.setOnAction(event -> HotelSupplyManagementMain.openCredits());
        });

    }

    public void confirmPassword(ActionEvent event) throws IOException {

        if (passwordField.getText().isEmpty() || confirmPasswordField.getText().isEmpty()) {
            HotelSupplyManagementMain.generateAlert(Alert.AlertType.ERROR,"Errore", "Almeno uno dei due campi e' vuoto.");
        }
        else if (passwordField.getText().equals(confirmPasswordField.getText())) {
            File file = new File("password.txt");
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(passwordField.getText());
                HotelSupplyManagementMain.generateAlert(Alert.AlertType.INFORMATION,"Successo", "La password e' stata creata con successo!");
                FXMLWindowLoader.loadFXML(getClass().getResource("/com/unifisweproject/hotelsupplymanagement/login/LoginWindow.fxml"),
                        LoginWindowController.getInstance(), true, event, "Hotel Supply Management", false);
            }
            catch (IOException e) {
                System.out.println("Errore nella scrittura su file: " + e.getMessage());
            }
        }
        else {
            HotelSupplyManagementMain.generateAlert(Alert.AlertType.ERROR,"Password Diverse", "Le due password inserite non corrispondono");
        }
    }

}
