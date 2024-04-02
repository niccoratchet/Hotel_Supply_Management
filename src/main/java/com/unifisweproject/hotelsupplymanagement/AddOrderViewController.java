package com.unifisweproject.hotelsupplymanagement;

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
import java.util.Objects;
import java.util.ResourceBundle;


public class AddOrderViewController implements Initializable {

    @FXML
    private DatePicker datePicker;
    @FXML
    private ChoiceBox<String> BFField;
    @FXML
    private ChoiceBox<String> typeOfPaymentField;
    @FXML
    private TextField customerCodeField;
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
    private Stage addItemStage;
    private OrderManagementSceneController orderManagementSceneController;
    private MainMenuController mainMenuController;
    private int lastOrderCode;
    private ItemInOrder itemInOrder = new ItemInOrder();
    private ArrayList<Integer> newAmount = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        datePicker.setValue(LocalDate.now());
        BFField.getItems().addAll("Bolla", "Fattura");
        typeOfPaymentField.getItems().addAll("Ricevuta bancaria", "Bonifico bancario", "Rimessa diretta");

        UnaryOperator<TextFormatter.Change> filterInt = change -> {             // Creazione del Formatter per inserimento delle quantità
            String text = change.getText();
            if (text.matches("[0-9]*")) {
                return change;
            }
            return null;
        };

        TextFormatter<String> textFormatterInt = new TextFormatter<>(filterInt);
        customerCodeField.setTextFormatter(textFormatterInt);

    }

    public void closeAddView(ActionEvent event) {
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
    }

    public void createOrder(ActionEvent event) {

            boolean bolla;
            bolla = BFField.getValue().equals("Bolla");
            Order newOrder = new Order(Integer.parseInt(customerCodeField.getText()), bolla, typeOfPaymentField.getValue(), datePicker.getValue().toString());
            orderManagementSceneController.addRow(newOrder);
            updateAmount();
            updateItemInOrder();
            ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();        // Istruzione per chiudere il form

        } catch (RuntimeException missingParameters) {

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Errore");
            alert.setHeaderText("Parametri assenti");
            alert.setContentText("Inserire il valore di tutti i dati obbligatori.");
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

}
