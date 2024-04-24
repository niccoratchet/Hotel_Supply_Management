package com.unifisweproject.hotelsupplymanagement.order;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ResourceBundle;

public class OrderManagementView implements Initializable {

    @FXML
    private TableView<Order> orderTable;
    @FXML
    private TableColumn<Order, Integer> IDColumn;
    @FXML
    private TableColumn<Order, String> TypeOfPaymentColumn;
    @FXML
    private TableColumn<Order, String> DateColumn;
    @FXML
    private TableColumn<Order, Integer> CustomerIDColumn;
    @FXML
    private Button deleteButton;
    @FXML
    private Button addButton;
    @FXML
    private Button backButton;
    @FXML
    private Button searchButton;
    @FXML
    private MenuItem itemMenuItem;
    @FXML
    private MenuItem supplierMenuItem;
    @FXML
    private MenuItem customerMenuItem;
    @FXML
    private MenuItem supplyMenuItem;
    @FXML
    private MenuItem mainMenuItem;
    @FXML
    private AnchorPane tableAnchorPane;
    private final ObservableList<Order> orderRows = FXCollections.observableArrayList();
    private final ContextMenu rightClickMenu = new ContextMenu();               // Content Menu e MenuItem per poter visualizzare menù tasto destro
    private final MenuItem viewOrderMenu = new MenuItem("Visualizza");
    private final MenuItem viewDeleteOrderMenu = new MenuItem("Elimina");
    private long lastClickTime = 0;
    private final OrderManagementController controller;

    public OrderManagementView() {
        controller = OrderManagementController.getInstance();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        itemMenuItem.setOnAction(this::handleActionEvent);
        supplierMenuItem.setOnAction(this::handleActionEvent);
        customerMenuItem.setOnAction(this::handleActionEvent);
        supplyMenuItem.setOnAction(this::handleActionEvent);
        mainMenuItem.setOnAction(this::handleActionEvent);
        addButton.setOnAction(this::handleActionEvent);
        deleteButton.setOnAction(this::handleActionEvent);
        backButton.setOnAction(this::handleActionEvent);
        searchButton.setOnAction(this::handleActionEvent);
        orderTable.getSelectionModel().selectedItemProperty().addListener((observableValue, oldSelection, newSelection) -> deleteButton.setDisable(newSelection == null));
        rightClickMenu.getItems().addAll(viewOrderMenu, viewDeleteOrderMenu);
        viewOrderMenu.setOnAction(this::handleActionEvent);
        viewOrderMenu.setId("viewOrderMenu");
        viewDeleteOrderMenu.setOnAction(this::handleActionEvent);
        viewDeleteOrderMenu.setId("viewDeleteOrderMenu");
        orderTable.setOnMouseClicked(event -> {
            if (event.getButton().equals(MouseButton.PRIMARY)) {            // Controlla se il click è un doppio click e gestiscilo di conseguenza
                rightClickMenu.hide();
                if (event.getClickCount() == 2) {
                    long currentTime = System.currentTimeMillis();
                    if (currentTime - lastClickTime < 5000)
                    {
                        handleMouseEvent(orderTable.getSelectionModel().getSelectedItem());
                    }
                    lastClickTime = currentTime;
                }
            }
            else {
                SelectionModel<Order> selectionModel = orderTable.getSelectionModel();        // verifico se è stato cliccato un elemento
                Order selectedOrder = selectionModel.getSelectedItem();
                if(selectedOrder != null)
                    rightClickMenu.show(tableAnchorPane, event.getScreenX(), event.getScreenY()); // Mostra il menu contestuale alle coordinate del click
            }
        });
    }

    public void setCellValueFactory() {

        IDColumn.setCellValueFactory(new PropertyValueFactory<>("Codice_ordine"));
        TypeOfPaymentColumn.setCellValueFactory(new PropertyValueFactory<>("Tipo_pagamento"));
        DateColumn.setCellValueFactory(new PropertyValueFactory<>("Data_ordine"));
        CustomerIDColumn.setCellValueFactory(new PropertyValueFactory<>("Codice_cliente"));

    }

    public void setRows(ObservableList<Order> orderRows) {

        this.orderRows.setAll(orderRows);
        setCellValueFactory();
        orderTable.setItems(this.orderRows);

    }

    public void refreshButtons() {

        addButton.setDisable(false);                // Riattivo bottone di aggiunta
        addButton.setVisible(true);
        searchButton.setDisable(false);             // Riattivo bottone di ricerca
        searchButton.setVisible(true);
        backButton.setDisable(true);                // Disattivo bottone "indietro" quando ho terminato una precedente ricerca
        backButton.setVisible(false);

    }

    public void enableBackButton() {

        backButton.setDisable(false);
        backButton.setVisible(true);
        addButton.setDisable(true);
        addButton.setVisible(false);
        searchButton.setDisable(true);
        searchButton.setVisible(false);

    }

    public void exitSearch() {

        refreshButtons();
        controller.setSearchView(false);
        controller.updateTable();

    }

    public void handleActionEvent(ActionEvent actionEvent) {
        controller.handleActionEvent(actionEvent);
    }

    public void handleMouseEvent(Order selectedOrder) {
        controller.handleMouseEvent(selectedOrder);
    }

    public TableView<Order> getOrderTable() {
        return orderTable;
    }

}
