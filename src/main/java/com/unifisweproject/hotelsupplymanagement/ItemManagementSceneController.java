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
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

public class ItemManagementSceneController implements Initializable {

    @FXML
    private TableView<Item> itemTable;
    @FXML
    private TableColumn<Item, Integer> IDColumn;
    @FXML
    private TableColumn<Item, String> NameColumn;
    @FXML
    private TableColumn<Item, Integer> AmountColumn;
    @FXML
    private TableColumn<Item, Double> PriceColumn;
    @FXML
    private TableColumn<Item, String> DescriptionColumn;
    @FXML
    private TableColumn<Item, String> DateColumn;
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
    private final MenuItem viewItemMenu = new MenuItem("Visualizza");
    private final MenuItem viewDeleteItemMenu = new MenuItem("Elimina");

    private ItemManagement itemManagement;
    private final ObservableList<Item> itemRows = FXCollections.observableArrayList();    // Lista di righe presenti nella tabella, si aggiorna nel caso dell'aggiunta di una riga

    private final ObservableList<Item> searchResultRows = FXCollections.observableArrayList();
    private boolean searchView = false;
    private ArrayList<Item> results = new ArrayList<>();
    private long lastClickTime = 0;

    @Override
    public void initialize(URL url, ResourceBundle rb) {            // Il metodo inizializza la tabella, inserendo tutte le righe presenti nel DataBase nella tabella Articolo

        Platform.runLater(this::createRows);            // TODO: Provare a togliere parte duplicata

        itemTable.getSelectionModel().selectedItemProperty().addListener((observableValue, oldSelection, newSelection) -> {
            if(newSelection != null) {
                modifyButton.setDisable(false);             // Aggiunta del listener nella tabella per rilevare quale elemento viene selezionato
                deleteButton.setDisable(false);
            }
            else {
                modifyButton.setDisable(true);
                deleteButton.setDisable(true);
            }
        });

        rightClickMenu.getItems().addAll(viewItemMenu, viewDeleteItemMenu);

        viewItemMenu.setOnAction(event -> {
            try {
                displayItemView(null);
            } catch (IOException e) {
                System.out.println("Non è stato possibile visualizzare l'item selezionato");
            }
        });

        viewDeleteItemMenu.setOnAction(event -> deleteRow());

        itemTable.setOnMouseClicked(event -> {

            if (event.getButton().equals(MouseButton.PRIMARY)) {            // Controlla se il click è un doppio click e gestiscilo di conseguenza
                rightClickMenu.hide();
                if (event.getClickCount() == 2) {
                    long currentTime = System.currentTimeMillis();
                    if (currentTime - lastClickTime < 5000)
                    {
                        try {
                            displayItemView(null);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    lastClickTime = currentTime;
                }
            }
            else {

                SelectionModel<Item> selectionModel = itemTable.getSelectionModel();        // verifico se è stato cliccato un elemento
                Item selectedItem = selectionModel.getSelectedItem();
                if(selectedItem != null)
                    rightClickMenu.show(tableAnchorPane, event.getScreenX(), event.getScreenY()); // Mostra il menu contestuale alle coordinate del click

            }

        });

    }

    public void createRows()  {

        ResultSet resultSet = itemManagement.getRows(true, null);

        try {
            while (resultSet.next()) {
                Item item = new Item(resultSet.getInt(1), resultSet.getInt(4),
                        resultSet.getDouble(3), resultSet.getString(2), resultSet.getString(5), resultSet.getString(6));
                itemManagement.getItemList().add(item);
            }
        }

        catch (SQLException e) {
            System.err.println("Errore durante il riempimento della tabella");
        }

        itemRows.addAll(itemManagement.getItemList());

        IDColumn.setCellValueFactory(new PropertyValueFactory<>("Codice_articolo"));
        NameColumn.setCellValueFactory(new PropertyValueFactory<>("Nome"));
        AmountColumn.setCellValueFactory(new PropertyValueFactory<>("Quantita"));
        PriceColumn.setCellValueFactory(new PropertyValueFactory<>("Prezzo"));
        DescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("Descrizione"));
        DateColumn.setCellValueFactory(new PropertyValueFactory<>("Data_inserimento"));

        itemTable.setItems(itemRows);                       // Inserisce nella tabella tutte le righe degli Item presenti nel DB

    }

    public void displayAddView() {

        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("AddItemView.fxml"));
            Parent root = loader.load();

            AddItemViewController addItemController = loader.getController();
            addItemController.setItemManagementSceneController(this);

            Stage stage = new Stage();
            stage.setTitle("Aggiungi prodotto");
            stage.setScene(new Scene(root, 580, 400));
            stage.show();

        }
        catch (IOException e) {
            System.err.println(e.getMessage());
        }

    }

    public void addRow(Item newItem) {

        newItem.setCodice_articolo(itemManagement.getNextItemCode() + 1);
        itemManagement.getItemList().add(newItem);
        itemManagement.add(newItem);
        updateTable();

    }

    public void modifyRow(Item toBeModified) {

        itemManagement.modify(toBeModified);
        createConfirmedItemModify();
        updateTable();

    }

    public void updateTable() {

        Platform.runLater(() -> {                       // Pulisci e aggiorna la tabella

            if(searchView) {

                itemTable.getItems().clear();
                searchResultRows.clear();
                searchResultRows.setAll(results);
                itemTable.setItems(searchResultRows);

            }
            else {

                itemTable.getItems().clear();
                itemRows.clear();
                itemRows.setAll(itemManagement.getItemList());
                itemTable.setItems(itemRows);

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

    public void setItemManagement(ItemManagement itemManagement) {
        this.itemManagement = itemManagement;
    }

    public void displayItemView(ActionEvent ignoredEvent) throws IOException {

        SelectionModel<Item> selectionModel = itemTable.getSelectionModel();
        Item selectedItem = selectionModel.getSelectedItem();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ItemView.fxml"));
            Parent root = loader.load();
            ItemViewController itemViewController = loader.getController();
            itemViewController.setDisplayedItem(selectedItem);
            itemViewController.setItemManagementSceneController(this);

            Stage stage = new Stage();
            stage.setTitle(selectedItem.getNome());
            stage.setScene(new Scene(root, 580, 400));
            stage.show();

        }
        catch (IOException e) {
            System.err.println("Errore durante l'apertura del file ItemView.fxml: " + e.getMessage());
        }

    }

    public boolean createConfirmDeleteAlert() {            // crea la finestra di avviso di cancellazione di un Item con richiesta di conferma

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Rimozione prodotto");
        alert.setContentText("Sicuro di procedere con l'eliminazione del prodotto dalla banca dati?");

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
            SelectionModel<Item> selectionModel = itemTable.getSelectionModel();
            Item selectedItem = selectionModel.getSelectedItem();
            itemManagement.getItemList().remove(selectedItem);
            itemManagement.delete(selectedItem.getCodice_articolo());

            if (searchView)
                results.remove(selectedItem);                   // Se sto visualizzando una ricerca, effettuo gli aggiornamenti anche su questa view

            updateTable();
        }

    }

    public void searchRow(Item toBeSearched) {

        results.clear();

        try {

            results = HotelSupplyManagementMain.castArrayList(itemManagement.search(toBeSearched));             // effettuo il cast della lista
            int numberOfResults = results.size();
            searchView = true;
            searchResultRows.clear();

            Platform.runLater(() -> {

                searchResultRows.setAll(results);
                itemTable.getItems().clear();
                itemTable.setItems(searchResultRows);

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

            FXMLLoader loader = new FXMLLoader(getClass().getResource("SearchItemView.fxml"));               // TODO: Replicare blocco try/catch su tutti gli altri caricamenti FXML
            Parent root = loader.load();

            SearchItemController searchItemController = loader.getController();
            searchItemController.setItemManagementSceneController(this);

            Stage stage = new Stage();
            stage.setTitle("Ricerca articolo");
            stage.setScene(new Scene(root, 580, 400));
            stage.show();

        }
        catch(IOException e) {
            System.out.println("Errore durante il caricamento di SearchItemView: " + e);
        }

    }

}
