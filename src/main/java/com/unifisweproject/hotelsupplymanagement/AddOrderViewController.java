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

public class AddOrderViewController implements Initializable {

    @FXML
    private DatePicker datePicker;
    @FXML
    private ChoiceBox<String> BFField;
    @FXML
    private ChoiceBox<String> typeOfPaymentField;
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
    private ObservableList<Item> itemList = FXCollections.observableArrayList();
    @FXML
    private ChoiceBox<String> customerList;
    private ResultSet resultSet;
    private Stage addItemStage;
    private OrderManagementSceneController orderManagementSceneController;
    private MainMenuController mainMenuController;
    private int lastOrderCode;
    private ItemInOrder itemInOrder = new ItemInOrder();
    private ArrayList<Integer> newAmount = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        Platform.runLater(() -> {
            datePicker.setValue(LocalDate.now());
            BFField.getItems().addAll("Bolla", "Fattura");
            typeOfPaymentField.getItems().addAll("Ricevuta bancaria", "Bonifico bancario", "Rimessa diretta");
            getCustomerList();
        });

    }

    public void getCustomerList() {

        resultSet = getResultSet();
        try {
            while (resultSet.next()) {
                if(resultSet.getString("Ragione_Sociale") == null)
                    customerList.getItems().add(resultSet.getString("Codice_Fiscale"));
                else
                    customerList.getItems().add(resultSet.getString("Ragione_Sociale"));
            }
        }
        catch (SQLException e) {
            System.err.println("Errore durante l'estrapolazione dei dati dei clienti: " + e.getMessage());
        }

    }


    public ResultSet getResultSet() {                   // Metodo per riottenere il ResultSet contenente i dati Codice_Fornitore e Ragione_Sociale dei fornitori dal DB

        try {
            Statement statement = HotelSupplyManagementMain.conn.createStatement();
            return statement.executeQuery("SELECT Codice_Cliente, Ragione_Sociale, Codice_Fiscale FROM Cliente");
        }
        catch (SQLException e) {
            System.err.println("Errore durante l'estrapolazione dei dati dei clienti: " + e.getMessage());
            return null;
        }

    }

    public void closeAddView(ActionEvent event) {
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
    }

    public void createOrder(ActionEvent event) {

        int customerCode = 0;
        try {
            int i = 0;
            while (i < 4) {
                switch (i) {
                    case 0 -> {
                        if (BFField.getValue() == null)
                            throw new RuntimeException("B/F mancante");
                    }
                    case 1 -> {
                        if (typeOfPaymentField.getValue() == null)
                            throw new RuntimeException("Tipo di pagamento mancante");
                    }
                    case 2 -> {
                        if (datePicker.getValue() == null)
                            throw new RuntimeException("Data mancante");
                    }
                    case 3 -> {
                        if(itemInOrder.getNumberOfItems() == 0)
                            throw new RuntimeException("Nessun articolo inserito");
                    }
                }
                i++;
            }
            try {
                resultSet = getResultSet();
                while (resultSet.next()) {
                    if (customerList.getValue().equals(resultSet.getString(2)) || customerList.getValue().equals(resultSet.getString(3))) {
                        customerCode = resultSet.getInt(1);
                        itemInOrder.setCodice_Ordine(customerCode);
                    }
                }
            }
            catch (SQLException e) {
                System.err.println("Errore durante l'estrapolazione del codice cliente: " + e.getMessage());
            }
            boolean bolla;
            bolla = BFField.getValue().equals("Bolla");
            Order newOrder = new Order(customerCode, bolla, typeOfPaymentField.getValue(), datePicker.getValue().toString());
            orderManagementSceneController.addRow(newOrder);
            updateAmount();
            updateItemInOrder();
            ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();        // Istruzione per chiudere il form
        }
        catch (RuntimeException missingParameters) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Errore");
            alert.setHeaderText("Parametri assenti");
            alert.setContentText("Inserire il valore di tutti i dati obbligatori. " + missingParameters.getMessage());
            alert.showAndWait();
        }

    }

    public void openListOfItemView (ActionEvent ignoredEvent) {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ListOfItems.fxml"));
            Parent root = loader.load();
            ListOfItemsController listOfItemsController = loader.getController();
            listOfItemsController.setAddOrderViewController(this);
            listOfItemsController.setMainMenuController(mainMenuController);
            addItemStage = new Stage();
            addItemStage.setTitle("Aggiungi articoli all'ordine");
            addItemStage.initModality(Modality.APPLICATION_MODAL);
            addItemStage.setScene(new Scene(root));
            addItemStage.show();
        }
        catch (IOException e) {
            System.err.println("Errore durante l'apertura del file ListOfItems.fxml: "+ e.getMessage());
        }

    }

    public void setOrderManagementSceneController(OrderManagementSceneController orderManagementSceneController) {
        this.orderManagementSceneController = orderManagementSceneController;
    }

    public void setMainMenuController(MainMenuController mainMenuController) {
        this.mainMenuController = mainMenuController;
    }

    public void addRow(Item newItem){

        itemList.add(newItem);
        itemCodeColumn.setCellValueFactory(new PropertyValueFactory<>("Codice_articolo"));
        itemNameColumn.setCellValueFactory(new PropertyValueFactory<>("Nome"));
        itemQuantityColumn.setCellValueFactory(new PropertyValueFactory<>("Quantita"));
        itemPriceColumn.setCellValueFactory(new PropertyValueFactory<>("Prezzo"));
        itemDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("Descrizione"));
        itemTableView.setItems(itemList);
        itemTableView.getColumns().setAll(itemCodeColumn, itemNameColumn, itemQuantityColumn, itemPriceColumn, itemDescriptionColumn);

    }

    public void updateItemInOrder() {        // Inserisce nel database gli articoli inerenti all'ordine

        String getCodeQuery = "SELECT seq FROM sqlite_sequence WHERE name = 'Ordine'";
        try {
            Statement statement = HotelSupplyManagementMain.conn.createStatement();
            ResultSet resultSet = statement.executeQuery(getCodeQuery);
            lastOrderCode = resultSet.getInt(1);
        }
        catch(SQLException e) {
            System.err.println("Errore durante l'estrapolazione dell'ultimo codice ordine: " + e.getMessage());
        }
        itemInOrder.setCodice_Ordine(lastOrderCode);
        for(int i = 0; i < itemInOrder.getNumberOfItems(); i++) {
            String addQuery = "INSERT INTO ArticoloInOrdine (Codice_Ordine, Codice_Articolo, Quantita) \n" +       // creazione della query di inserimento
                    "VALUES (?, ?, ?)";
            try {
                PreparedStatement preparedStatement = HotelSupplyManagementMain.conn.prepareStatement(addQuery);
                preparedStatement.setInt(1, itemInOrder.getCodice_Ordine());
                preparedStatement.setInt(2, itemInOrder.getCodice_Articolo(i));
                preparedStatement.setInt(3, itemInOrder.getQuantita(i));
                preparedStatement.executeUpdate();                                                          // una volta creata, si invia il comando al DBMS
            }
            catch (SQLException e) {
                System.out.println("Errore durante l'aggiunta di un item in un order: "+ e.getMessage() +" \n Query utilizzata: " + addQuery);
            }
        }

    }

    public void updateAmount() {

        for(int i = 0; i < itemInOrder.getNumberOfItems(); i++){
            String modifyQuery = "UPDATE Articolo SET Quantita = ? WHERE Codice_Articolo = " + itemInOrder.getCodice_Articolo(i);       //Istruzioni per aggiornare la quantita dell'item del DB
            try {
                PreparedStatement statement = HotelSupplyManagementMain.conn.prepareStatement(modifyQuery);
                statement.setInt(1, newAmount.get(i));
                statement.executeUpdate();
            }
            catch (SQLException e) {
                System.err.println("Errore di formattazione nella generazione della query di modifica: " + e.getMessage());
            }
        }

    }

    public ArrayList<Integer> getNewAmount() {
        return this.newAmount;
    }

    public ItemInOrder getItemInOrder() {
        return this.itemInOrder;
    }

}
