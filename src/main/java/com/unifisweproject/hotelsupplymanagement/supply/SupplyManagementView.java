package com.unifisweproject.hotelsupplymanagement.supply;

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

public class SupplyManagementView implements Initializable {

    @FXML
    private TableView<Supply> supplyTable;
    @FXML
    private TableColumn<Supply, Integer> supplyColumn;
    @FXML
    private TableColumn<Supply, String> supplierColumn;
    @FXML
    private TableColumn<Supply, String> itemColumn;
    @FXML
    private TableColumn<Supply, String> dateColumn;
    @FXML
    private TableColumn<Supply, Double> priceColumn;
    @FXML
    private TableColumn<Supply, Integer> amountColumn;
    @FXML
    private Button deleteButton;
    @FXML
    private Button addButton;
    @FXML
    private Button backButton;
    @FXML
    private Button searchButton;
    @FXML
    private AnchorPane tableAnchorPane;
    private final ContextMenu rightClickMenu = new ContextMenu();
    private final ObservableList<Supply> supplyRows = FXCollections.observableArrayList();
    private final MenuItem viewSupplyMenu = new MenuItem("Visualizza dettagli");
    private final MenuItem viewDeleteSupplyMenu = new MenuItem("Elimina");
    private long lastClickTime = 0;
    private final SupplyManagementController controller;

    public SupplyManagementView() {
        controller = SupplyManagementController.getInstance();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        addButton.setOnAction(this::handleActionEvent);
        deleteButton.setOnAction(this::handleActionEvent);
        backButton.setOnAction(this::handleActionEvent);
        searchButton.setOnAction(this::handleActionEvent);
        supplyTable.getSelectionModel().selectedItemProperty().addListener((observableValue, oldSelection, newSelection) -> deleteButton.setDisable(newSelection == null));
        rightClickMenu.getItems().addAll(viewSupplyMenu, viewDeleteSupplyMenu);
        viewSupplyMenu.setOnAction(this::handleActionEvent);
        viewSupplyMenu.setId("viewSupplyMenu");
        viewDeleteSupplyMenu.setOnAction(this::handleActionEvent);
        viewDeleteSupplyMenu.setId("viewDeleteSupplyMenu");
        supplyTable.setOnMouseClicked(event -> {
            if (event.getButton().equals(MouseButton.PRIMARY)) {            // Controlla se il click è un doppio click e gestiscilo di conseguenza
                rightClickMenu.hide();
                if (event.getClickCount() == 2) {
                    long currentTime = System.currentTimeMillis();
                    if (currentTime - lastClickTime < 5000)
                    {
                        handleMouseEvent(supplyTable.getSelectionModel().getSelectedItem());
                    }
                    lastClickTime = currentTime;
                }
            }
            else {
                SelectionModel<Supply> selectionModel = supplyTable.getSelectionModel();        // verifico se è stato cliccato un elemento
                Supply selectedSupply = selectionModel.getSelectedItem();
                if(selectedSupply != null)
                    rightClickMenu.show(tableAnchorPane, event.getScreenX(), event.getScreenY()); // Mostra il menu contestuale alle coordinate del click
            }
        });
    }


    public void setCellValueFactory() {

        supplyColumn.setCellValueFactory(new PropertyValueFactory<>("Codice_fornitura"));
        supplierColumn.setCellValueFactory(new PropertyValueFactory<>("Codice_fornitore"));
        itemColumn.setCellValueFactory(new PropertyValueFactory<>("Codice_articolo"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("Prezzo"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("Data_fornitura"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("Quantita"));

    }


    public void setRows(ObservableList<Supply> supplyRows) {

        this.supplyRows.setAll(supplyRows);
        setCellValueFactory();
        supplyTable.setItems(this.supplyRows);

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

    public void handleMouseEvent(Supply selectedSupply) {
        controller.handleMouseEvent(selectedSupply);
    }

    public TableView<Supply> getSupplyTable() {
        return supplyTable;
    }



}
