package com.unifisweproject.hotelsupplymanagement;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;

public class MainMenuController {

    public Button loginButton;
    private Stage stage;
    private Scene scene;

    private ItemManagement itemManagement;

    @FXML
    TextField passwordTextField;

    public void switchToScene2(ActionEvent event) throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("ItemManagementScene.fxml"));
        Parent root = loader.load();

        ItemManagementSceneController itemManagementSceneController = loader.getController();
        itemManagementSceneController.setItemManagement(itemManagement);

        stage = (Stage)((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();

    }

    public void verifyPassword(ActionEvent event) throws IOException {              // TODO: Sposta la parte di verifica password in un altro controller

        String password = passwordTextField.getText();

        if(password.equals("Cazzo")) {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("MainMenuScene.fxml"));
            Parent root = loader.load();

            MainMenuController mainMenuController = loader.getController();
            mainMenuController.setItemManagement(itemManagement);

            if(itemManagement == null) {
                System.out.println("Cazzo");
            }

            stage = (Stage)((Node) event.getSource()).getScene().getWindow();
            stage.setTitle("Hotel Supply Management");
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

        }
        else {
            passwordTextField.setText("Coglione");
        }

    }

    public void setItemManagement(ItemManagement itemManagement) {
        this.itemManagement = itemManagement;
    }

}
