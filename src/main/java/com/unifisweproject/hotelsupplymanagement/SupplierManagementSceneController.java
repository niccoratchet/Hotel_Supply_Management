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

public class SupplierManagementSceneController implements Initializable {

    @FXML
    private TableView<Supplier> supplierTable;
    @FXML
    private TableColumn<Supplier, Integer> IDColumn;
    @FXML
    private TableColumn<Supplier, String> BusinessNameColumn;
    @FXML
    private TableColumn<Supplier, String> PivaColumn;
    @FXML
    private TableColumn<Supplier, String> AddressColumn;
    @FXML
    private TableColumn<Supplier, String> CivicColumn;
    @FXML
    private TableColumn<Supplier, String> CapColumn;
    @FXML
    private TableColumn<Supplier, String> DateColumn;
    @FXML
    private Button modifyButton;
    @FXML
    private Button deleteButton;

    private SupplierManagement supplierManagement;
    ObservableList<Supplier> supplierRows = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Platform.runLater(()-> {
            createRows();
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
        });

    }

    public void createRows()  {

        ResultSet resultSet = supplierManagement.getRows(true, null);

        try {
            while (resultSet.next()) {
                Supplier supplier = new Supplier(resultSet.getInt(1), resultSet.getString(7),
                        resultSet.getString(3), resultSet.getString(2), resultSet.getString(4),
                        resultSet.getString(5),resultSet.getString(6));
                supplierManagement.getSupplierList().add(supplier);
            }
        }

        catch (SQLException e) {
            System.err.println("Errore");
        }

        supplierRows.addAll(supplierManagement.getSupplierList());

        IDColumn.setCellValueFactory(new PropertyValueFactory<>("Codice_fornitore"));
        BusinessNameColumn.setCellValueFactory(new PropertyValueFactory<>("Ragione_sociale"));
        PivaColumn.setCellValueFactory(new PropertyValueFactory<>("P_iva"));
        AddressColumn.setCellValueFactory(new PropertyValueFactory<>("Indirizzo"));
        CivicColumn.setCellValueFactory(new PropertyValueFactory<>("Civico"));
        CapColumn.setCellValueFactory(new PropertyValueFactory<>("Cap"));
        DateColumn.setCellValueFactory(new PropertyValueFactory<>("Data_inserimento"));

        supplierTable.setItems(supplierRows);                       // Inserisce nella tabella tutte le righe degli Item presenti nel DB

    }

    public void displayAddView() {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AddSupplierView.fxml"));
            Parent root = loader.load();

            AddSupplierViewController addSupplierController = loader.getController();
            addSupplierController.setSupplierManagementSceneController(this);

            Stage stage = new Stage();
            stage.setTitle("Aggiungi fornitore");
            stage.setScene(new Scene(root, 580, 400));
            stage.show();
        }
        catch (IOException e) {
            System.err.println(e.getMessage());
        }

    }

    public void addRow(Supplier newSupplier) {

        newSupplier.setCodice_fornitore(supplierManagement.getNextSupplierCode() + 1);
        supplierManagement.getSupplierList().add(newSupplier);
        supplierManagement.add(newSupplier);
        updateTable();

    }

    public void modifyRow(Supplier toBeModified) {
        supplierManagement.modify(toBeModified);
        updateTable();
    }

    public void updateTable() {

        Platform.runLater(() -> {                       // Pulisci e aggiorna la tabella
            supplierTable.getItems().clear();
            supplierRows.setAll(supplierManagement.getSupplierList());
            supplierTable.setItems(supplierRows);
        });

    }

    public void setSupplierManagement(SupplierManagement supplierManagement) {
        this.supplierManagement = supplierManagement;
    }

    public void displaySupplierView(ActionEvent event) throws IOException {

        SelectionModel<Supplier> selectionModel = supplierTable.getSelectionModel();
        Supplier selectedSupplier = selectionModel.getSelectedItem();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("SupplierView.fxml"));
        Parent root = loader.load();

        SupplierViewController supplierViewController = loader.getController();
        supplierViewController.setDisplayedSupplier(selectedSupplier);
        supplierViewController.setSupplierManagementSceneController(this);

        Stage stage = new Stage();
        stage.setTitle(selectedSupplier.getRagione_sociale());
        stage.setScene(new Scene(root, 580, 400));
        stage.show();

    }

    public void deleteRow() {
        SelectionModel<Supplier> selectionModel = supplierTable.getSelectionModel();
        Supplier selectedSupplier = selectionModel.getSelectedItem();
        supplierManagement.getSupplierList().remove(selectedSupplier);
        supplierManagement.delete(selectedSupplier.getCodice_fornitore());           // TODO: Mettere avviso prima della cancellazione
        updateTable();
    }

}
