package com.unifisweproject.hotelsupplymanagement.order;

import com.unifisweproject.hotelsupplymanagement.FXMLWindowLoader;
import com.unifisweproject.hotelsupplymanagement.item.Item;
import com.unifisweproject.hotelsupplymanagement.item.ItemManagement;
import com.unifisweproject.hotelsupplymanagement.itemsInOderAndSupply.ItemsInOrderManagement;
import com.unifisweproject.hotelsupplymanagement.itemsInOderAndSupply.ListOfItemsWindow;
import com.unifisweproject.hotelsupplymanagement.main.HotelSupplyManagementMain;
import com.unifisweproject.hotelsupplymanagement.main.MainMenuWindowController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Objects;

public class OrderManagementController {

    private static final OrderManagementController instance = new OrderManagementController();      // Singleton
    private boolean searchView = false;
    private ArrayList<Order> results = new ArrayList<>();
    private final OrderManagement orderManagement;
    private final MainMenuWindowController mainMenuWindowController;
    private OrderManagementView orderManagementView;
    private OrderDisplayWindow orderDisplayWindow;
    private OrderSearchWindow orderSearchWindow;
    private ListOfItemsWindow listOfItemsWindow;
    private OrderAddWindow orderAddWindow;
    private ItemsInOrderManagement itemsInOrderManagement;
    private ResultSet resultSet;
    private final ArrayList<Integer> newAmount = new ArrayList<>();             // Lista per memorizzare le nuove quantita degli item in un nuovo ordine

    private OrderManagementController() {

        mainMenuWindowController = MainMenuWindowController.getInstance();
        orderManagement = OrderManagement.getInstance();

    }

    public static OrderManagementController getInstance() {
        return instance;
    }

    public void initializeRows()  {

        if(!mainMenuWindowController.getIsNotFirstTimeLoad().get(3)) {
            try {
                orderManagement.loadFromDB();
                mainMenuWindowController.getIsNotFirstTimeLoad().set(3, true);
            }
            catch (SQLException e) {
                System.err.println("Errore durante il riempimento della tabella");
            }
        }
        ObservableList<Order> orderRows = FXCollections.observableArrayList(orderManagement.getOrderList());
        orderManagementView.setRows(orderRows);

    }

    public ObservableList<String> getCustomerList() {           // Metodo per ottenere i dati dei clienti dal DB e inserirli nella ChoiceBox della finestra OrderAddWindow

        resultSet = orderManagement.getCustomerList();
        ObservableList<String> customerList = FXCollections.observableArrayList();
        try {
            while (resultSet.next()) {
                if(resultSet.getString("Ragione_Sociale") == null)
                    customerList.add(resultSet.getString("Codice_Fiscale"));
                else
                    customerList.add(resultSet.getString("Ragione_Sociale"));
            }
            return customerList;
        }
        catch (SQLException e) {
            System.err.println("Errore durante l'estrapolazione dei dati dei clienti: " + e.getMessage());
            return null;
        }

    }

    public void createRow(ActionEvent event) {              // Metodo per la creazione di un nuovo ordine fatto partire dalla finestra OrderAddWindow

        int customerCode = 0;
        String BFValue = orderAddWindow.getBFField().getValue();
        String TypeOfPayment = orderAddWindow.getTypeOfPaymentField().getValue();
        String selectedCustomer = orderAddWindow.getCustomerList().getValue();
        LocalDate orderDate = orderAddWindow.getDatePicker().getValue();
        try {
            if ((BFValue == null) || (TypeOfPayment == null) || (orderDate == null) || (itemsInOrderManagement.getNumberOfItems() == 0))
                throw new RuntimeException("Parametri mancanti");
            try {
                resultSet = orderManagement.getCustomerList();
                while (resultSet.next()) {
                    if (selectedCustomer.equals(resultSet.getString("Ragione_Sociale")) || selectedCustomer.equals(resultSet.getString("Codice_Fiscale"))) {
                        customerCode = resultSet.getInt("Codice_Cliente");
                    }
                }
                orderManagement.add(new Order(customerCode, BFValue.equals("Bolla"), TypeOfPayment, orderDate.toString()));
                updateTable();
                itemsInOrderManagement.insertItemInOrderRows(newAmount, orderManagement.getLastOrderCode());
                itemsInOrderManagement = null;                                                                 // Una volta terminata l'aggiunta, l'oggetto ItemsInOrderManagement usato per la creazione dell'ordine viene messo a null per poter essere di nuovo inizializzato
                orderAddWindow.closeAddView(event);
            }
            catch (SQLException e) {
                System.err.println("Errore durante l'estrapolazione del codice cliente: " + e.getMessage());
            }
        }
        catch (RuntimeException missingParameters)
        {
            HotelSupplyManagementMain.generateAlert(Alert.AlertType.ERROR, "Errore", "Parametri assenti",
                    "Inserire il valore di tutti i dati obbligatori. " + missingParameters.getMessage());
        }

    }

    public void addItemToOrder(ActionEvent event) {               // Metodo per aggiungere un articolo a un ordine e visualizzare in OrderAddWindow, fatto partire da ListOfItemsWindow

        Item selectedItem = listOfItemsWindow.getItemTableView().getSelectionModel().getSelectedItem();         // TODO: Probabilmente anche la controparte Supply dovrà avere un metodo simile
        int newAmount;
        String quantity = listOfItemsWindow.getQuantityField().getText();
        if (quantity.equals("0")) {
            HotelSupplyManagementMain.generateAlert(Alert.AlertType.ERROR, "Errore", "Quantità errata",
                    "Non e' possibile aggiungere un articolo con quantita' pari a 0");
            return;
        }
        newAmount = selectedItem.getQuantita() - Integer.parseInt(quantity);
        if(newAmount < 0) {
            HotelSupplyManagementMain.generateAlert(Alert.AlertType.ERROR, "Errore", "Quantità errata",
                    "Non ci sono abbastanza articoli in magazzino, inserire una quantita' valida");
        }
        else {
            addItemInTable(newAmount, selectedItem, event);
        }

    }

    public void addItemInTable(int amount, Item selectedItem, ActionEvent event) {                          // Aggiorna la tabella degli articoli in un ordine aggiornando anche l'oggetto ItemsInOrderManagement

        String quantity = listOfItemsWindow.getQuantityField().getText();
        Item itemToAdd = new Item(selectedItem.getCodice_articolo(), Integer.parseInt(quantity), selectedItem.getPrezzo(),
                selectedItem.getNome(), selectedItem.getDescrizione(), selectedItem.getData_inserimento());
        orderAddWindow.getItemList().add(itemToAdd);
        newAmount.add(amount);
        itemsInOrderManagement.addCodice_Articolo(selectedItem.getCodice_articolo());
        itemsInOrderManagement.addQuantita(Integer.parseInt(quantity));
        ObservableList<Item> orderRows = FXCollections.observableArrayList(orderAddWindow.getItemList());
        orderAddWindow.setRows(orderRows);
        listOfItemsWindow.closeAddItem(event);

    }

    public void initializeListOfItemsTable()  {             // Metodo per inizializzare la tabella degli articoli da aggiungere in un ordine in ListOfItemsWindow

        try {
            if (!mainMenuWindowController.getIsNotFirstTimeLoad().get(0)) {
                ItemManagement.getInstance().loadFromDB();                          // Se nel corso del programma non è stata ancora caricata la lista degli articoli, si richiama il metodo loadFromDB
                mainMenuWindowController.getIsNotFirstTimeLoad().set(0, true);
            }
        }
        catch (SQLException e) {
            System.err.println("Errore durante il riempimento della tabella");
        }
        ObservableList<Item> itemRows = FXCollections.observableArrayList(ItemManagement.getInstance().getItemList());
        listOfItemsWindow.setRows(itemRows);

    }

    public void updateTable() {

        ObservableList<Order> orderRows;
        if(searchView) {
            orderRows = FXCollections.observableArrayList(results);
        }
        else {
            orderRows = FXCollections.observableArrayList(orderManagement.getOrderList());
        }
        orderManagementView.setRows(orderRows);

    }

    public void displayOrderInfo() {                // Metodo per aggiungere gli articoli e le informazioni di un ordine nella finestra OrderDisplayWindow

        String bf;
        Order displayedOrder = orderDisplayWindow.getDisplayedOrder();
        orderDisplayWindow.getCodeLabel().setText("Dati ordine n°" + displayedOrder.getCodice_ordine());
        orderDisplayWindow.getTypeOfPaymentField().setValue(displayedOrder.getTipo_pagamento());
        orderDisplayWindow.getDatePicker().setValue(LocalDate.parse(displayedOrder.getData_ordine()));
        if(displayedOrder.isBolla()) {
            bf = "Bolla";
        }
        else {
            bf = "Fattura";
        }
        orderDisplayWindow.getBFField().setValue(bf);
        orderDisplayWindow.getItemList().setAll(ItemsInOrderManagement.getItemFromAnOrder(displayedOrder));
        ObservableList<Item> itemRows = FXCollections.observableArrayList(orderDisplayWindow.getItemList());
        orderDisplayWindow.setRows(itemRows);

    }

    public void deleteRow(Object toBeDeleted) {

        if (HotelSupplyManagementMain.displayConfirmationAlert("Attenzione","Rimozione ordine", "Sicuro di procedere con l'eliminazione dell'ordine dalla banca dati?")) {
            Order selectedOrder = (Order) toBeDeleted;
            orderManagement.delete(toBeDeleted);
            if (searchView)
                results.remove(selectedOrder);                   // Se sto visualizzando una ricerca, effettuo gli aggiornamenti anche su questa view
            updateTable();
        }

    }

    public void searchRow(ActionEvent event) {

        Order toBeSearched = getSearchFilters();
        if (toBeSearched != null) {
            results.clear();
            try {
                results = HotelSupplyManagementMain.castArrayList(orderManagement.search(toBeSearched));             // effettuo il cast della lista
                int numberOfResults = results.size();
                searchView = true;
                ObservableList<Order> searchResultRows = FXCollections.observableArrayList(results);
                orderManagementView.setRows(searchResultRows);
                HotelSupplyManagementMain.generateAlert(Alert.AlertType.INFORMATION, "Completata", "Risultato ricerca",
                        "La ricerca ha restituito " + numberOfResults + " risultati");
                orderManagementView.enableBackButton();
            }
            catch (NullPointerException e) {                            // Serve a gestire il caso in cui si lascino vuoti i campi di ricerca selezionati
                HotelSupplyManagementMain.generateAlert(Alert.AlertType.ERROR, "Errore", "Parametri mancanti",
                        "Inserire almeno un parametro di ricerca");
            }
            orderSearchWindow.closeSearchView(event);
        }

    }

    public Order getSearchFilters() {

        Order searchItem = new Order(-1, -1, false,
                null, null);   // NOTA: è un oggetto order fittizio utile alla ricerca
        int i = 0;
        try {
            while (i < 4) {
                switch (i) {
                    case 0 -> {
                        if (!orderSearchWindow.getOrderCodeField().isDisabled() && ! "".equals(orderSearchWindow.getOrderCodeField().getText()))
                            searchItem.setCodice_ordine(Integer.parseInt(orderSearchWindow.getOrderCodeField().getText()));
                    }
                    case 1 -> {
                        if (!orderSearchWindow.getTypeOfPaymentField().isDisabled() && ! "".equals(orderSearchWindow.getTypeOfPaymentField().getValue()))
                            searchItem.setTipo_pagamento(orderSearchWindow.getTypeOfPaymentField().getValue());
                    }
                    case 2 -> {
                        if (!orderSearchWindow.getDatePicker().isDisabled() && orderSearchWindow.getDatePicker().getValue() != null)
                            searchItem.setData_ordine(orderSearchWindow.getDatePicker().getValue().toString());
                    }
                    case 3 -> {
                        if (!orderSearchWindow.getCustomerCodeField().isDisabled() && ! "".equals(orderSearchWindow.getCustomerCodeField().getText()))
                            searchItem.setCodice_cliente(Integer.parseInt(orderSearchWindow.getCustomerCodeField().getText()));
                    }
                }
                i++;
            }
            return searchItem;
        }
        catch (NumberFormatException e) {
            HotelSupplyManagementMain.generateAlert(Alert.AlertType.ERROR, "Errore", "Formattazione scorretta",
                    "Errore di formattazione: verifica i campi inseriti");
            return null;
        }

    }

    public void openDifferentManagement(ActionEvent event) {

        Stage stage = (Stage) ((MenuItem) event.getSource()).getParentPopup().getOwnerWindow();
        String menuName = ((MenuItem) event.getSource()).getText();
        mainMenuWindowController.getStageFromMenuBar(event, stage, menuName);

    }

    public void displayView(ActionEvent event) {

        try {
            orderManagementView = new OrderManagementView();
            FXMLWindowLoader.loadFXML(getClass().getResource("/com/unifisweproject/hotelsupplymanagement/order/OrderManagementWindow.fxml"),
                    orderManagementView, true, event, "Gestione ordini", false);
        }
        catch (IOException e) {
            System.err.println("Errore durante l'apertura del file OrderManagementView.fxml: " + e.getMessage());
        }
        initializeRows();
        if (Objects.isNull(itemsInOrderManagement))
            itemsInOrderManagement = new ItemsInOrderManagement();

    }

    public void displayRowView(ActionEvent event, Object toBeDisplayed) {

        try {
            orderDisplayWindow = new OrderDisplayWindow(((Order) toBeDisplayed));
            FXMLWindowLoader.loadFXML(getClass().getResource("/com/unifisweproject/hotelsupplymanagement/order/OrderDisplayWindow.fxml"),
                    orderDisplayWindow, false, event, "Visualizza ordine", false);
        }
        catch (IOException e) {
            System.err.println("Errore durante l'apertura del file OrderDisplayWindow.fxml: " + e.getMessage());
        }

    }

    public void displaySearchView(ActionEvent event) {

        try {
            orderSearchWindow = new OrderSearchWindow();
            FXMLWindowLoader.loadFXML(getClass().getResource("/com/unifisweproject/hotelsupplymanagement/order/OrderSearchWindow.fxml"),
                    orderSearchWindow, false, event, "Ricerca ordine", false);
        }
        catch(IOException e) {
            System.out.println("Errore durante il caricamento di SearchOrderView: " + e);
        }

    }

    public void displayAddView() {

        try {
            orderAddWindow = new OrderAddWindow();
            FXMLWindowLoader.loadFXML(getClass().getResource("/com/unifisweproject/hotelsupplymanagement/order/OrderAddWindow.fxml"), orderAddWindow,
                    false, null, "Aggiungi ordine", false);
        }
        catch (IOException e) {
            System.err.println("Errore durante il caricamento del file OrderAddWindow.fxml: "+e.getMessage());
        }

    }

    public void openListOfItemView (ActionEvent ignoredEvent) {

        try {
            listOfItemsWindow = new ListOfItemsWindow(this);
            FXMLWindowLoader.loadFXML(getClass().getResource("/com/unifisweproject/hotelsupplymanagement/listOfItems/ListOfItemsWindow.fxml"),
                    listOfItemsWindow, false, null, "Aggiungi articoli all'ordine", false);
        }
        catch (IOException e) {
            System.err.println("Errore durante l'apertura del file ListOfItemsWindow.fxml: "+ e.getMessage());
        }
        initializeListOfItemsTable();
        if (Objects.isNull(itemsInOrderManagement))
            itemsInOrderManagement = new ItemsInOrderManagement();

    }

    public void handleActionEvent(ActionEvent event) {

        if (event.getSource() instanceof Button button) {
            switch (button.getId()) {
                case "addButton" -> displayAddView();
                case "searchButton" -> displaySearchView(event);
                case "backButton" -> orderManagementView.exitSearch();
                case "modifyButton" -> {
                    Order selectedOrder = orderManagementView.getOrderTable().getSelectionModel().getSelectedItem();
                    displayRowView(null, selectedOrder);
                }
                case "deleteButton" -> {
                    Order selectedOrder = orderManagementView.getOrderTable().getSelectionModel().getSelectedItem();
                    deleteRow(selectedOrder);
                }
                case "addItemButton" -> openListOfItemView(event);
            }
        }
        else if (event.getSource() instanceof MenuItem menuItem) {
            if (menuItem.getId().equals("viewOrderMenu") || menuItem.getId().equals("viewDeleteOrderMenu")) {
                Order selectedOrder = orderManagementView.getOrderTable().getSelectionModel().getSelectedItem();
                if (Objects.nonNull(selectedOrder)) {
                    if (menuItem.getId().equals("viewOrderMenu"))
                        displayRowView(null, selectedOrder);
                    else
                        deleteRow(selectedOrder);
                }
            }
            else
                openDifferentManagement(event);
        }

    }

    public void handleMouseEvent(Order selectedOrder) {
        displayRowView(null, selectedOrder);
    }

    public void setSearchView(boolean searchView) {
        this.searchView = searchView;
    }

}
