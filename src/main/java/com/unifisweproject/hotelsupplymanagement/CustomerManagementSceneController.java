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

public class CustomerManagementSceneController implements Initializable {

    @FXML
    private TableView<Customer> customerTable;
    @FXML
    private TableColumn<Customer, Integer> IDColumn;
    @FXML
    private TableColumn<Customer, String> BusinessNameColumn;
    @FXML
    private TableColumn<Customer, String> PivaColumn;
    @FXML
    private TableColumn<Customer, String> AddressColumn;
    @FXML
    private TableColumn<Customer, String> CapColumn;
    @FXML
    private TableColumn<Customer, String> CivicNumberColumn;
    @FXML
    private TableColumn<Customer, String> NameColumn;
    @FXML
    private TableColumn<Customer, String> SurnameColumn;
    @FXML
    private TableColumn<Customer, String> FiscalCodeColumn;
    @FXML
    private TableColumn<Customer, Integer> DiscountColumn;
    @FXML
    private TableColumn<Customer, String> DateColumn;
    @FXML
    private Button modifyButton;
    @FXML
    private Button deleteButton;

    private CustomerManagement customerManagement;
    ObservableList<Customer> customerRows = FXCollections.observableArrayList();    // Lista di righe presenti nella tabella, si aggiorna nel caso dell'aggiunta di una riga

    @Override
    public void initialize(URL url, ResourceBundle rb) {            // Il metodo inizializza la tabella, inserendo tutte le righe presenti nel DataBase nella tabella Cliente
        Platform.runLater(this::createRows);

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

    }

    public void createRows()  {

        ResultSet resultSet = customerManagement.getRows();

        try {
            while (resultSet.next()) {
                Customer customer = new Customer(resultSet.getInt(1), resultSet.getInt(3),
                        resultSet.getString(4), resultSet.getString(5), resultSet.getString(2), resultSet.getString(6),
                        resultSet.getString(7), resultSet.getString(8), resultSet.getString(9), resultSet.getString(11),
                        resultSet.getString(10));
                customerManagement.getCustomerList().add(customer);
            }
        }

        catch (SQLException e) {
            System.err.println("Errore");
        }

        customerRows.addAll(customerManagement.getCustomerList());

        IDColumn.setCellValueFactory(new PropertyValueFactory<>("Codice_cliente"));
        BusinessNameColumn.setCellValueFactory(new PropertyValueFactory<>("Ragione_sociale"));
        PivaColumn.setCellValueFactory(new PropertyValueFactory<>("P_iva"));
        AddressColumn.setCellValueFactory(new PropertyValueFactory<>("Indirizzo"));
        CapColumn.setCellValueFactory(new PropertyValueFactory<>("Cap"));
        CivicNumberColumn.setCellValueFactory(new PropertyValueFactory<>("Civico"));
        NameColumn.setCellValueFactory(new PropertyValueFactory<>("Nome"));
        SurnameColumn.setCellValueFactory(new PropertyValueFactory<>("Cognome"));
        FiscalCodeColumn.setCellValueFactory(new PropertyValueFactory<>("Codice_fiscale"));
        DiscountColumn.setCellValueFactory(new PropertyValueFactory<>("Sconto"));
        DateColumn.setCellValueFactory(new PropertyValueFactory<>("Data_inserimento"));

        customerTable.setItems(customerRows);                       // Inserisce nella tabella tutte le righe dei Customer presenti nel DB

    }

    public void displayAddView() {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AddCustomerView.fxml"));
            Parent root = loader.load();

            AddCustomerViewController addCustomerController = loader.getController();
            addCustomerController.setCustomerManagementSceneController(this);

            Stage stage = new Stage();
            stage.setTitle("Aggiungi cliente");
            stage.setScene(new Scene(root, 580, 400));
            stage.show();
        }
        catch (IOException e) {
            System.err.println(e.getMessage());
        }

    }

    public void addRow(Customer newCustomer) {

        newCustomer.setCodice_cliente(customerManagement.getNextCustomerCode() + 1);
        customerManagement.getCustomerList().add(newCustomer);
        customerManagement.add(newCustomer);
        updateTable();

    }

    public void modifyRow(Customer toBeModified) {
        customerManagement.modify(toBeModified);
        updateTable();
    }

    public void updateTable() {

        Platform.runLater(() -> {                       // Pulisci e aggiorna la tabella
            customerTable.getItems().clear();
            customerRows.setAll(customerManagement.getCustomerList());
            customerTable.setItems(customerRows);
        });

    }

    public void setCustomerManagement(CustomerManagement customerManagement) {
        this.customerManagement = customerManagement;
    }

    public void displayCustomerView(ActionEvent event) throws IOException {

        SelectionModel<Customer> selectionModel = customerTable.getSelectionModel();
        Customer selectedCustomer = selectionModel.getSelectedItem();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("CustomerView.fxml"));
        Parent root = loader.load();

        CustomerViewController customerViewController = loader.getController();
        customerViewController.setDisplayedCustomer(selectedCustomer);
        customerViewController.setCustomerManagementSceneController(this);

        Stage stage = new Stage();
        stage.setTitle(selectedCustomer.getNome());
        stage.setScene(new Scene(root, 580, 400));
        stage.show();

    }

    public void deleteRow() {
        SelectionModel<Customer> selectionModel = customerTable.getSelectionModel();
        Customer selectedCustomer = selectionModel.getSelectedItem();
        customerManagement.getCustomerList().remove(selectedCustomer);                      /////////////////////////////////////////////////////////////////////
        customerManagement.delete(selectedCustomer.getCodice_cliente());           // TODO: Mettere avviso prima della cancellazione
        updateTable();
    }

}
