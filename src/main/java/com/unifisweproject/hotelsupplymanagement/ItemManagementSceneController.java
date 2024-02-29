package com.unifisweproject.hotelsupplymanagement;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.SelectionModel;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class ItemManagementSceneController implements Initializable {

    @FXML
    private TableView<Item> itemTable;
    @FXML
    private TableColumn<Item, Integer> IDColumn;
    @FXML
    private TableColumn<Item, String> NameColumn;
    @FXML
    private TableColumn<Item, Integer> AmountColumn;
    @FXML
    private TableColumn<Item, Double> PriceColumn;
    @FXML
    private TableColumn<Item, String> DescriptionColumn;
    @FXML
    private TableColumn<Item, String> DateColumn;
    @FXML
    private Button modifyButton;
    @FXML
    private Button deleteButton;

    private ItemManagement itemManagement;
    ObservableList<Item> itemRows = FXCollections.observableArrayList();    // Lista di righe presenti nella tabella, si aggiorna nel caso dell'aggiunta di una riga

    @Override
    public void initialize(URL url, ResourceBundle rb) {            // Il metodo inizializza la tabella, inserendo tutte le righe presenti nel DataBase nella tabella Articolo
        Platform.runLater(this::createRows);

        itemTable.getSelectionModel().selectedItemProperty().addListener((observableValue, oldSelection, newSelection) -> {
            if(newSelection != null) {
                modifyButton.setDisable(false);
                deleteButton.setDisable(false);
            }
            else {
                modifyButton.setDisable(true);
                deleteButton.setDisable(true);
            }
        });

    }

    public void createRows()  {

        ResultSet resultSet = itemManagement.getRows();

        try {
            while (resultSet.next()) {
                Item item = new Item(resultSet.getInt(1), resultSet.getInt(4),
                        resultSet.getDouble(3), resultSet.getString(2), resultSet.getString(5), resultSet.getString(6));
                itemManagement.getItemList().add(item);
            }
        }

        catch (SQLException e) {
            System.err.println("Errore");
        }

        itemRows.addAll(itemManagement.getItemList());

        IDColumn.setCellValueFactory(new PropertyValueFactory<>("Codice_articolo"));
        NameColumn.setCellValueFactory(new PropertyValueFactory<>("Nome"));
        AmountColumn.setCellValueFactory(new PropertyValueFactory<>("Quantita"));
        PriceColumn.setCellValueFactory(new PropertyValueFactory<>("Prezzo"));
        DescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("Descrizione"));
        DateColumn.setCellValueFactory(new PropertyValueFactory<>("Data_inserimento"));

        itemTable.setItems(itemRows);                       // Inserisce nella tabella tutte le righe degli Item presenti nel DB

    }

    public void displayAddView() {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AddItemView.fxml"));
            Parent root = loader.load();

            AddItemViewController addItemController = loader.getController();
            addItemController.setItemManagementSceneController(this);

            Stage stage = new Stage();
            stage.setTitle("Aggiungi prodotto");
            stage.setScene(new Scene(root, 580, 400));
            stage.show();
        }
        catch (IOException e) {
            System.err.println(e.getMessage());
        }

    }

    public void addRow(Item newItem) {

        newItem.setCodice_articolo(itemManagement.getNextItemCode() + 1);
        itemManagement.getItemList().add(newItem);
        itemManagement.add(newItem);
        updateTable();

    }

    public void modifyRow(Item toBeModified) {
        itemManagement.modify(toBeModified);
        updateTable();
    }

    public void updateTable() {

        Platform.runLater(() -> {                       // Pulisci e aggiorna la tabella
            itemTable.getItems().clear();
            itemRows.setAll(itemManagement.getItemList());
            itemTable.setItems(itemRows);
        });

    }

    public void setItemManagement(ItemManagement itemManagement) {
        this.itemManagement = itemManagement;
    }

    public void displayItemView(ActionEvent event) throws IOException {

        SelectionModel<Item> selectionModel = itemTable.getSelectionModel();
        Item selectedItem = selectionModel.getSelectedItem();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("ItemView.fxml"));
        Parent root = loader.load();

        ItemViewController itemViewController = loader.getController();
        itemViewController.setDisplayedItem(selectedItem);
        itemViewController.setItemManagementSceneController(this);

        Stage stage = new Stage();
        stage.setTitle(selectedItem.getNome());
        stage.setScene(new Scene(root, 580, 400));
        stage.show();

    }

    public void deleteRow() {
        SelectionModel<Item> selectionModel = itemTable.getSelectionModel();
        Item selectedItem = selectionModel.getSelectedItem();
        itemManagement.getItemList().remove(selectedItem);
        itemManagement.delete(selectedItem.getCodice_articolo());           // TODO: Mettere avviso prima della cancellazione
        updateTable();
    }

}
