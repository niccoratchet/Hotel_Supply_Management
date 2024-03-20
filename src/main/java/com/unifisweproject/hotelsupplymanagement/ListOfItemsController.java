package com.unifisweproject.hotelsupplymanagement;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;

public class ListOfItemsController implements Initializable {

    private AddOrderViewController addOrderViewController;
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
    private TextField quantityField;
    @FXML
    private Button addButton;
    @FXML
    private ObservableList<Item> itemList = FXCollections.observableArrayList();

    private MainMenuController mainMenuController;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        Platform.runLater(this::createRows);

        itemTableView.getSelectionModel().selectedItemProperty().addListener((observableValue, oldSelection, newSelection) -> {

            if(newSelection != null) {       // Aggiunta del listener nella tabella per rilevare quale elemento viene selezionato
                quantityField.setVisible(true);
                quantityLabel.setVisible(true);
                quantityField.setDisable(false);
            } else {
                quantityField.setVisible(false);
                quantityLabel.setVisible(false);
                quantityField.setDisable(true);
            }

        });

        quantityField.textProperty().addListener((obs, oldText, newText) -> {       // Controlla se la quantita non è vuota

            if (!newText.trim().isEmpty()) {        // Se inserisco la quantita
                addButton.setDisable(false);
            } else {
                addButton.setDisable(true);
            }

        });

        itemCodeColumn.setCellValueFactory(new PropertyValueFactory<>("Codice_articolo"));
        itemNameColumn.setCellValueFactory(new PropertyValueFactory<>("Nome"));
        itemQuantityColumn.setCellValueFactory(new PropertyValueFactory<>("Quantita"));

        itemTableView.setItems(itemList);
        itemTableView.getColumns().setAll(itemCodeColumn, itemNameColumn, itemQuantityColumn);


        UnaryOperator<TextFormatter.Change> filterInt = change -> {             // Creazione del Formatter per inserimento delle quantità
            String text = change.getText();
            if (text.matches("[0-9]*")) {
                return change;
            }
            return null;
        };

        TextFormatter<String> textFormatterInt = new TextFormatter<>(filterInt);
        quantityField.setTextFormatter(textFormatterInt);

    }

    public void setAddOrderViewController(AddOrderViewController addOrderViewController) {
        this.addOrderViewController = addOrderViewController;
    }

    public void setMainMenuController(MainMenuController mainMenuController) {
        this.mainMenuController = mainMenuController;
    }

    public void closeAddItem(ActionEvent event) {
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
    }

    public void addItemToOrder(ActionEvent event){

        Item selectedItem = itemTableView.getSelectionModel().getSelectedItem();
        int newAmount = selectedItem.getQuantita() - Integer.parseInt(quantityField.getText());

        if(newAmount<0) {

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Errore");
            alert.setContentText("Non ci sono abbastanza articoli in magazzino, inserire una quantita' valida");
            alert.showAndWait();

        } else{

            Item itemToAdd = new Item(selectedItem.getCodice_articolo(), Integer.parseInt(quantityField.getText()), selectedItem.getPrezzo(), selectedItem.getNome(), selectedItem.getDescrizione(), selectedItem.getData_inserimento());

            addOrderViewController.addRow(itemToAdd);
            addOrderViewController.newAmount.add(newAmount);

            addOrderViewController.itemInOrder.addCodice_Articolo(selectedItem.getCodice_articolo());
            addOrderViewController.itemInOrder.addQuantita(Integer.parseInt(quantityField.getText()));


            ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();        // Istruzione per chiudere il form

        }

    }


    public void createRows()  {

        if (!mainMenuController.getIsNotFirstTimeLoad().get(0)) {
            ResultSet resultSet = mainMenuController.getItemManagement().getRows(true, null);

            try {
                while (resultSet.next()) {
                    Item item = new Item(resultSet.getInt(1), resultSet.getInt(4),
                            resultSet.getDouble(3), resultSet.getString(2), resultSet.getString(5), resultSet.getString(6));
                    mainMenuController.getItemManagement().getItemList().add(item);
                }
                mainMenuController.getIsNotFirstTimeLoad().set(0, true);
            }

            catch (SQLException e) {
                System.err.println("Errore durante il riempimento della tabella");
            }
        }

        itemList.addAll(mainMenuController.getItemManagement().getItemList());

        itemCodeColumn.setCellValueFactory(new PropertyValueFactory<>("Codice_articolo"));
        itemNameColumn.setCellValueFactory(new PropertyValueFactory<>("Nome"));
        itemQuantityColumn.setCellValueFactory(new PropertyValueFactory<>("Quantita"));

        itemTableView.setItems(itemList);                       // Inserisce nella tabella tutte le righe degli Item presenti nel DB

    }

}
