package com.unifisweproject.hotelsupplymanagement;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;


public class AddItemViewController implements Initializable {


    @FXML
    private DatePicker datePicker;
    @FXML
    private TextField nameField;
    @FXML
    private TextField priceField;
    @FXML
    private TextField amountField;
    @FXML
    private TextField descriptionField;

    private ItemManagementSceneController itemManagementSceneController;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        datePicker.setValue(LocalDate.now());
    }

    public void createItem(ActionEvent event) throws IOException {

        boolean error = false;
        int amount = 0;
        double price = 0;

        try {
            amount = Integer.parseInt(amountField.getText());
        }
        catch (NumberFormatException e) {
            System.err.println("Inserire un valore di quantità valido");
            error = true;
        }
        try {
            price = Double.parseDouble(priceField.getText());
        }
        catch (NumberFormatException e) {
            System.err.println("Inserire un valore di prezzo valido");
            error = true;
        }
        if (!error) {
            Item newItem = new Item(amount, price, nameField.getText(), descriptionField.getText(), datePicker.getValue().toString());
            itemManagementSceneController.addRow(newItem);
        }
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();        // Istruzione per chiudere il form

    }

    public void setItemManagementSceneController(ItemManagementSceneController itemManagementSceneController) {
        this.itemManagementSceneController = itemManagementSceneController;
    }

}
