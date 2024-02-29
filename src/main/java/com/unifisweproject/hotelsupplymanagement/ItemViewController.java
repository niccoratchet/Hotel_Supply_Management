package com.unifisweproject.hotelsupplymanagement;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.time.LocalDate;
import java.util.Date;
import java.util.ResourceBundle;

public class ItemViewController implements Initializable {


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
    private Item displayedItem;
    private ItemManagementSceneController itemManagementSceneController;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Platform.runLater(this::addItemInfo);
    }

    public void addItemInfo() {

        codeLabel.setText("Dati articolo n°" + displayedItem.getCodice_articolo());         // TODO: Modifica deve essere possibile premerlo SOLO se è stata selezionata una riga
        nameField.setText(displayedItem.getNome());
        priceField.setText(Double.toString(displayedItem.getPrezzo()));
        amountField.setText(Integer.toString(displayedItem.getQuantita()));
        datePicker.setValue(LocalDate.parse(displayedItem.getData_inserimento()));
        descriptionField.setText(displayedItem.getDescrizione());

    }

    public void setDisplayedItem(Item displayedItem) {
        this.displayedItem = displayedItem;
    }

    public void modifyItem(ActionEvent event) {

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
            displayedItem.setNome(nameField.getText());
            displayedItem.setPrezzo(price);
            displayedItem.setQuantita(amount);
            displayedItem.setData_inserimento(datePicker.getValue().toString());
            displayedItem.setDescrizione(descriptionField.getText());
            itemManagementSceneController.modifyRow(displayedItem);
        }

        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();        // Istruzione per chiudere il form

    }

    public void closeItemView(ActionEvent event) {
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
    }

    public void setItemManagementSceneController(ItemManagementSceneController itemManagementSceneController) {
        this.itemManagementSceneController = itemManagementSceneController;
    }
}
