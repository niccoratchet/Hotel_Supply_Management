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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;
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
    @FXML
    private Button backButton;
    @FXML
    private Button searchButton;
    @FXML
    private Button addButton;
    @FXML
    private AnchorPane tableAnchorPane;

    private final ContextMenu rightClickMenu = new ContextMenu();               // Content Menu e MenuItem per poter visualizzare menù tasto destro
    private final MenuItem viewSupplierMenu = new MenuItem("Visualizza");
    private final MenuItem viewDeleteSupplierMenu = new MenuItem("Elimina");
    private Stage addStage;
    private SupplierManagement supplierManagement;
    private MainMenuController mainMenuController;
    private final ObservableList<Supplier> supplierRows = FXCollections.observableArrayList();

    private final ObservableList<Supplier> searchResultRows = FXCollections.observableArrayList();

    private boolean searchView = false;
    private ArrayList<Supplier> results = new ArrayList<>();
    private long lastClickTime = 0;
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        Platform.runLater(this::createRows);

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

        rightClickMenu.getItems().addAll(viewSupplierMenu, viewDeleteSupplierMenu);

        viewSupplierMenu.setOnAction(event -> displaySupplierView(null));

        viewDeleteSupplierMenu.setOnAction(event -> deleteRow());

        supplierTable.setOnMouseClicked(event -> {
            if (event.getButton().equals(MouseButton.PRIMARY)) {            // Controlla se il click è un doppio click e gestiscilo di conseguenza
                rightClickMenu.hide();
                if (event.getClickCount() == 2) {
                    long currentTime = System.currentTimeMillis();
                    if (currentTime - lastClickTime < 5000)
                    {
                        displaySupplierView(null);
                    }
                    lastClickTime = currentTime;
                }
            }
            else {
                SelectionModel<Supplier> selectionModel = supplierTable.getSelectionModel();        // verifico se è stato cliccato un elemento
                Supplier selectedSupplier = selectionModel.getSelectedItem();
                if(selectedSupplier != null)
                    rightClickMenu.show(tableAnchorPane, event.getScreenX(), event.getScreenY()); // Mostra il menu contestuale alle coordinate del click
            }
        });
    }

    public void createRows()  {

        if(!mainMenuController.getIsNotFirstTimeLoad().get(2)) {
            ResultSet resultSet = supplierManagement.getRows(true, null);

            try {
                while (resultSet.next()) {
                    Supplier supplier = new Supplier(resultSet.getInt(1), resultSet.getString(2),
                            resultSet.getString(3), resultSet.getString(4), resultSet.getString(5),
                            resultSet.getString(7),resultSet.getString(6));
                    supplierManagement.getSupplierList().add(supplier);
                }
                mainMenuController.getIsNotFirstTimeLoad().set(2, true);
            }

            catch (SQLException e) {
                System.err.println("Errore durante il riempimento della tabella");
            }
        }

        supplierRows.addAll(supplierManagement.getSupplierList());

        IDColumn.setCellValueFactory(new PropertyValueFactory<>("Codice_fornitore"));
        BusinessNameColumn.setCellValueFactory(new PropertyValueFactory<>("Ragione_sociale"));
        PivaColumn.setCellValueFactory(new PropertyValueFactory<>("P_IVA"));
        AddressColumn.setCellValueFactory(new PropertyValueFactory<>("Indirizzo"));
        CivicColumn.setCellValueFactory(new PropertyValueFactory<>("Civico"));
        CapColumn.setCellValueFactory(new PropertyValueFactory<>("CAP"));
        DateColumn.setCellValueFactory(new PropertyValueFactory<>("Data_inserimento"));

        supplierTable.setItems(supplierRows);                       // Inserisce nella tabella tutte le righe degli Item presenti nel DB

    }

    public void displayAddView() {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AddSupplierView.fxml"));
            Parent root = loader.load();
            AddSupplierViewController addSupplierController = loader.getController();
            addSupplierController.setSupplierManagementSceneController(this);
            addStage = new Stage();
            addStage.setTitle("Aggiungi fornitore");
            addStage.initModality(Modality.APPLICATION_MODAL);
            addStage.setScene(new Scene(root));
            addStage.show();
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
            if(searchView) {
                supplierTable.getItems().clear();
                searchResultRows.clear();
                searchResultRows.setAll(results);
                supplierTable.setItems(searchResultRows);
            }
            else {
                supplierTable.getItems().clear();
                supplierRows.clear();
                supplierRows.setAll(supplierManagement.getSupplierList());
                supplierTable.setItems(supplierRows);

                addButton.setDisable(false);                // Riattivo bottone di aggiunta
                addButton.setVisible(true);

                backButton.setDisable(true);                // Disattivo bottone "indietro" quando ho terminato una precedente ricerca
                backButton.setVisible(false);
            }
        });

    }

    public void exitSearch() {

        searchButton.setDisable(false);             // Riattivo bottone di ricerca
        searchButton.setVisible(true);
        searchView = false;
        updateTable();

    }

    public void setSupplierManagement(SupplierManagement supplierManagement) {
        this.supplierManagement = supplierManagement;
    }

    public void displaySupplierView(ActionEvent ignoredEvent) {

        SelectionModel<Supplier> selectionModel = supplierTable.getSelectionModel();
        Supplier selectedSupplier = selectionModel.getSelectedItem();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("SupplierView.fxml"));
            Parent root = loader.load();
            SupplierViewController supplierViewController = loader.getController();
            supplierViewController.setDisplayedSupplier(selectedSupplier);
            supplierViewController.setSupplierManagementSceneController(this);
            Stage stage = new Stage();
            stage.setTitle(selectedSupplier.getRagione_sociale());
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.show();
        }
        catch (IOException e) {
            System.err.println("Errore durante il caricamento della pagina SupplierView.fxml: " + e.getMessage());
        }

    }

    public boolean createConfirmDeleteAlert() {            // crea la finestra di avviso di cancellazione di un Item con richiesta di conferma

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Rimozione fornitore");
        alert.setContentText("Sicuro di procedere con l'eliminazione del fornitore dalla banca dati?");
        ButtonType buttonTypeYes = new ButtonType("Sì");
        ButtonType buttonTypeNo = new ButtonType("No");
        alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == buttonTypeYes;

    }

    public void createConfirmedItemModify() {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Modifiche applicate");
        alert.setContentText("Le modifiche sono state eseguite");
        alert.showAndWait();

    }

    public void deleteRow() {

        if (createConfirmDeleteAlert()) {
            SelectionModel<Supplier> selectionModel = supplierTable.getSelectionModel();
            Supplier selectedSupplier = selectionModel.getSelectedItem();
            supplierManagement.getSupplierList().remove(selectedSupplier);
            supplierManagement.delete(selectedSupplier.getCodice_fornitore());           // TODO: Mettere avviso prima della cancellazione

            if (searchView)
                results.remove(selectedSupplier);

            updateTable();
        }

    }

    public void searchRow(Supplier toBeSearched) {

        results.clear();
        try {
            results = HotelSupplyManagementMain.castArrayList(supplierManagement.search(toBeSearched));             // effettuo il cast della lista
            int numberOfResults = results.size();
            searchView = true;
            searchResultRows.clear();

            Platform.runLater(() -> {
                searchResultRows.setAll(results);
                supplierTable.getItems().clear();
                supplierTable.setItems(searchResultRows);

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Risultato ricerca");
                alert.setContentText("La ricerca ha reso " + numberOfResults + " risultati");
                alert.showAndWait();
            });

            backButton.setDisable(false);
            backButton.setVisible(true);
            searchButton.setDisable(true);
            searchButton.setVisible(false);
            addButton.setDisable(true);
            addButton.setVisible(false);

        }
        catch (NullPointerException e) {                            // Serve a gestire il caso in cui si lascino vuoti i campi di ricerca selezionati
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Errore");
            alert.setContentText("Parametri di ricerca vuoti: una volta spuntati inserire almeno un valore");
            alert.showAndWait();
        }

    }

    public void displaySearchItemView(ActionEvent ignoredEvent) {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("SearchSupplierView.fxml"));               // TODO: Replicare blocco try/catch su tutti gli altri caricamenti FXML
            Parent root = loader.load();
            SearchSupplierController searchSupplierController = loader.getController();
            searchSupplierController.setSupplierManagementSceneController(this);
            Stage stage = new Stage();
            stage.setTitle("Ricerca fornitore");
            stage.setScene(new Scene(root, 580, 400));
            stage.show();
        }
        catch(IOException e) {
            System.out.println("Errore durante il caricamento di SearchSupplierView: " + e);
        }

    }

    public void openDifferentManagement(ActionEvent event) {

        Stage stage = (Stage) ((MenuItem) event.getSource()).getParentPopup().getOwnerWindow();
        String menuName = ((MenuItem) event.getSource()).getParentMenu().getText();
        mainMenuController.getStageFromMenuBar(event, stage, menuName);

    }

    public void setMainMenuController(MainMenuController mainMenuController) {
        this.mainMenuController = mainMenuController;
    }

}
