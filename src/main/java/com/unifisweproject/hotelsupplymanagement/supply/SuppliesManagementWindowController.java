package com.unifisweproject.hotelsupplymanagement.supply;

import com.unifisweproject.hotelsupplymanagement.main.HotelSupplyManagementMain;
import com.unifisweproject.hotelsupplymanagement.itemsInOderAndSupply.ItemInSupply;
import com.unifisweproject.hotelsupplymanagement.main.MainMenuWindowController;
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

public class SuppliesManagementWindowController implements Initializable {

    private static final SuppliesManagementWindowController instance = new SuppliesManagementWindowController();        // Singleton per la finestra di gestione delle forniture
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
    private Button searchButton;
    @FXML
    private AnchorPane tableAnchorPane;
    private boolean searchView = false;
    private final ContextMenu rightClickMenu = new ContextMenu();
    private final MenuItem viewSupplyMenu = new MenuItem("Visualizza dettagli");
    private final MenuItem deleteSupplyMenu = new MenuItem("Elimina");
    private MainMenuWindowController mainMenuWindowController;
    private SuppliesManagement suppliesManagement;
    private ArrayList<Supply> results = new ArrayList<>();
    private final ObservableList<Supply> supplyRows = FXCollections.observableArrayList();
    private final ObservableList<Supply> searchResultRows = FXCollections.observableArrayList();
    private long lastClickTime = 0;

    private SuppliesManagementWindowController() {               // Costruttore privato per evitare la creazione di nuove istanze (SingleTon)
        mainMenuWindowController = MainMenuWindowController.getInstance();
        suppliesManagement = SuppliesManagement.getInstance();
    }

    public static SuppliesManagementWindowController getInstance() {          // Metodo per ottenere l'istanza della classe (SingleTon)
        return instance;
    }


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

        if (!mainMenuWindowController.getIsNotFirstTimeLoad().get(4)) {
            ResultSet resultSet = suppliesManagement.getRows(true, null);
            try {
                while(resultSet.next()) {
                    Supply supply = new Supply(resultSet.getInt("codice_fornitura"), resultSet.getInt("codice_fornitore"),
                            resultSet.getInt("codice_articolo"), resultSet.getString("data_fornitura"), resultSet.getInt("quantita"), resultSet.getDouble("prezzo"));
                    suppliesManagement.getSuppliesList().add(supply);
                }
                mainMenuWindowController.getIsNotFirstTimeLoad().set(4, true);
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
            double price = newSupplies.getPrezzo(i) * newSupplies.getQuantita(i);
            Supply newSupply = new Supply(newSupplies.getCodice_Fornitore(), newSupplies.getCodice_Articolo(i), newSupplies.getData_Fornitura(), newSupplies.getQuantita(i), price);
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

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/unifisweproject/hotelsupplymanagement/supply/SupplyDisplayWindow.fxml"));
            Parent root = loader.load();
            SupplyDisplayWindowController supplyDisplayWindowController = loader.getController();
            supplyDisplayWindowController.setDisplayedSupply(suppliesTable.getSelectionModel().getSelectedItem());
            supplyDisplayWindowController.setSuppliesManagementSceneController(this);
            Stage viewSupplyStage = new Stage();
            viewSupplyStage.initModality(Modality.APPLICATION_MODAL);
            viewSupplyStage.getIcons().add(HotelSupplyManagementMain.icon);
            viewSupplyStage.setTitle("Fornitura n°" + suppliesTable.getSelectionModel().getSelectedItem().getCodice_fornitura());
            viewSupplyStage.setResizable(false);
            viewSupplyStage.setScene(new Scene(root));
            viewSupplyStage.show();
        }
        catch (IOException e) {
            System.err.println("Errore durante l'apertura del file SupplyDisplayWindow.fxml: " + e.getMessage());
        }

    }

    public void displayAddView(ActionEvent ignoredEvent) {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/unifisweproject/hotelsupplymanagement/supply/SupplyAddWindow.fxml"));
            Parent root = loader.load();
            SupplyAddWindowController supplyAddWindowController = loader.getController();
            supplyAddWindowController.setSuppliesManagementSceneController(this);
            supplyAddWindowController.setMainMenuController(mainMenuWindowController);
            Stage addStage = new Stage();
            addStage.initModality(Modality.APPLICATION_MODAL);
            addStage.getIcons().add(HotelSupplyManagementMain.icon);
            addStage.setTitle("Aggiungi fornitura");
            addStage.setResizable(false);
            addStage.setScene(new Scene(root));
            addStage.show();
        }
        catch (IOException e) {
            System.err.println(e.getMessage());
        }

    }

    public void displaySearchView(ActionEvent ignoredEvent) {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/unifisweproject/hotelsupplymanagement/supply/SupplySearchWindow.fxml"));
            Parent root = loader.load();
            SupplySearchWindowController searchSupplyViewController = loader.getController();
            searchSupplyViewController.setSuppliesManagementSceneController(this);
            Stage searchStage = new Stage();
            searchStage.initModality(Modality.APPLICATION_MODAL);
            searchStage.setTitle("Ricerca fornitura");
            searchStage.setResizable(false);
            searchStage.getIcons().add(HotelSupplyManagementMain.icon);
            searchStage.setScene(new Scene(root));
            searchStage.show();
        }
        catch (IOException e) {
            System.err.println("Errore durante l'apertura del file SupplySearchWindow.fxml: " + e.getMessage());
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

    public void searchRow(Supply toBeSearched) {

        results.clear();
        try {
            results = HotelSupplyManagementMain.castArrayList(suppliesManagement.search(toBeSearched));       // Estrapola i risultati della ricerca
            searchView = true;
            searchResultRows.clear();
            Platform.runLater(() -> {
                searchResultRows.setAll(results);
                suppliesTable.getItems().clear();
                suppliesTable.setItems(searchResultRows);
                displaySearchResultsAlert();
            });
            backButton.setDisable(false);               // Riattiva il bottone "indietro" per tornare alla vista precedente
            backButton.setVisible(true);
            searchButton.setDisable(true);              // Disattiva il bottone di ricerca per evitare ricerche multiple
            searchButton.setVisible(false);
            addButton.setDisable(true);                 // Disattiva il bottone di aggiunta per evitare aggiunte multiple
            addButton.setVisible(false);
        }
        catch (NullPointerException e) {
            displayMissingParametersAlert(e);
        }

    }

    public void exitSearchView(ActionEvent ignoredEvent) {

        searchView = false;
        searchButton.setDisable(false);                 // Riattiva il bottone di ricerca
        searchButton.setVisible(true);
        updateTable();

    }

    public void displaySearchResultsAlert() {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Risultati ricerca");
        alert.setHeaderText("Risultati ricerca");
        alert.setContentText("Sono stati trovati " + results.size() + " risultati");
        alert.showAndWait();

    }

    public void displayMissingParametersAlert(NullPointerException e) {

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Errore");
        alert.setHeaderText("Parametri di ricerca vuoti");
        alert.setContentText("Inserire almeno un valore all'interno dei parametri di ricerca selezionati: " + e.getMessage());
        alert.showAndWait();

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

    public void setMainMenuController(MainMenuWindowController mainMenuWindowController) {
        this.mainMenuWindowController = mainMenuWindowController;
    }

    public void setSuppliesManagement(SuppliesManagement suppliesManagement) {
        this.suppliesManagement = suppliesManagement;
    }

    public void openDifferentManagement(ActionEvent event) {

        Stage stage = (Stage) ((MenuItem) event.getSource()).getParentPopup().getOwnerWindow();
        String menuName = ((MenuItem) event.getSource()).getText();
        mainMenuWindowController.getStageFromMenuBar(event, stage, menuName);

    }


}
