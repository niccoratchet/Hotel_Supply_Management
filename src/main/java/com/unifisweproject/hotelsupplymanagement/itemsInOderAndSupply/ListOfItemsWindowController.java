
/*
    Questa classe funge da controllore per la finestra che visualizza un piccolo riepilogo degli articoli presenti nel DB. Utile all'aggiunta degli articoli che
    compongono un ordine o una fornitura.
*/

package com.unifisweproject.hotelsupplymanagement.itemsInOderAndSupply;

import com.unifisweproject.hotelsupplymanagement.item.Item;
import com.unifisweproject.hotelsupplymanagement.main.MainMenuWindowController;
import com.unifisweproject.hotelsupplymanagement.order.OrderAddWindowController;
import com.unifisweproject.hotelsupplymanagement.supply.SupplyAddWindowController;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;

public class ListOfItemsWindowController implements Initializable {

    private OrderAddWindowController orderAddWindowController = null;               // Questa classe può essere utilizzata sia dalla gestione degli Ordini che delle Forniture
    private SupplyAddWindowController supplyAddWindowController = null;
    @FXML
    private TableView<Item> itemTableView;
    @FXML
    private TableColumn<Item, Integer> itemCodeColumn;
    @FXML
    private TableColumn<Item, String> itemNameColumn;
    @FXML
    private TableColumn<Item, Integer> itemQuantityColumn;
    @FXML
    private Label quantityLabel;
    @FXML
    private Label priceLabel;
    @FXML
    private TextField quantityField;
    @FXML
    private TextField priceField;
    @FXML
    private Button addButton;
    @FXML
    private ObservableList<Item> itemList = FXCollections.observableArrayList();
    private MainMenuWindowController mainMenuWindowController;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        Platform.runLater(this::createRows);
        setSelectionModel();
        setTextFieldListeners();
        setCellValueFactory();
        itemTableView.setItems(itemList);
        itemTableView.getColumns().setAll(itemCodeColumn, itemNameColumn, itemQuantityColumn);
        setTextFormatters();

    }
    public void closeAddItem(ActionEvent event) {
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
    }

    public void setCellValueFactory() {

        itemCodeColumn.setCellValueFactory(new PropertyValueFactory<>("Codice_articolo"));
        itemNameColumn.setCellValueFactory(new PropertyValueFactory<>("Nome"));
        itemQuantityColumn.setCellValueFactory(new PropertyValueFactory<>("Quantita"));

    }

    public void setTextFieldListeners() {

        quantityField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (supplyAddWindowController != null) {
                addButton.setDisable(quantityField.getText().isEmpty() || priceField.getText().isEmpty());
            }
            else {
                addButton.setDisable(quantityField.getText().isEmpty());
            }
        });
        priceField.textProperty().addListener((observable, oldValue, newValue) -> addButton.setDisable(quantityField.getText().isEmpty() || priceField.getText().isEmpty()));

    }

    public void setSelectionModel() {

        itemTableView.getSelectionModel().selectedItemProperty().addListener((observableValue, oldSelection, newSelection) -> {
            if (newSelection != null) {                      // Aggiunta del listener nella tabella per rilevare quale elemento viene selezionato
                quantityField.setVisible(true);
                quantityLabel.setVisible(true);
                quantityField.setDisable(false);
                if (supplyAddWindowController != null) {
                    priceLabel.setVisible(true);
                    priceField.setVisible(true);
                    priceField.setDisable(false);
                }
            }
            else {
                quantityField.setVisible(false);
                quantityLabel.setVisible(false);
                quantityField.setDisable(true);
                if (supplyAddWindowController != null) {
                    priceLabel.setVisible(false);
                    priceField.setVisible(false);
                    priceField.setDisable(true);
                }
            }
        });

    }

    public void setTextFormatters() {

        UnaryOperator<TextFormatter.Change> filterInt = change -> {             // Creazione del Formatter per inserimento delle quantità
            String text = change.getText();
            if (text.matches("[0-9]*")) {
                return change;
            }
            return null;
        };
        TextFormatter<String> textFormatterInt = new TextFormatter<>(filterInt);
        quantityField.setTextFormatter(textFormatterInt);
        UnaryOperator<TextFormatter.Change> filterDouble = change -> {              // Creazione del Formatter per inserimento del prezzo
            String text = change.getText();
            if (text.matches("[0-9]*\\.?[0-9]*")) {
                return change;
            }
            return null;
        };
        TextFormatter<String> textFormatterDouble = new TextFormatter<>(filterDouble);
        priceField.setTextFormatter(textFormatterDouble);

    }

    public void addItemToOrder(ActionEvent event) {

        Item selectedItem = itemTableView.getSelectionModel().getSelectedItem();
        int newAmount;
        if (quantityField.getText().equals("0")) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Errore");
            alert.setContentText("Non e' possibile aggiungere un articolo con quantita' pari a 0");
            alert.showAndWait();
            return;
        }
        if(orderAddWindowController != null) {
            newAmount = selectedItem.getQuantita() - Integer.parseInt(quantityField.getText());
            if(newAmount < 0) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Errore");
                alert.setContentText("Non ci sono abbastanza articoli in magazzino, inserire una quantita' valida");
                alert.showAndWait();
            }
            else {
                addItemInTable(newAmount, selectedItem, event);
            }
        }
        else {
            newAmount = selectedItem.getQuantita() + Integer.parseInt(quantityField.getText());
            addItemInTable(newAmount, selectedItem, event);
        }

    }

    public void addItemInTable(int newAmount, Item selectedItem, ActionEvent event) {                          // Aggiorna la lista di Item nella tabella della finestra precedente

        if (orderAddWindowController != null) {
            Item itemToAdd = new Item(selectedItem.getCodice_articolo(), Integer.parseInt(quantityField.getText()), selectedItem.getPrezzo(),
                    selectedItem.getNome(), selectedItem.getDescrizione(), selectedItem.getData_inserimento());
            orderAddWindowController.addRow(itemToAdd);
            orderAddWindowController.getNewAmount().add(newAmount);
            orderAddWindowController.getItemInOrder().addCodice_Articolo(selectedItem.getCodice_articolo());
            orderAddWindowController.getItemInOrder().addQuantita(Integer.parseInt(quantityField.getText()));
        }
        else {
            Item itemToAdd = new Item(selectedItem.getCodice_articolo(), Integer.parseInt(quantityField.getText()), Double.parseDouble(priceField.getText()),
                    selectedItem.getNome(), selectedItem.getDescrizione(), selectedItem.getData_inserimento());
            supplyAddWindowController.addRow(itemToAdd);
            supplyAddWindowController.getSuppliedAmount().add(newAmount);
            supplyAddWindowController.getItemInSupply().addCodice_Articolo(selectedItem.getCodice_articolo());
            supplyAddWindowController.getItemInSupply().addQuantita(Integer.parseInt(quantityField.getText()));
            supplyAddWindowController.getItemInSupply().addPrezzo(Double.parseDouble(priceField.getText()));
        }
        closeAddItem(event);

    }

    public void createRows()  {

        if (!mainMenuWindowController.getIsNotFirstTimeLoad().get(0)) {
            ResultSet resultSet = mainMenuWindowController.getItemManagement().getRows(true, null);
            try {
                while (resultSet.next()) {
                    int quantita = resultSet.getInt(4);
                    double prezzo = resultSet.getDouble(3) * quantita;

                    Item item = new Item(resultSet.getInt(1), quantita,
                            prezzo, resultSet.getString(2), resultSet.getString(5), resultSet.getString(6));
                    mainMenuWindowController.getItemManagement().getItemList().add(item);
                }
                mainMenuWindowController.getIsNotFirstTimeLoad().set(0, true);
            }
            catch (SQLException e) {
                System.err.println("Errore durante il riempimento della tabella");
            }
        }
        itemList.addAll(mainMenuWindowController.getItemManagement().getItemList());
        itemCodeColumn.setCellValueFactory(new PropertyValueFactory<>("Codice_articolo"));
        itemNameColumn.setCellValueFactory(new PropertyValueFactory<>("Nome"));
        itemQuantityColumn.setCellValueFactory(new PropertyValueFactory<>("Quantita"));
        itemTableView.setItems(itemList);                       // Inserisce nella tabella tutte le righe degli Item presenti nel DB

    }
    public void setAddSupplyViewController(SupplyAddWindowController supplyAddWindowController) {
        this.supplyAddWindowController = supplyAddWindowController;
    }

    public void setAddOrderViewController(OrderAddWindowController orderAddWindowController) {
        this.orderAddWindowController = orderAddWindowController;
    }

    public void setMainMenuController(MainMenuWindowController mainMenuWindowController) {
        this.mainMenuWindowController = mainMenuWindowController;
    }

}
