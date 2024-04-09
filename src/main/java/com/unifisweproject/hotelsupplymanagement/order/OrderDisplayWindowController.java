package com.unifisweproject.hotelsupplymanagement.order;

import com.unifisweproject.hotelsupplymanagement.main.HotelSupplyManagementMain;
import com.unifisweproject.hotelsupplymanagement.item.Item;
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

public class OrderDisplayWindowController implements Initializable {

    @FXML
    private TableView<Item> itemTable;
    @FXML
    private TableColumn<Item, Integer> codeColumn;
    @FXML
    private TableColumn<Item, String> nameColumn;
    @FXML
    private TableColumn<Item, Integer> amountColumn;
    @FXML
    private TableColumn<Item, String> descriptionColumn;
    @FXML
    private TableColumn<Item, Double> priceColumn;
    @FXML
    private ChoiceBox<String> BFField;
    @FXML
    private ChoiceBox<String> typeOfPaymentField;
    @FXML
    private DatePicker datePicker;
    @FXML
    private Label codeLabel;
    private Order displayedOrder;
    private OrderManagementWindowController orderManagementWindowController;
    private final ObservableList<Item> itemList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        Platform.runLater(this::addOrderInfo);
        BFField.getItems().addAll("Bolla", "Fattura");
        typeOfPaymentField.getItems().addAll("Ricevuta bancaria", "Bonifico bancario", "Rimessa diretta");

    }

    public void addOrderInfo() {

        String bf;
        codeLabel.setText("Dati ordine n°" + displayedOrder.getCodice_ordine());
        typeOfPaymentField.setValue(displayedOrder.getTipo_pagamento());
        datePicker.setValue(LocalDate.parse(displayedOrder.getData_ordine()));
        if(displayedOrder.isBolla()) {
            bf = "Bolla";
        }
        else {
            bf = "Fattura";
        }
        BFField.setValue(bf);

        getItemsInOrder();

    }

    public void setDisplayedOrder(Order displayedOrder) {
        this.displayedOrder = displayedOrder;
    }

    public void closeOrderView(ActionEvent event) {
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
    }

    public void setOrderManagementSceneController(OrderManagementWindowController orderManagementWindowController) {
        this.orderManagementWindowController = orderManagementWindowController;
    }

    public void getItemsInOrder() {

        String getCodeQuery = "SELECT A.Codice_Articolo, A.Nome, A.Prezzo, AIO.Quantita, A.Descrizione, A.Data_Inserimento " +
                "FROM ArticoloInOrdine AIO " +
                "RIGHT JOIN Articolo A ON AIO.Codice_Articolo = A.Codice_Articolo " +
                "WHERE AIO.Codice_Ordine = " + displayedOrder.getCodice_ordine();
        try {
            Statement statement = HotelSupplyManagementMain.conn.createStatement();
            ResultSet resultSet = statement.executeQuery(getCodeQuery);
            while (resultSet.next()) {
                int quantita = resultSet.getInt(4);
                double prezzoTot = resultSet.getDouble(3) * quantita;
                Item item = new Item(resultSet.getInt(1), quantita,
                        prezzoTot, resultSet.getString(2),
                        resultSet.getString(5), resultSet.getString(6));
                codeColumn.setCellValueFactory(new PropertyValueFactory<>("Codice_articolo"));
                nameColumn.setCellValueFactory(new PropertyValueFactory<>("Nome"));
                amountColumn.setCellValueFactory(new PropertyValueFactory<>("Quantita"));
                descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("Descrizione"));
                priceColumn.setCellValueFactory(new PropertyValueFactory<>("Prezzo"));
                itemList.add(item);                       // Inserisce nella tabella tutte le righe degli Item presenti nel DB
            }
            itemTable.setItems(itemList);
        }
        catch(SQLException e) {
            System.err.println("Errore nell'esecuzione della query");
        }

    }
    public void deleteOrder(ActionEvent event) {

        orderManagementWindowController.deleteRow();
        closeOrderView(event);

    }

}
