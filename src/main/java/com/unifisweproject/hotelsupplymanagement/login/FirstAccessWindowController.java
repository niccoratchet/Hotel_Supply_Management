package com.unifisweproject.hotelsupplymanagement.login;

import com.unifisweproject.hotelsupplymanagement.main.HotelSupplyManagementMain;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FirstAccessWindowController {

    private Scene scene;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField confirmPasswordField;


    public void confirmPassword(ActionEvent event) throws IOException {

        if(passwordField.getText().isEmpty() || confirmPasswordField.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Errore");
            alert.setHeaderText("Campi vuoti");
            alert.setContentText("Almeno uno dei due campi e' vuoto.");
            alert.showAndWait();
        }
        else if(passwordField.getText().equals(confirmPasswordField.getText())) {
            File file = new File("password.txt");
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(passwordField.getText());
            } catch (IOException e) {
                System.out.println("Errore nella scrittura su file: " + e.getMessage());
            }
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Successo!");
            alert.setHeaderText("Successo!");
            alert.setContentText("La password e' stata creata con successo!");
            alert.showAndWait();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("LoginWindow.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle("Hotel Supply Management");
            scene = new Scene(root);
            stage.setScene(scene);
            stage.setResizable(false);
            stage.getIcons().add(HotelSupplyManagementMain.icon);
            stage.show();
        }

        else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Errore");
            alert.setHeaderText("Password Diverse");
            alert.setContentText("Le due password inserite non corrispondono");
            alert.showAndWait();
        }
    }

    public void openCredits() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Credits");
        alert.setHeaderText("Hotel Supply Management");
        alert.setContentText("Software developed by Niccolò Redi, Lorenzo Gazzini and Edoardo Cravegni. \n For the SWE Exam of UNIFI (A.A.2023/2024.)");
        alert.showAndWait();
    }


}
