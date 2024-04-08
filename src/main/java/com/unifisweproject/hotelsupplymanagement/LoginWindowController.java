package com.unifisweproject.hotelsupplymanagement;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class LoginWindowController {

    @FXML
    private TextField passwordTextField;

    public void verifyPassword(ActionEvent event) throws IOException {

        String password = passwordTextField.getText();
        String filePath = "password.txt";
        try {
            String fileContent = Files.readString(Paths.get(filePath));
            if(password.equals(fileContent)) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("MainMenuWindow.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setTitle("Hotel Supply Management");
                stage.getIcons().add(HotelSupplyManagementMain.icon);
                stage.setResizable(false);
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
            }
            else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Errore");
                alert.setHeaderText("Password errata");
                alert.setContentText("Inserire la password corretta");
                alert.showAndWait();
            }

        }
        catch (FileNotFoundException e) {
            System.out.println("File non trovato: " + e.getMessage());
        }
    }

}