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

public class ItemDisplayWindow implements Initializable {

    @FXML
    private TextField nameField;
    @FXML
    private TextField priceField;
    @FXML
    private TextField amountField;
    @FXML
    private DatePicker datePicker;
    @FXML
    private TextField descriptionField;
    @FXML
    private Label codeLabel;
    @FXML
    private Button modifyButton;
    @FXML
    private Button backButton;
    private final Item displayedItem;
    private final ItemManagementWindowController controller;

    public ItemDisplayWindow(Item displayedItem) {
        this.controller = ItemManagementWindowController.getInstance();
        this.displayedItem = displayedItem;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        addItemInfo();
        priceField.setTextFormatter(HotelSupplyManagementMain.getDoubleFormatter());
        amountField.setTextFormatter(HotelSupplyManagementMain.getIntFormatter());
        modifyButton.setOnAction(this::modifyItem);
        backButton.setOnAction(this::closeItemView);

    }

    public void addItemInfo() {

        codeLabel.setText("Dati articolo n°" + displayedItem.getCodice_articolo());
        nameField.setText(displayedItem.getNome());
        priceField.setText(Double.toString(displayedItem.getPrezzo()));
        amountField.setText(Integer.toString(displayedItem.getQuantita()));
        datePicker.setValue(LocalDate.parse(displayedItem.getData_inserimento()));
        descriptionField.setText(displayedItem.getDescrizione());

    }

    public void modifyItem(ActionEvent event) {
        controller.modifyRow(event, displayedItem);
    }

    public void closeItemView(ActionEvent event) {
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
    }

    public TextField getDescriptionField() {
        return descriptionField;
    }

    public Label getCodeLabel() {
        return codeLabel;
    }

    public DatePicker getDatePicker() {
        return datePicker;
    }

    public TextField getAmountField() {
        return amountField;
    }

    public TextField getPriceField() {
        return priceField;
    }

    public TextField getNameField() {
        return nameField;
    }

}
