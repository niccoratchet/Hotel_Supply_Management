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
import javafx.stage.Stage;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class SupplyViewController implements Initializable {

    @FXML
    private Label codeLabel;
    @FXML
    private DatePicker datePicker;
    @FXML
    private ChoiceBox<String> supplierList;
    @FXML
    private TableView<Item> itemTableView;
    @FXML
    private TableColumn<Item, Integer> itemCodeColumn;
    @FXML
    private TableColumn<Item, String> itemNameColumn;
    @FXML
    private TableColumn<Item, Integer> itemQuantityColumn;
    @FXML
    private TableColumn<Item, Double> itemPriceColumn;
    @FXML
    private TableColumn<Item, String> itemDescriptionColumn;
    private Supply displayedSupply;
    private final ObservableList<Item> itemList = FXCollections.observableArrayList();
    private SuppliesManagementSceneController suppliesManagementSceneController;                // TODO: Rendere possibile l'eliminazione dalla visualizzazione? La modifica?

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Platform.runLater(this::addSupplyInfo);
    }

    public void addSupplyInfo() {

        datePicker.setValue(LocalDate.parse(displayedSupply.getData_fornitura()));
        codeLabel.setText("Dati fornitura n°" + displayedSupply.getCodice_fornitura());
        setCellValueFactory();
        getSupplierName();
        addItemsInTable();

    }

    public void addItemsInTable() {                     // Inserisce gli articoli nella tabella effettuando una query sul DB e facendo un Join per collegarsi alla tabella "Fornitura".

        String searchItemQuery = "SELECT A.Codice_Articolo, A.Nome, F.Prezzo, F.Quantita, A. Descrizione, F.Data_Fornitura FROM Fornitura F" +
                " JOIN Articolo A ON F.Codice_Articolo = A.Codice_Articolo WHERE F.Codice_Fornitura = " + displayedSupply.getCodice_fornitura();
        try {
            Statement statement = HotelSupplyManagementMain.conn.createStatement();
            ResultSet resultSet = statement.executeQuery(searchItemQuery);
            while (resultSet.next()) {
                itemList.add(new Item(resultSet.getInt(1), resultSet.getInt(4), resultSet.getDouble(3),
                        resultSet.getString(2), resultSet.getString(5), resultSet.getString(6)));
            }
            itemTableView.setItems(itemList);
        }
        catch (SQLException e) {
            System.out.println("Errore durante l'estrapolazione degli articoli legati a questa fornitura: " + e.getMessage());
        }

    }

    public void getSupplierName() {                         // In base al codice del fornitore, estrae il nome del fornitore dalla tabella "Fornitore" e lo inserisce nella ChoiceBox.

        String searchSupplierQuery = "SELECT Ragione_Sociale FROM Fornitore WHERE Codice_Fornitore = " + displayedSupply.getCodice_fornitore();
        try {
            Statement statement = HotelSupplyManagementMain.conn.createStatement();
            ResultSet resultSet = statement.executeQuery(searchSupplierQuery);
            while (resultSet.next()) {
                supplierList.setValue(resultSet.getString(1));
            }
        }
        catch (SQLException e) {
            System.out.println("Errore durante l'estrapolazione del nome del fornitore: " + e.getMessage());
        }

    }

    public void closeSupplyView(ActionEvent event) {
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
    }

    public void deleteSupply(ActionEvent event) {
        suppliesManagementSceneController.deleteRow();
        closeSupplyView(event);
    }

    public void setCellValueFactory() {

        itemCodeColumn.setCellValueFactory(new PropertyValueFactory<>("Codice_articolo"));
        itemNameColumn.setCellValueFactory(new PropertyValueFactory<>("Nome"));
        itemQuantityColumn.setCellValueFactory(new PropertyValueFactory<>("Quantita"));
        itemPriceColumn.setCellValueFactory(new PropertyValueFactory<>("Prezzo"));
        itemDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("Descrizione"));

    }

    public void setSuppliesManagementSceneController(SuppliesManagementSceneController suppliesManagementSceneController) {
        this.suppliesManagementSceneController = suppliesManagementSceneController;
    }

    public void setDisplayedSupply(Supply displayedSupply) {
        this.displayedSupply = displayedSupply;
    }

}
