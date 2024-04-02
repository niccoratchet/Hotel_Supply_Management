package com.unifisweproject.hotelsupplymanagement;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
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

public class OrderManagementSceneController implements Initializable{
    @FXML
    private TableView<Order> orderTable;
    @FXML
    private TableColumn<Order, Integer> IDColumn;
    @FXML
    private TableColumn<Order, String> TypeOfPaymentColumn;
    @FXML
    private TableColumn<Order, String> DateColumn;
    @FXML
    private TableColumn<Order, Integer> CustomerIDColumn;
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
    private ArrayList<Order> results = new ArrayList<>();
    private final ContextMenu rightClickMenu = new ContextMenu();               // Content Menu e MenuItem per poter visualizzare menù tasto destro
    private final MenuItem viewOrderMenu = new MenuItem("Visualizza");              //TODO cambiare nome
    private final MenuItem viewDeleteOrderMenu = new MenuItem("Elimina");           //TODO cambiare nome
    private OrderManagement orderManagement;
    private MainMenuController mainMenuController;
    private final ObservableList<Order> orderRows = FXCollections.observableArrayList();
    private final ObservableList<Order> searchResultRows = FXCollections.observableArrayList();

    private long lastClickTime = 0;

    @Override
    public void initialize(URL url, ResourceBundle rb) {            // Il metodo inizializza la tabella, inserendo tutte le righe presenti nel DataBase nella tabella Ordine

        Platform.runLater(this::createRows);
        orderTable.getSelectionModel().selectedItemProperty().addListener((observableValue, oldSelection, newSelection) -> deleteButton.setDisable(newSelection == null));
        rightClickMenu.getItems().addAll(viewOrderMenu, viewDeleteOrderMenu);
        viewOrderMenu.setOnAction(event -> displayOrderView(null));
        viewDeleteOrderMenu.setOnAction(event -> deleteRow());
        orderTable.setOnMouseClicked(event -> {
            if (event.getButton().equals(MouseButton.PRIMARY)) {            // Controlla se il click è un doppio click e gestiscilo di conseguenza
                rightClickMenu.hide();
                if (event.getClickCount() == 2) {
                    long currentTime = System.currentTimeMillis();
                    if (currentTime - lastClickTime < 5000)
                    {
                        displayOrderView(null);
                    }
                    lastClickTime = currentTime;
                }
            }
            else {
                SelectionModel<Order> selectionModel = orderTable.getSelectionModel();        // verifico se è stato cliccato un elemento
                Order selectedOrder = selectionModel.getSelectedItem();
                if(selectedOrder != null)
                    rightClickMenu.show(tableAnchorPane, event.getScreenX(), event.getScreenY()); // Mostra il menu contestuale alle coordinate del click
            }
        });

    }


    public void deleteRow() {

        if (createConfirmDeleteAlert()) {
            SelectionModel<Order> selectionModel = orderTable.getSelectionModel();
            Order selectedOrder = selectionModel.getSelectedItem();
            orderManagement.getOrderList().remove(selectedOrder);
            orderManagement.delete(selectedOrder.getCodice_ordine());
            try {                                                                                                                                                           //Codice per eliminare righe dalla tabella ArticoloInOrdine
                PreparedStatement statement = HotelSupplyManagementMain.conn.prepareStatement("DELETE FROM ArticoloInOrdine WHERE Codice_Ordine = " + selectedOrder.getCodice_ordine());
                statement.executeUpdate();
            } catch (SQLException e) {
                System.err.println("Errore durante l'eliminazione della riga ArticoloInOrdine: " + e.getMessage());
            }

            if (searchView)
                results.remove(selectedOrder);                   // Se sto visualizzando una ricerca, effettuo gli aggiornamenti anche su questa view
            updateTable();
        }

    }

    public void displayOrderView(ActionEvent ignoredEvent) {

        SelectionModel<Order> selectionModel = orderTable.getSelectionModel();
        Order selectedOrder = selectionModel.getSelectedItem();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("OrderView.fxml"));
            Parent root = loader.load();
            OrderViewController orderViewController = loader.getController();
            orderViewController.setDisplayedOrder(selectedOrder);
            orderViewController.setOrderManagementSceneController(this);
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle(Integer.toString(selectedOrder.getCodice_ordine()));
            stage.setScene(new Scene(root));
            stage.show();
        }
        catch (IOException e) {
            System.err.println("Errore durante l'apertura del file OrderView.fxml: " + e.getMessage());
        }

    }

    public void createRows()  {

        if(!mainMenuController.getIsNotFirstTimeLoad().get(3)) {
            ResultSet resultSet = orderManagement.getRows(true, null);
            try {
                while (resultSet.next()) {
                    Order order = new Order(resultSet.getInt(1), resultSet.getInt(5),
                            resultSet.getBoolean(2), resultSet.getString(3),
                            resultSet.getString(4));
                    orderManagement.getOrderList().add(order);
                }
                mainMenuController.getIsNotFirstTimeLoad().set(3, true);
            }
            catch (SQLException e) {
                System.err.println("Errore durante il riempimento della tabella");
            }
        }
        orderRows.addAll(orderManagement.getOrderList());
        IDColumn.setCellValueFactory(new PropertyValueFactory<>("Codice_ordine"));
        TypeOfPaymentColumn.setCellValueFactory(new PropertyValueFactory<>("Tipo_pagamento"));
        DateColumn.setCellValueFactory(new PropertyValueFactory<>("Data_ordine"));
        CustomerIDColumn.setCellValueFactory(new PropertyValueFactory<>("Codice_cliente"));
        orderTable.setItems(orderRows);                       // Inserisce nella tabella tutte le righe dei Customer presenti nel DB

    }

    public void displayAddView() {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AddOrderView.fxml"));
            Parent root = loader.load();
            AddOrderViewController addOrderController = loader.getController();
            addOrderController.setOrderManagementSceneController(this);
            addOrderController.setMainMenuController(mainMenuController);
            Stage addStage = new Stage();
            addStage.initModality(Modality.APPLICATION_MODAL);
            addStage.setTitle("Aggiungi ordine");
            addStage.setScene(new Scene(root));
            addStage.show();
        }
        catch (IOException e) {
            System.err.println(e.getMessage());
        }

    }

    public void addRow(Order newOrder) {

        newOrder.setCodice_ordine(orderManagement.getNextOrderCode() + 1);
        orderManagement.getOrderList().add(newOrder);
        orderManagement.add(newOrder);
        updateTable();

    }

    public void updateTable() {

        Platform.runLater(() -> {                       // Pulisci e aggiorna la tabella
            if(searchView) {
                orderTable.getItems().clear();
                searchResultRows.clear();
                searchResultRows.setAll(results);
                orderTable.setItems(searchResultRows);
            }
            else {
                orderTable.getItems().clear();
                orderRows.clear();
                orderRows.setAll(orderManagement.getOrderList());
                orderTable.setItems(orderRows);

                addButton.setDisable(false);                // Riattivo bottone di aggiunta
                addButton.setVisible(true);

                backButton.setDisable(true);                // Disattivo bottone "indietro" quando ho terminato una precedente ricerca
                backButton.setVisible(false);
            }
        });

    }

    public void setOrderManagement(OrderManagement orderManagement) {
        this.orderManagement = orderManagement;
    }

    public boolean createConfirmDeleteAlert() {            // crea la finestra di avviso di cancellazione di un Item con richiesta di conferma

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Rimozione ordine");
        alert.setContentText("Sicuro di procedere con l'eliminazione dell'ordine dalla banca dati?");
        ButtonType buttonTypeYes = new ButtonType("Sì");
        ButtonType buttonTypeNo = new ButtonType("No");
        alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == buttonTypeYes;

    }


    public void searchRow(Order toBeSearched) {

        results.clear();
        try {
            results = HotelSupplyManagementMain.castArrayList(orderManagement.search(toBeSearched));             // effettuo il cast della lista
            int numberOfResults = results.size();
            searchView = true;
            searchResultRows.clear();

            Platform.runLater(() -> {
                searchResultRows.setAll(results);
                orderTable.getItems().clear();
                orderTable.setItems(searchResultRows);

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

    public void openDifferentManagement(ActionEvent event) {

        Stage stage = (Stage) ((MenuItem) event.getSource()).getParentPopup().getOwnerWindow();
        String menuName = ((MenuItem) event.getSource()).getText();
        mainMenuController.getStageFromMenuBar(event, stage, menuName);

    }

    public void setMainMenuController(MainMenuController mainMenuController) {
        this.mainMenuController = mainMenuController;
    }

    public void displaySearchView(ActionEvent ignoredEvent) {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("SearchOrderView.fxml"));               // TODO: Replicare blocco try/catch su tutti gli altri caricamenti FXML
            Parent root = loader.load();
            SearchOrderController searchOrderController = loader.getController();
            searchOrderController.setOrderManagementSceneController(this);
            Stage searchStage = new Stage();
            searchStage.initModality(Modality.APPLICATION_MODAL);
            searchStage.setTitle("Ricerca ordine");
            searchStage.setScene(new Scene(root));
            searchStage.show();
        }
        catch(IOException e) {
            System.out.println("Errore durante il caricamento di SearchOrderView: " + e);
        }

    }

    public void exitSearch(ActionEvent ignoredEvent) {

        searchButton.setDisable(false);             // Riattivo bottone di ricerca
        searchButton.setVisible(true);
        searchView = false;
        updateTable();

    }
}
