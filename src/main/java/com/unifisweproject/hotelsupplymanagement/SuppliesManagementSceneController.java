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
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

public class SuppliesManagementSceneController implements Initializable {

    @FXML
    private TableView<Supply> suppliesTable;
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
    private AnchorPane tableAnchorPane;
    private final boolean searchView = false;
    private final ContextMenu rightClickMenu = new ContextMenu();
    private final MenuItem viewSupplyMenu = new MenuItem("Visualizza dettagli");
    private final MenuItem deleteSupplyMenu = new MenuItem("Modifica fornitura");
    private MainMenuController mainMenuController;
    private SuppliesManagement suppliesManagement;
    private ArrayList<Supply> results = new ArrayList<>();
    private final ObservableList<Supply> supplyRows = FXCollections.observableArrayList();
    private final ObservableList<Supply> searchResultRows = FXCollections.observableArrayList();
    private long lastClickTime = 0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        Platform.runLater(() -> {
            createRows();
            setTableSelectionModel();
            setCellValueFactory();
            setRightClickMenu();
            setDoubleClickAction();
            suppliesTable.setItems(supplyRows);
        });

    }

    public void setTableSelectionModel() {                          // Imposta la selezione della tabella per individuare la riga selezionata
        suppliesTable.getSelectionModel().selectedItemProperty().addListener((observableValue, oldSelection, newSelection) ->
                deleteButton.setDisable(newSelection == null));
    }

    public void setRightClickMenu() {                       // Imposta il funzionamento del menu contestuale con il tasto destro del mouse

        rightClickMenu.getItems().addAll(viewSupplyMenu, deleteSupplyMenu);
        viewSupplyMenu.setOnAction(event -> displayRowView(null));
        deleteSupplyMenu.setOnAction(event -> deleteRow());

    }

    public void setCellValueFactory() {                         // Imposta il tipo di parametri nelle colonne della tabella supplyTable

        supplyColumn.setCellValueFactory(new PropertyValueFactory<>("Codice_fornitura"));
        supplierColumn.setCellValueFactory(new PropertyValueFactory<>("Codice_fornitore"));
        itemColumn.setCellValueFactory(new PropertyValueFactory<>("Codice_articolo"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("Data_fornitura"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("Prezzo"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("Quantita"));

    }

    public void setDoubleClickAction() {

        suppliesTable.setOnMouseClicked(event -> {
            if (event.getButton().equals(MouseButton.PRIMARY)) {            // Controlla se il click è un doppio click e gestiscilo di conseguenza
                rightClickMenu.hide();
                if (event.getClickCount() == 2) {
                    long currentTime = System.currentTimeMillis();
                    if (currentTime - lastClickTime < 5000)
                        displayRowView(null);
                    lastClickTime = currentTime;
                }
            }
            else {
                SelectionModel<Supply> selectionModel = suppliesTable.getSelectionModel();        // verifico se è stato cliccato un elemento
                Supply selectedSupply = selectionModel.getSelectedItem();
                if(selectedSupply != null)
                    rightClickMenu.show(tableAnchorPane, event.getScreenX(), event.getScreenY());       // Mostra il menu contestuale alle coordinate del click
            }
        });

    }

    public void createRows() {

        if (!mainMenuController.getIsNotFirstTimeLoad().get(4)) {
            ResultSet resultSet = suppliesManagement.getRows(true, null);
            try {
                while(resultSet.next()) {
                    Supply supply = new Supply(resultSet.getInt("codice_fornitura"), resultSet.getInt("codice_fornitore"), resultSet.getInt("codice_articolo"), resultSet.getString("data_fornitura"), resultSet.getInt("quantita"), resultSet.getDouble("prezzo"));
                    suppliesManagement.getSuppliesList().add(supply);
                }
                mainMenuController.getIsNotFirstTimeLoad().set(4, true);
            }
            catch (SQLException e) {
                System.err.println("Errore durante la creazione delle righe di SuppliesViewController: " + e.getMessage());
            }
        }
        supplyRows.addAll(suppliesManagement.getSuppliesList());
        setCellValueFactory();
        suppliesTable.setItems(supplyRows);

    }

    public void addSupply(ItemInSupply newSupplies) {

        for(int i = 0; i < newSupplies.getNumberOfItems(); i++) {
            Supply newSupply = new Supply(newSupplies.getCodice_Fornitore(), newSupplies.getCodice_Articolo(i), newSupplies.getData_Fornitura(), newSupplies.getQuantita(i), newSupplies.getPrezzo(i));
            newSupply.setCodice_fornitura(suppliesManagement.getNextSupplyCode() + 1);
            suppliesManagement.getSuppliesList().add(newSupply);
            suppliesManagement.add(newSupply);
        }
        updateTable();

    }

    public boolean createConfirmDeleteAlert() {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Rimozione fornitura");
        alert.setContentText("Sicuro di procedere con l'eliminazione?");
        ButtonType buttonTypeYes = new ButtonType("Sì");
        ButtonType buttonTypeNo = new ButtonType("No");
        alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == buttonTypeYes;

    }

    public void displayRowView(ActionEvent ignoredEvent) {                  // Visualizza i dettagli della fornitura selezionata senza però poterla modificare
        // TODO
    }

    public void displayAddView(ActionEvent ignoredEvent) {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AddSupplyView.fxml"));
            Parent root = loader.load();
            AddSupplyViewController addSupplyViewController= loader.getController();
            addSupplyViewController.setSuppliesManagementSceneController(this);
            addSupplyViewController.setMainMenuController(mainMenuController);
            Stage addStage = new Stage();
            addStage.initModality(Modality.APPLICATION_MODAL);
            addStage.setTitle("Aggiungi fornitura");
            addStage.setScene(new Scene(root));
            addStage.show();
        }
        catch (IOException e) {
            System.err.println(e.getMessage());
        }

    }

    public void deleteRow() {

        if (createConfirmDeleteAlert()) {
            SelectionModel<Supply> selectionModel = suppliesTable.getSelectionModel();
            Supply selectedSupply = selectionModel.getSelectedItem();
            suppliesManagement.getSuppliesList().remove(selectedSupply);
            suppliesManagement.delete(selectedSupply.getCodice_fornitura());
            try {
                PreparedStatement statement = HotelSupplyManagementMain.conn.prepareStatement("DELETE FROM Fornitura WHERE Codice_Fornitura = " + selectedSupply.getCodice_fornitura());
                statement.executeUpdate();
            } catch (SQLException e) {
                System.err.println("Errore durante l'eliminazione della riga Fornitura: " + e.getMessage());
            }
            if (searchView)
                results.remove(selectedSupply);                   // Se sto visualizzando una ricerca, effettuo gli aggiornamenti anche su questa view
            updateTable();
        }

    }

    public void updateTable() {

        Platform.runLater(() -> {                       // Pulisci e aggiorna la tabella
            if(searchView) {
                suppliesTable.getItems().clear();
                searchResultRows.clear();
                searchResultRows.setAll(results);
                suppliesTable.setItems(searchResultRows);
            }
            else {
                suppliesTable.getItems().clear();
                supplyRows.clear();
                supplyRows.setAll(suppliesManagement.getSuppliesList());
                suppliesTable.setItems(supplyRows);
                addButton.setDisable(false);                // Riattivo bottone di aggiunta
                addButton.setVisible(true);
                backButton.setDisable(true);                // Disattivo bottone "indietro" quando ho terminato una precedente ricerca
                backButton.setVisible(false);
            }
        });

    }

    public void setMainMenuController(MainMenuController mainMenuController) {
        this.mainMenuController = mainMenuController;
    }

    public void setSuppliesManagement(SuppliesManagement suppliesManagement) {
        this.suppliesManagement = suppliesManagement;
    }

    public void openDifferentManagement(ActionEvent event) {
        Stage stage = (Stage) ((MenuItem) event.getSource()).getParentPopup().getOwnerWindow();
        String menuName = ((MenuItem) event.getSource()).getText();
        mainMenuController.getStageFromMenuBar(event, stage, menuName);
    }


}
