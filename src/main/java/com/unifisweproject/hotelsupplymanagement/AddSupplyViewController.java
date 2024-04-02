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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class AddSupplyViewController implements Initializable {

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
    private ChoiceBox<String> supplierCodeList;
    @FXML
    private DatePicker datePicker;
    private ResultSet resultSet;
    private ArrayList<Integer> suppliedAmount = new ArrayList<>();
    private ObservableList<Item> itemList = FXCollections.observableArrayList();
    private SuppliesManagementSceneController suppliesManagementSceneController;
    private ItemInSupply itemInSupply = new ItemInSupply();
    private MainMenuController mainMenuController;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        Platform.runLater(() -> datePicker.setValue(LocalDate.now()));
        getSupplierList();
        setCellValueFactory();

    }

    public void getSupplierList() {

        String getSupplierList = "SELECT Codice_Fornitore, Ragione_Sociale FROM Fornitore";
        try {
            PreparedStatement preparedStatement = HotelSupplyManagementMain.conn.prepareStatement(getSupplierList);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                supplierCodeList.getItems().add(resultSet.getString("Ragione_Sociale"));
            }
        }
        catch (SQLException e) {
            System.err.println("Errore durante il reperimento della lista dei fornitori: " + e.getMessage());
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ListOfItems.fxml"));
            Parent root = loader.load();
            ListOfItemsController listOfItemsController = loader.getController();
            listOfItemsController.setAddSupplyViewController(this);
            listOfItemsController.setMainMenuController(mainMenuController);
            Stage itemListStage = new Stage();
            itemListStage.initModality(Modality.APPLICATION_MODAL);
            itemListStage.setTitle("Seleziona l'articolo rifornito");
            itemListStage.setScene(new Scene(root));
            itemListStage.show();
        }
        catch (IOException e) {
            System.err.println("Errore durante l'apertura del file ListOfItems.fxml: " + e.getMessage());
        }

    }

    public void verifyEmptyFields() {

        int i = 0;
        while (i < 3) {
            switch (i) {
                case 0:
                    if (supplierCodeList.getValue() == null) {
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

    public void addRow (Item newItem) {

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
            while (resultSet.next()) {
                if (supplierCodeList.getValue().equals(resultSet.getString("Ragione_Sociale"))) {
                    itemInSupply.setCodice_Fornitore(Integer.parseInt(resultSet.getString("Codice_Fornitore")));
                }
            }
        }
        catch (SQLException e) {
            System.err.println("Errore durante l'estrapolazione del codice fornitore: " + e.getMessage());
        }
        itemInSupply.setData_Fornitura(datePicker.getValue().toString());
        suppliesManagementSceneController.addSupply(itemInSupply);
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
        suppliesManagementSceneController.updateTable();
        closeAddView(ignoredEvent);

    }

    public void setMainMenuController(MainMenuController mainMenuController) {
        this.mainMenuController = mainMenuController;
    }

    public void setSuppliesManagementSceneController(SuppliesManagementSceneController suppliesManagementSceneController) {
        this.suppliesManagementSceneController = suppliesManagementSceneController;
    }

    public ArrayList<Integer> getSuppliedAmount() {
        return suppliedAmount;
    }

    public ItemInSupply getItemInSupply() {
        return itemInSupply;
    }

}
