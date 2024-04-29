package com.unifisweproject.hotelsupplymanagement.customer;

import com.unifisweproject.hotelsupplymanagement.DataManagementView;
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

public class CustomerDataManagementView implements Initializable, DataManagementView {

    @FXML
    private TableView<Customer> customerTable;
    @FXML
    private TableColumn<Customer, Integer> IDColumn;
    @FXML
    private TableColumn<Customer, String> businessNameColumn;
    @FXML
    private TableColumn<Customer, String> PIVAColumn;
    @FXML
    private TableColumn<Customer, String> addressColumn;
    @FXML
    private TableColumn<Customer, String> CAPColumn;
    @FXML
    private TableColumn<Customer, String> civicNumberColumn;
    @FXML
    private TableColumn<Customer, String> nameColumn;
    @FXML
    private TableColumn<Customer, String> surnameColumn;
    @FXML
    private TableColumn<Customer, String> fiscalCodeColumn;
    @FXML
    private TableColumn<Customer, Integer> discountColumn;
    @FXML
    private TableColumn<Customer, String> dateColumn;
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
    private MenuItem itemMenuItem;
    @FXML
    private MenuItem supplierMenuItem;
    @FXML
    private MenuItem supplyMenuItem;
    @FXML
    private MenuItem orderMenuItem;
    @FXML
    private MenuItem mainMenuItem;
    @FXML
    private AnchorPane tableAnchorPane;
    private long lastClickTime = 0;
    private final ContextMenu rightClickMenu = new ContextMenu();               // Content Menu e MenuItem per poter visualizzare menù tasto destro
    private final MenuItem viewCustomerMenu = new MenuItem("Visualizza");
    private final MenuItem viewDeleteCustomerMenu = new MenuItem("Elimina");
    ObservableList<Customer> customerRows = FXCollections.observableArrayList();    // Lista di righe presenti nella tabella, si aggiorna nel caso dell'aggiunta di una riga
    private final CustomerDataManagementController controller;

    public CustomerDataManagementView(){
        controller = CustomerDataManagementController.getInstance();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {            // Il metodo inizializza la tabella, inserendo tutte le righe presenti nel DataBase nella tabella Cliente
        initializeView();
    }

    @Override
    public void initializeView() {

        modifyButton.setOnAction(this::handleActionEvent);
        deleteButton.setOnAction(this::handleActionEvent);
        backButton.setOnAction(this::handleActionEvent);
        searchButton.setOnAction(this::handleActionEvent);
        addButton.setOnAction(this::handleActionEvent);
        customerTable.getSelectionModel().selectedItemProperty().addListener((observableValue, oldSelection, newSelection) -> {
            if(newSelection != null) {
                modifyButton.setDisable(false);             // Aggiunta del listener nella tabella per rilevare quale elemento viene selezionato
                deleteButton.setDisable(false);
            }
            else {
                modifyButton.setDisable(true);
                deleteButton.setDisable(true);
            }
        });
        rightClickMenu.getItems().addAll(viewCustomerMenu, viewDeleteCustomerMenu);
        viewCustomerMenu.setOnAction(this::handleActionEvent);
        viewCustomerMenu.setId("viewCustomerMenu");
        viewDeleteCustomerMenu.setOnAction(this::handleActionEvent);
        viewDeleteCustomerMenu.setId("viewDeleteCustomerMenu");
        customerTable.setOnMouseClicked(event -> {
            if (event.getButton().equals(MouseButton.PRIMARY)) {            // Controlla se il click è un doppio click e gestiscilo di conseguenza
                rightClickMenu.hide();
                if (event.getClickCount() == 2) {
                    long currentTime = System.currentTimeMillis();
                    if (currentTime - lastClickTime < 5000)
                    {
                        handleMouseEvent(customerTable.getSelectionModel().getSelectedItem());
                    }
                    lastClickTime = currentTime;
                }
            }
            else {
                SelectionModel<Customer> selectionModel = customerTable.getSelectionModel();        // verifico se è stato cliccato un elemento
                Customer selectedCustomer = selectionModel.getSelectedItem();
                if(selectedCustomer != null)
                    rightClickMenu.show(tableAnchorPane, event.getScreenX(), event.getScreenY());       // Mostra il menu contestuale alle coordinate del click
            }
        });
        customerTable.getSelectionModel().selectedItemProperty().addListener((observableValue, oldSelection, newSelection) -> {
            if(newSelection != null) {
                modifyButton.setDisable(false);
                deleteButton.setDisable(false);
            }
            else {
                modifyButton.setDisable(true);
                deleteButton.setDisable(true);
            }
        });
        itemMenuItem.setOnAction(this::handleActionEvent);
        supplierMenuItem.setOnAction(this::handleActionEvent);
        supplyMenuItem.setOnAction(this::handleActionEvent);
        orderMenuItem.setOnAction(this::handleActionEvent);
        mainMenuItem.setOnAction(this::handleActionEvent);

    }


    public void setRows(ObservableList<Customer> customerRows) {

        this.customerRows.setAll(customerRows);
        setCellValueFactory();
        customerTable.setItems(this.customerRows);

    }

    @Override
    public void setCellValueFactory() {

        IDColumn.setCellValueFactory(new PropertyValueFactory<>("Codice_cliente"));
        businessNameColumn.setCellValueFactory(new PropertyValueFactory<>("Ragione_sociale"));
        PIVAColumn.setCellValueFactory(new PropertyValueFactory<>("P_IVA"));
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("Indirizzo"));
        CAPColumn.setCellValueFactory(new PropertyValueFactory<>("CAP"));
        civicNumberColumn.setCellValueFactory(new PropertyValueFactory<>("Civico"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("Nome"));
        surnameColumn.setCellValueFactory(new PropertyValueFactory<>("Cognome"));
        fiscalCodeColumn.setCellValueFactory(new PropertyValueFactory<>("Codice_fiscale"));
        discountColumn.setCellValueFactory(new PropertyValueFactory<>("Sconto"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("Data_inserimento"));

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
    public void handleActionEvent(ActionEvent actionEvent) {
        controller.handleActionEvent(actionEvent);
    }

    @Override
    public void handleMouseEvent(Object selectedCustomer) {
        controller.handleMouseEvent((Customer) selectedCustomer);
    }

    @Override
    public void exitSearch() {

        refreshButtons();
        controller.setSearchView(false);
        controller.updateTable();

    }

    public TableView<Customer> getCustomerTable() {
        return customerTable;
    }

}
