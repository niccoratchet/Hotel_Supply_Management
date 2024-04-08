package com.unifisweproject.hotelsupplymanagement;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginWindowController {

    @FXML
    private TextField passwordTextField;

    public void verifyPassword(ActionEvent event) throws IOException {              // TODO: Sposta la parte di verifica password in un altro controller

        String password = passwordTextField.getText();
        if(password.equals("Cazzo")) {
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
            passwordTextField.setText("Coglione");
        }

    }

}
