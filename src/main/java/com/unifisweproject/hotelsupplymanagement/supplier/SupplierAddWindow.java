package com.unifisweproject.hotelsupplymanagement.supplier;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.stage.Stage;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;


public class SupplierAddWindow implements Initializable {


    @FXML
    private DatePicker datePicker;
    @FXML
    private Button addButton;
    @FXML
    private Button backButton;
    private SupplierManagementController controller;
    /*private ContactDetailsAddWindow contactDetailsAddWindowController = null;
    private CompanyDetailsAddWindow companyDetailsAddWindowController = null;*/

    //public ItemAddWindow() {
    //    this.controller = ManagementController.getInstance();
    //}

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        datePicker.setValue(LocalDate.now());
        addButton.setOnAction(this::addSupplier);
        backButton.setOnAction(this::closeAddView);
    }

    public void addSupplier(ActionEvent event) {
        //controller.createRow(event);
    }

    public void closeAddView(ActionEvent event) {
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
    }

    public DatePicker getDatePicker() {
        return datePicker;
    }
}
