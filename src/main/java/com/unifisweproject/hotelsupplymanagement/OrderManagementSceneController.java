package com.unifisweproject.hotelsupplymanagement;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class OrderManagementSceneController implements Initializable{
    @FXML
    private TableView<Order> orderTable;
    @FXML
    private TableColumn<Order, Integer> IDColumn;
    @FXML
    private TableColumn<Order, Boolean> BFColumn; //TODO: gestire la colonna booleana
    @FXML
    private TableColumn<Order, String> TypeOfPaymentColumn;
    @FXML
    private TableColumn<Customer, String> DateColumn;
    @FXML
    private TableColumn<Order, Integer> CustomerIDColumn;
    @FXML
    private Button modifyButton;
    @FXML
    private Button deleteButton;

    private OrderManagement orderManagement;
    ObservableList<Order> orderRows = FXCollections.observableArrayList();

    public void setOrderManagement(OrderManagement orderManagement) { this.orderManagement = orderManagement;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {            // Il metodo inizializza la tabella, inserendo tutte le righe presenti nel DataBase nella tabella Cliente
        Platform.runLater(this::createRows);

        orderTable.getSelectionModel().selectedItemProperty().addListener((observableValue, oldSelection, newSelection) -> {
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

        ResultSet resultSet = orderManagement.getRows(true, null);

        try {
            while (resultSet.next()) {
                Order order = new Order(resultSet.getInt(1), resultSet.getInt(5),
                        resultSet.getBoolean(2), resultSet.getString(3),
                        resultSet.getString(4));
                orderManagement.getOrderList().add(order);
            }
        }

        catch (SQLException e) {
            System.err.println("Errore");
        }

        orderRows.addAll(orderManagement.getOrderList());

        IDColumn.setCellValueFactory(new PropertyValueFactory<>("Codice_ordine"));
        BFColumn.setCellValueFactory(new PropertyValueFactory<>("B/f"));
        TypeOfPaymentColumn.setCellValueFactory(new PropertyValueFactory<>("Tipo_pagamento"));
        DateColumn.setCellValueFactory(new PropertyValueFactory<>("Data_ordine"));
        CustomerIDColumn.setCellValueFactory(new PropertyValueFactory<>("Codice_cliente"));

        orderTable.setItems(orderRows);                       // Inserisce nella tabella tutte le righe dei Customer presenti nel DB

    }

    public void displayAddView() {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AddOrderView.fxml"));
            Parent root = loader.load();

            AddOrderViewController addOrderController = loader.getController();
            addOrderController.setOrderManagementSceneController(this);

            Stage stage = new Stage();
            stage.setTitle("Aggiungi ordine");
            stage.setScene(new Scene(root, 580, 400));
            stage.show();
        }
        catch (IOException e) {
            System.err.println(e.getMessage());
        }

    }

    public void addRow(Order newOrder) {

        newOrder.setCodice_ordine(orderManagement.getNextOrderCode() + 1);
        orderManagement.getOrderList().add(newOrder);
        orderManagement.add(newOrder);
        updateTable();

    }

    public void modifyRow(Order toBeModified) {
        orderManagement.modify(toBeModified);
        updateTable();
    }

    public void updateTable() {

        Platform.runLater(() -> {                       // Pulisci e aggiorna la tabella
            orderTable.getItems().clear();
            orderRows.setAll(orderManagement.getOrderList());
            orderTable.setItems(orderRows);
        });

    }
}
