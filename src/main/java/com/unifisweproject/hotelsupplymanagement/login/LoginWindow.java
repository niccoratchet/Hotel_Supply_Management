package com.unifisweproject.hotelsupplymanagement.login;

import com.unifisweproject.hotelsupplymanagement.FXMLWindowLoader;
import com.unifisweproject.hotelsupplymanagement.main.HotelSupplyManagementMain;
import com.unifisweproject.hotelsupplymanagement.main.MainMenuWindow;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ResourceBundle;

import static javafx.scene.control.Alert.AlertType.ERROR;

/**
 * La classe si occupa do verificare la password inserita dall'utente e di aprire la finestra principale dell'applicazione
 * */

public class LoginWindow implements Initializable {

    @FXML
    private TextField passwordTextField;
    @FXML
    private Button loginButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        loginButton.setOnAction(event -> {
            try {
                verifyPassword(event);
            } catch (IOException e) {
                System.err.println("Errore durante il login: " + e.getMessage());
            }
        });

    }

    public void verifyPassword(ActionEvent event) throws IOException {

        String password = passwordTextField.getText();
        String filePath = "password.txt";
        try {
            String fileContent = Files.readString(Paths.get(filePath));
            if (password.equals(fileContent)) {
                MainMenuWindow.getInstance().setStage((javafx.stage.Stage) loginButton.getScene().getWindow());                       // Imposta lo Stage per la finestra principale
                FXMLWindowLoader.loadFXML(getClass().getResource("/com/unifisweproject/hotelsupplymanagement/main/MainMenuWindow.fxml"),
                        MainMenuWindow.getInstance(), true, event, "Hotel Supply Management", false);
            }
            else {
                HotelSupplyManagementMain.generateAlert(ERROR, "Errore","Password errata", "La password inserita non è corretta");
            }
        }
        catch (FileNotFoundException e) {
            System.out.println("File non trovato: " + e.getMessage());
        }

    }


}