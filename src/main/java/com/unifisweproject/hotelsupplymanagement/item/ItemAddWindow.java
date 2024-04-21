package com.unifisweproject.hotelsupplymanagement.item;

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
import java.util.function.UnaryOperator;


public class ItemAddWindow implements Initializable {

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
    @FXML
    private Button addButton;
    @FXML
    private Button backButton;
    private final ItemManagementWindowController controller;

    public ItemAddWindow() {
        this.controller = ItemManagementWindowController.getInstance();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        datePicker.setValue(LocalDate.now());
        TextFormatter<String> textFormatterDouble = new TextFormatter<>(HotelSupplyManagementMain.filterDouble);
        priceField.setTextFormatter(textFormatterDouble);
        TextFormatter<String> textFormatterInt = new TextFormatter<>(HotelSupplyManagementMain.filterInt);
        amountField.setTextFormatter(textFormatterInt);
        addButton.setOnAction(this::addItem);
        backButton.setOnAction(this::closeAddView);

    }

    public void addItem(ActionEvent event) {
        controller.createItem(event);
    }

    public void closeAddView(ActionEvent event) {
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
    }

    public TextField getPriceField() {
        return priceField;
    }

    public DatePicker getDatePicker() {
        return datePicker;
    }

    public TextField getNameField() {
        return nameField;
    }

    public TextField getAmountField() {
        return amountField;
    }

    public TextField getDescriptionField() {
        return descriptionField;
    }

}
