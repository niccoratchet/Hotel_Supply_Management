package com.unifisweproject.hotelsupplymanagement;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.stage.Stage;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;


public class ItemAddWindowController implements Initializable {


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
    private ItemManagementWindowController itemManagementWindowController;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        datePicker.setValue(LocalDate.now());
        UnaryOperator<TextFormatter.Change> filterDouble = change -> {              // Creazione del Formatter per inserimento del prezzo
            String text = change.getText();
            if (text.matches("[0-9]*\\.?[0-9]*")) {
                return change;
            }
            return null;
        };
        UnaryOperator<TextFormatter.Change> filterInt = change -> {             // Creazione del Formatter per inserimento delle quantità
            String text = change.getText();
            if (text.matches("[0-9]*")) {
                return change;
            }
            return null;
        };
        TextFormatter<String> textFormatterDouble = new TextFormatter<>(filterDouble);
        priceField.setTextFormatter(textFormatterDouble);
        TextFormatter<String> textFormatterInt = new TextFormatter<>(filterInt);
        amountField.setTextFormatter(textFormatterInt);

    }

    public void closeAddView(ActionEvent event) {
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
    }

    public void createItem(ActionEvent event) {

        try {
            int i = 0;
            while (i < 4) {
                switch (i) {
                    case 0 -> {
                        if ("".equals(nameField.getText()))
                            throw new RuntimeException("Nome mancante");
                    }
                    case 1 -> {
                        if ("".equals(priceField.getText()))
                            throw new RuntimeException("Prezzo mancante");
                    }
                    case 2 -> {
                        if ("".equals(amountField.getText()))
                            throw new RuntimeException("Quantità mancante");
                    }
                    case 3 -> {
                        if (datePicker.getValue() == null)
                            throw new RuntimeException("Data mancante");
                    }
                }
                i++;
            }
            Item newItem = new Item(Integer.parseInt(amountField.getText()), Double.parseDouble(priceField.getText()), nameField.getText(), descriptionField.getText(), datePicker.getValue().toString());
            itemManagementWindowController.addRow(newItem);
            ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();        // Istruzione per chiudere il form
        }
        catch (RuntimeException missingParameters) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Errore");
            alert.setHeaderText("Parametri assenti");
            alert.setContentText("Inserire il valore di tutti i dati obbligatori.");
            alert.showAndWait();
        }

    }

    public void setItemManagementSceneController(ItemManagementWindowController itemManagementWindowController) {
        this.itemManagementWindowController = itemManagementWindowController;
    }

}
