package com.unifisweproject.hotelsupplymanagement.login;

import com.unifisweproject.hotelsupplymanagement.FXMLWindowLoader;
import com.unifisweproject.hotelsupplymanagement.main.HotelSupplyManagementMain;
import com.unifisweproject.hotelsupplymanagement.main.MainMenuWindowController;
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

public class LoginWindowController implements Initializable {

    private static final LoginWindowController instance = new LoginWindowController();      // Applicazione SingleTon per la finestra di login
    @FXML
    private TextField passwordTextField;
    @FXML
    private Button loginButton;

    private LoginWindowController() {           // Costruttore privato per evitare la creazione di nuove istanze (SingleTon)
    }

    public static LoginWindowController getInstance() {         // Metodo per ottenere l'istanza della classe (SingleTon)
        return instance;
    }

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
                MainMenuWindowController.getInstance().setStage((javafx.stage.Stage) loginButton.getScene().getWindow());                       // Imposta lo Stage per la finestra principale
                FXMLWindowLoader.loadFXML(getClass().getResource("/com/unifisweproject/hotelsupplymanagement/main/MainMenuWindow.fxml"),
                        MainMenuWindowController.getInstance(), true, event, "Hotel Supply Management", false);
            }
            else {
                HotelSupplyManagementMain.generateAlert(ERROR,"Password errata", "La password inserita non è corretta");
            }
        }
        catch (FileNotFoundException e) {
            System.out.println("File non trovato: " + e.getMessage());
        }

    }


}