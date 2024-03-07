package com.unifisweproject.hotelsupplymanagement;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;


public class AddSupplierViewController implements Initializable {


    @FXML
    private DatePicker datePicker;
    @FXML
    private TextField businessNameField;
    @FXML
    private TextField pivaField;
    @FXML
    private TextField addressField;
    @FXML
    private TextField civicField;
    @FXML
    private TextField capField;

    private SupplierManagementSceneController supplierManagementSceneController;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        datePicker.setValue(LocalDate.now());
    }

    public void closeAddView(ActionEvent event) {
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
    }

    public void createSupplier(ActionEvent event) {
        Supplier newSupplier = new Supplier(datePicker.getValue().toString(), pivaField.getText(), businessNameField.getText(), addressField.getText(), civicField.getText(), capField.getText());
        supplierManagementSceneController.addRow(newSupplier);

        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();        // Istruzione per chiudere il form

    }

    public void setSupplierManagementSceneController(SupplierManagementSceneController supplierManagementSceneController) {
        this.supplierManagementSceneController = supplierManagementSceneController;
    }

}
