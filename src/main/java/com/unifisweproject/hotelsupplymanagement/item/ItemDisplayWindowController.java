package com.unifisweproject.hotelsupplymanagement.item;

import javafx.application.Platform;
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

public class ItemDisplayWindowController implements Initializable {


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
    private ItemManagementWindowController itemManagementWindowController;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        Platform.runLater(this::addItemInfo);
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

    public void addItemInfo() {

        codeLabel.setText("Dati articolo n°" + displayedItem.getCodice_articolo());
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
            displayedItem.setNome(nameField.getText());
            displayedItem.setPrezzo(Double.parseDouble(priceField.getText()));
            displayedItem.setQuantita(Integer.parseInt(amountField.getText()));
            displayedItem.setData_inserimento(datePicker.getValue().toString());
            displayedItem.setDescrizione(descriptionField.getText());
            itemManagementWindowController.modifyRow(displayedItem);

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

    public void closeItemView(ActionEvent event) {
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
    }

    public void setItemManagementSceneController(ItemManagementWindowController itemManagementWindowController) {
        this.itemManagementWindowController = itemManagementWindowController;
    }
}
