package com.unifisweproject.hotelsupplymanagement.supply;

import com.unifisweproject.hotelsupplymanagement.main.HotelSupplyManagementMain;
import com.unifisweproject.hotelsupplymanagement.item.Item;
import com.unifisweproject.hotelsupplymanagement.order.Order;
import com.unifisweproject.hotelsupplymanagement.order.OrderManagementController;
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
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class SupplyDisplayWindow implements Initializable {

    @FXML
    private Label codeLabel;
    @FXML
    private DatePicker datePicker;
    @FXML
    private ChoiceBox<String> supplierList;
    @FXML
    private TableView<Item> itemTable;
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
    @FXML
    private Button deleteButton;
    @FXML
    private Button backButton;
    private final Supply displayedSupply;
    private final ObservableList<Item> itemList = FXCollections.observableArrayList();
    private final SupplyManagementController controller;

    public SupplyDisplayWindow(Supply displayedSupply) {

        this.displayedSupply = displayedSupply;
        controller = SupplyManagementController.getInstance();

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        try {
            controller.displaySupplyInfo();
        }
        catch (SQLException e) {
            System.out.println("Errore nella visualizzazione dei dati del fornitore: " + e.getMessage());
        }
        backButton.setOnAction(this::closeSupplyView);
        deleteButton.setOnAction(this::deleteSupply);

    }


    public void closeSupplyView(ActionEvent event) {
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
    }


    public void setCellValueFactory() {

        itemCodeColumn.setCellValueFactory(new PropertyValueFactory<>("Codice_articolo"));
        itemNameColumn.setCellValueFactory(new PropertyValueFactory<>("Nome"));
        itemQuantityColumn.setCellValueFactory(new PropertyValueFactory<>("Quantita"));
        itemPriceColumn.setCellValueFactory(new PropertyValueFactory<>("Prezzo"));
        itemDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("Descrizione"));

    }


    public void setRows(ObservableList<Item> itemRows) {

        this.itemList.setAll(itemRows);
        setCellValueFactory();
        itemTable.setItems(this.itemList);

    }


    public void deleteSupply(ActionEvent event) {
        controller.deleteRow(displayedSupply);
        closeSupplyView(event);
    }


    public Supply getDisplayedSupply() {
        return displayedSupply;
    }

    public ObservableList<Item> getItemList() {
        return itemList;
    }

    public Label getCodeLabel() {
        return codeLabel;
    }

    public DatePicker getDatePicker() {
        return datePicker;
    }

    public ChoiceBox<String> getSupplierList() {
        return supplierList;
    }

}
