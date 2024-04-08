package com.unifisweproject.hotelsupplymanagement;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class SupplyAddWindowController implements Initializable {

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

    @FXML
    private ChoiceBox<String> supplierList;
    @FXML
    private DatePicker datePicker;
    private ResultSet resultSet;
    private ArrayList<Integer> suppliedAmount = new ArrayList<>();
    private ObservableList<Item> itemList = FXCollections.observableArrayList();
    private SuppliesManagementWindowController suppliesManagementWindowController;
    private ItemInSupply itemInSupply = new ItemInSupply();
    private MainMenuWindowController mainMenuWindowController;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        Platform.runLater(() -> datePicker.setValue(LocalDate.now()));
        getSupplierList();
        setCellValueFactory();

    }

    public void getSupplierList() {

        resultSet = getResultSet();
        try {
            while (resultSet.next()) {
                supplierList.getItems().add(resultSet.getString("Ragione_Sociale"));
            }
        }
        catch (SQLException e) {
            System.err.println("Errore durante l'estrapolazione dei dati dei fornitori: " + e.getMessage());
        }

    }

    public ResultSet getResultSet() {                   // Metodo per riottenere il ResultSet contenente i dati Codice_Fornitore e Ragione_Sociale dei fornitori dal DB

        try {
            Statement statement = HotelSupplyManagementMain.conn.createStatement();
            return statement.executeQuery("SELECT Codice_Fornitore, Ragione_Sociale FROM Fornitore");
        }
        catch (SQLException e) {
            System.err.println("Errore durante l'estrapolazione dei dati dei fornitori: " + e.getMessage());
            return null;
        }

    }

    public void setCellValueFactory() {

        itemCodeColumn.setCellValueFactory(new PropertyValueFactory<>("Codice_articolo"));
        itemNameColumn.setCellValueFactory(new PropertyValueFactory<>("Nome"));
        itemQuantityColumn.setCellValueFactory(new PropertyValueFactory<>("Quantita"));
        itemPriceColumn.setCellValueFactory(new PropertyValueFactory<>("Prezzo"));
        itemDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("Descrizione"));

    }

    public void closeAddView(ActionEvent event) {
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
    }

    public void displayAddItemView(ActionEvent ignoredEvent) {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ListOfItemsWindow.fxml"));
            Parent root = loader.load();
            ListOfItemsWindowController listOfItemsWindowController = loader.getController();
            listOfItemsWindowController.setAddSupplyViewController(this);
            listOfItemsWindowController.setMainMenuController(mainMenuWindowController);
            Stage itemListStage = new Stage();
            itemListStage.initModality(Modality.APPLICATION_MODAL);
            itemListStage.setTitle("Seleziona l'articolo rifornito");
            itemListStage.setResizable(false);
            itemListStage.getIcons().add(HotelSupplyManagementMain.icon);
            itemListStage.setScene(new Scene(root));
            itemListStage.show();
        }
        catch (IOException e) {
            System.err.println("Errore durante l'apertura del file ListOfItemsWindow.fxml: " + e.getMessage());
        }

    }

    public void verifyEmptyFields() {

        int i = 0;
        while (i < 3) {
            switch (i) {
                case 0:
                    if (supplierList.getValue() == null) {
                        throw new RuntimeException("Fornitore");
                    }
                case 1:
                    if (datePicker.getValue() == null) {
                        throw new RuntimeException("Data");
                    }
                case 2:
                    if (itemList.isEmpty()) {
                        throw new RuntimeException("Articoli");
                    }
            }
            i++;
        }

    }

    public void displayEmptyFieldAlert(String field) {

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Campo vuoto");
        alert.setHeaderText("Il campo " + field + " non può essere vuoto");
        alert.showAndWait();

    }

    public void addRow (Item newItem) {                 // TODO: Fai vedere il prezzo totale dell'articolo per la quantita inserita

        itemList.add(newItem);
        itemTableView.setItems(itemList);
        itemTableView.getColumns().setAll(itemCodeColumn, itemNameColumn, itemQuantityColumn, itemPriceColumn, itemDescriptionColumn);

    }

    public void createSupply(ActionEvent ignoredEvent) {

        try {
            verifyEmptyFields();
        }
        catch (RuntimeException e) {
            displayEmptyFieldAlert(e.getMessage());
            return;
        }

        try {
            resultSet = getResultSet();
            while (resultSet.next()) {
                if (supplierList.getValue().equals(resultSet.getString(2))) {
                    itemInSupply.setCodice_Fornitore(Integer.parseInt(resultSet.getString(1)));
                }
            }
        }
        catch (SQLException e) {
            System.err.println("Errore durante l'estrapolazione del codice fornitore: " + e.getMessage());
        }
        itemInSupply.setData_Fornitura(datePicker.getValue().toString());
        suppliesManagementWindowController.addSupply(itemInSupply);
        updateAmount();
        displayAddExectuedAlert(ignoredEvent);

    }


    public void updateAmount() {

        for(int i = 0; i < itemInSupply.getNumberOfItems(); i++){
            String modifyQuery = "UPDATE Articolo SET Quantita = ? WHERE Codice_Articolo = " + itemInSupply.getCodice_Articolo(i);       //Istruzioni per aggiornare la quantita dell'item del DB
            try {
                PreparedStatement statement = HotelSupplyManagementMain.conn.prepareStatement(modifyQuery);
                statement.setInt(1, suppliedAmount.get(i));
                statement.executeUpdate();
            }
            catch (SQLException e) {
                System.err.println("Errore di formattazione nella generazione della query di modifica: " + e.getMessage());
            }
        }
    }

    public void displayAddExectuedAlert(ActionEvent ignoredEvent) {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Aggiunta completata");
        alert.setHeaderText("Fornitura aggiunta con successo");
        alert.showAndWait();
        suppliesManagementWindowController.updateTable();
        closeAddView(ignoredEvent);

    }

    public void setMainMenuController(MainMenuWindowController mainMenuWindowController) {
        this.mainMenuWindowController = mainMenuWindowController;
    }

    public void setSuppliesManagementSceneController(SuppliesManagementWindowController suppliesManagementWindowController) {
        this.suppliesManagementWindowController = suppliesManagementWindowController;
    }

    public ArrayList<Integer> getSuppliedAmount() {
        return suppliedAmount;
    }

    public ItemInSupply getItemInSupply() {
        return itemInSupply;
    }

}
