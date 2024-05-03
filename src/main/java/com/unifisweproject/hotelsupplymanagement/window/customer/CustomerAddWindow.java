package com.unifisweproject.hotelsupplymanagement.window.customer;

import com.unifisweproject.hotelsupplymanagement.controller.customer.CustomerManagementController;
import com.unifisweproject.hotelsupplymanagement.main.HotelSupplyManagementMain;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;


public class CustomerAddWindow implements Initializable {

    @FXML
    private TextField nameField;
    @FXML
    private DatePicker datePicker;
    @FXML
    private TextField surnameField;
    @FXML
    private TextField fiscalCodeField;
    @FXML
    private TextField discountField;
    @FXML
    private CheckBox privateCheckBox;
    @FXML
    private Button addCompanyDataButton;
    @FXML
    private Button addContactButton;
    @FXML
    private Button addButton;
    @FXML
    private Button backButton;

    private final CustomerManagementController controller;

    public CustomerAddWindow() {
        this.controller = CustomerManagementController.getInstance();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        datePicker.setValue(LocalDate.now());
        fiscalCodeField.setTextFormatter(HotelSupplyManagementMain.getFiscalCodeFormatter());
        discountField.setTextFormatter(HotelSupplyManagementMain.getDiscountFormatter());
        addButton.setOnAction(this::addCustomer);
        backButton.setOnAction(this::closeAddView);
        privateCheckBox.setOnAction(this::disableCompanyData);
        addContactButton.setOnAction(controller::loadAddContactDetailsView);
        addCompanyDataButton.setOnAction(controller::loadAddCompanyDetailsView);

    }

    public void addCustomer(ActionEvent event) {
        controller.createRow(event);
    }

    public void disableCompanyData(ActionEvent event) {
        controller.disableCompanyData();
    }

    public void closeAddView(ActionEvent event) {
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
    }

    public TextField getSurnameField() {
        return surnameField;
    }

    public DatePicker getDatePicker() {
        return datePicker;
    }

    public TextField getNameField() {
        return nameField;
    }

    public TextField getDiscountField() {
        return discountField;
    }

    public TextField getFiscalCodeField() {
        return fiscalCodeField;
    }

    public CheckBox getPrivateCheckBox() {
        return privateCheckBox;
    }
    public Button getAddCompanyDataButton() {
        return addCompanyDataButton;
    }

}
