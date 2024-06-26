package com.unifisweproject.hotelsupplymanagement.view.supplier;

import com.unifisweproject.hotelsupplymanagement.model.supplier.Supplier;
import com.unifisweproject.hotelsupplymanagement.controller.supplier.SupplierManagementController;
import com.unifisweproject.hotelsupplymanagement.view.DataManagementView;
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

public class SupplierManagementView implements Initializable, DataManagementView {

    @FXML
    private TableView<Supplier> supplierTable;
    @FXML
    private TableColumn<Supplier, Integer> IDColumn;
    @FXML
    private TableColumn<Supplier, String> businessNameColumn;
    @FXML
    private TableColumn<Supplier, String> PIVAColumn;
    @FXML
    private TableColumn<Supplier, String> addressColumn;
    @FXML
    private TableColumn<Supplier, String> civicNumberColumn;
    @FXML
    private TableColumn<Supplier, String> CAPColumn;
    @FXML
    private TableColumn<Supplier, String> dateColumn;
    @FXML
    private Button modifyButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Button backButton;
    @FXML
    private Button searchButton;
    @FXML
    private Button addButton;
    @FXML
    private AnchorPane tableAnchorPane;
    @FXML
    private MenuItem itemMenuItem;
    @FXML
    private MenuItem orderMenuItem;
    @FXML
    private MenuItem customerMenuItem;
    @FXML
    private MenuItem supplyMenuItem;
    @FXML
    private MenuItem mainMenuItem;
    private final ContextMenu rightClickMenu = new ContextMenu();               // Content Menu e MenuItem per poter visualizzare menù tasto destro
    private final MenuItem viewSupplierMenu = new MenuItem("Visualizza");
    private final MenuItem viewDeleteSupplierMenu = new MenuItem("Elimina");
    private final ObservableList<Supplier> supplierRows = FXCollections.observableArrayList();
    private long lastClickTime = 0;
    private final SupplierManagementController controller;

    public SupplierManagementView() {
        controller = SupplierManagementController.getInstance();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeView();
    }

    @Override
    public void initializeView() {

        itemMenuItem.setOnAction(this::handleActionEvent);
        orderMenuItem.setOnAction(this::handleActionEvent);
        supplyMenuItem.setOnAction(this::handleActionEvent);
        customerMenuItem.setOnAction(this::handleActionEvent);
        mainMenuItem.setOnAction(this::handleActionEvent);
        addButton.setOnAction(this::handleActionEvent);
        searchButton.setOnAction(this::handleActionEvent);
        modifyButton.setOnAction(this::handleActionEvent);
        deleteButton.setOnAction(this::handleActionEvent);
        backButton.setOnAction(this::handleActionEvent);
        supplierTable.getSelectionModel().selectedItemProperty().addListener((observableValue, oldSelection, newSelection) -> {
            if(newSelection != null) {
                modifyButton.setDisable(false);             // Aggiunta del listener nella tabella per rilevare quale elemento viene selezionato
                deleteButton.setDisable(false);
            }
            else {
                modifyButton.setDisable(true);
                deleteButton.setDisable(true);
            }
        });
        rightClickMenu.getItems().addAll(viewSupplierMenu, viewDeleteSupplierMenu);
        viewSupplierMenu.setOnAction(this::handleActionEvent);
        viewSupplierMenu.setId("viewSupplierMenu");
        viewDeleteSupplierMenu.setOnAction(this::handleActionEvent);
        viewDeleteSupplierMenu.setId("viewDeleteSupplierMenu");
        supplierTable.setOnMouseClicked(event -> {
            if (event.getButton().equals(MouseButton.PRIMARY)) {            // Controlla se il click è un doppio click e gestiscilo di conseguenza
                rightClickMenu.hide();
                if (event.getClickCount() == 2) {
                    long currentTime = System.currentTimeMillis();
                    if (currentTime - lastClickTime < 5000)
                    {
                        handleMouseEvent(supplierTable.getSelectionModel().getSelectedItem());
                    }
                    lastClickTime = currentTime;
                }
            }
            else {
                SelectionModel<Supplier> selectionModel = supplierTable.getSelectionModel();        // verifico se è stato cliccato un elemento
                Supplier selectedSupplier = selectionModel.getSelectedItem();
                if(selectedSupplier != null)
                    rightClickMenu.show(tableAnchorPane, event.getScreenX(), event.getScreenY());       // Mostra il menu contestuale alle coordinate del click
            }
        });
        supplierTable.getSelectionModel().selectedItemProperty().addListener((observableValue, oldSelection, newSelection) -> {
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

    public void setRows(ObservableList<Supplier> supplierRows) {

        this.supplierRows.setAll(supplierRows);
        setCellValueFactory();
        supplierTable.setItems(this.supplierRows);

    }

    @Override
    public void refreshButtons() {

        addButton.setDisable(false);                // Riattivo bottone di aggiunta
        addButton.setVisible(true);
        searchButton.setDisable(false);             // Riattivo bottone di ricerca
        searchButton.setVisible(true);
        backButton.setDisable(true);                // Disattivo bottone "indietro" quando ho terminato una precedente ricerca
        backButton.setVisible(false);

    }

    @Override
    public void enableBackButton() {

        backButton.setDisable(false);
        backButton.setVisible(true);
        addButton.setDisable(true);
        addButton.setVisible(false);
        searchButton.setDisable(true);
        searchButton.setVisible(false);

    }

    @Override
    public void setCellValueFactory() {

        IDColumn.setCellValueFactory(new PropertyValueFactory<>("Codice_fornitore"));
        businessNameColumn.setCellValueFactory(new PropertyValueFactory<>("Ragione_sociale"));
        PIVAColumn.setCellValueFactory(new PropertyValueFactory<>("P_IVA"));
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("Indirizzo"));
        civicNumberColumn.setCellValueFactory(new PropertyValueFactory<>("Civico"));
        CAPColumn.setCellValueFactory(new PropertyValueFactory<>("CAP"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("Data_inserimento"));

    }

    @Override
    public void handleActionEvent(ActionEvent actionEvent) {
        controller.handleActionEvent(actionEvent);
    }

    @Override
    public void handleMouseEvent(Object selectedSupplier) {
        controller.handleMouseEvent((Supplier) selectedSupplier);
    }

    @Override
    public void exitSearch() {

        refreshButtons();
        controller.setSearchView(false);
        controller.updateTable();

    }

    public TableView<Supplier> getSupplierTable() {
        return supplierTable;
    }

}
