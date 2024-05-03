package com.unifisweproject.hotelsupplymanagement.controller.supply;

import com.unifisweproject.hotelsupplymanagement.FXMLWindowLoader;
import com.unifisweproject.hotelsupplymanagement.controller.DataManagementController;
import com.unifisweproject.hotelsupplymanagement.model.item.Item;
import com.unifisweproject.hotelsupplymanagement.model.item.ItemManagementModel;
import com.unifisweproject.hotelsupplymanagement.itemsInOrderAndSupply.ListOfItemsWindow;
import com.unifisweproject.hotelsupplymanagement.main.HotelSupplyManagementMain;
import com.unifisweproject.hotelsupplymanagement.itemsInOrderAndSupply.ItemsInSupplyManagement;
import com.unifisweproject.hotelsupplymanagement.main.MainMenuWindow;
import com.unifisweproject.hotelsupplymanagement.model.supply.SupplyManagementModel;
import com.unifisweproject.hotelsupplymanagement.model.supply.Supply;
import com.unifisweproject.hotelsupplymanagement.view.supply.SupplyManagementView;
import com.unifisweproject.hotelsupplymanagement.window.supply.SupplyAddWindow;
import com.unifisweproject.hotelsupplymanagement.window.supply.SupplyDisplayWindow;
import com.unifisweproject.hotelsupplymanagement.window.supply.SupplySearchWindow;
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

public class SupplyManagementController implements DataManagementController {

    private static final SupplyManagementController instance = new SupplyManagementController();        // Singleton per la finestra di gestione delle forniture

    private boolean searchView = false;
    private SupplyManagementView view;
    private final MainMenuWindow mainMenuWindow;
    private final SupplyManagementModel model;
    private ArrayList<Supply> results = new ArrayList<>();
    private ResultSet resultSet;
    private ListOfItemsWindow listOfItemsWindow;
    private SupplySearchWindow supplySearchWindow;
    private SupplyAddWindow supplyAddWindow;
    private SupplyDisplayWindow supplyDisplayWindow;
    private ItemsInSupplyManagement itemsInSupplyManagement;
    private final ArrayList<Integer> newAmount = new ArrayList<>();             // Lista per memorizzare le nuove quantita degli item in un nuovo ordine

    private SupplyManagementController() {               // Costruttore privato per evitare la creazione di nuove istanze (SingleTon)

        mainMenuWindow = MainMenuWindow.getInstance();
        model = SupplyManagementModel.getInstance();

    }

    public static SupplyManagementController getInstance() {          // Metodo per ottenere l'istanza della classe (SingleTon)
        return instance;
    }

    @Override
    public void initializeRows()  {

        if(!mainMenuWindow.getIsNotFirstTimeLoad().get(4)) {
            try {
                model.loadFromDB();
                mainMenuWindow.getIsNotFirstTimeLoad().set(4, true);
            }
            catch (SQLException e) {
                System.err.println("Errore durante il riempimento della tabella");
            }
        }
        ObservableList<Supply> supplyRows = FXCollections.observableArrayList(model.getSupplyList());
        view.setRows(supplyRows);

    }

    public ObservableList<String> getSupplierList() {           // Metodo per ottenere i dati dei fornitori dal DB e inserirli nella ChoiceBox della finestra OrderAddWindow

        resultSet = model.getSupplierList();
        ObservableList<String> supplierList = FXCollections.observableArrayList();
        try {
            while (resultSet.next()) {
                supplierList.add(resultSet.getString("Ragione_Sociale"));
            }
            return supplierList;
        }
        catch (SQLException e) {
            System.err.println("Errore durante l'estrapolazione dei dati dei fornitori: " + e.getMessage());
            return null;
        }

    }

    @Override
    public void createRow(ActionEvent event) {              // Metodo per la creazione di una nuova fornitura fatto partire dalla finestra SupplyAddWindow

        int supplierCode = 0;
        String selectedSupplier = supplyAddWindow.getSupplierList().getValue();
        LocalDate supplyDate = supplyAddWindow.getDatePicker().getValue();
        try {
            if ((supplyDate == null) || (itemsInSupplyManagement.getNumberOfItems() == 0))
                throw new RuntimeException("Parametri mancanti");
            try {
                resultSet = model.getSupplierList();
                while (resultSet.next()) {
                    if (selectedSupplier.equals(resultSet.getString("Ragione_Sociale"))) {
                        supplierCode = resultSet.getInt("Codice_fornitore");
                        break;
                    }
                }
                for (int i = 0; i < itemsInSupplyManagement.getNumberOfItems(); i++) {
                    model.add(new Supply(supplierCode, itemsInSupplyManagement.getCodice_Articolo(i), supplyDate.toString(), itemsInSupplyManagement.getQuantita(i),
                            itemsInSupplyManagement.getPrezzo(i)));
                }
                itemsInSupplyManagement.updateItemAmount(newAmount);
                updateTable();
                itemsInSupplyManagement = null;                                                                 // Una volta terminata l'aggiunta, l'oggetto ItemsInOrderManagement usato per la creazione dell'ordine viene messo a null per poter essere di nuovo inizializzato
                supplyAddWindow.closeAddView(event);
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

    public void addItemToSupply(ActionEvent event) {               // Metodo per aggiungere un articolo a una fornitura e visualizzare in SupplyAddWindow, fatto partire da ListOfItemsWindow

        Item selectedItem = listOfItemsWindow.getItemTableView().getSelectionModel().getSelectedItem();
        String quantity = listOfItemsWindow.getQuantityField().getText();
        if (quantity.equals("0")) {
            HotelSupplyManagementMain.generateAlert(Alert.AlertType.ERROR, "Errore", "Quantità errata",
                    "Non e' possibile aggiungere un articolo con quantita' pari a 0");
            return;
        }
        Item itemToAdd = new Item(selectedItem.getCodice_articolo(), Integer.parseInt(quantity), Double.parseDouble(listOfItemsWindow.getPriceField().getText())*Integer.parseInt(quantity),
                selectedItem.getNome(), selectedItem.getDescrizione(), selectedItem.getData_inserimento());
        supplyAddWindow.getItemList().add(itemToAdd);
        newAmount.add(selectedItem.getQuantita() + Integer.parseInt(quantity));
        itemsInSupplyManagement.addCodice_Articolo(selectedItem.getCodice_articolo());
        itemsInSupplyManagement.addPrezzo(Double.parseDouble(listOfItemsWindow.getPriceField().getText())*Integer.parseInt(quantity));
        itemsInSupplyManagement.addQuantita(Integer.parseInt(quantity));
        ObservableList<Item> supplyRows = FXCollections.observableArrayList(supplyAddWindow.getItemList());
        supplyAddWindow.setRows(supplyRows);
        listOfItemsWindow.closeAddItem(event);

    }

    public void initializeListOfItemsTable()  {             // Metodo per inizializzare la tabella degli articoli da aggiungere in un ordine in ListOfItemsWindow

        try {
            if (!mainMenuWindow.getIsNotFirstTimeLoad().get(0)) {
                ItemManagementModel.getInstance().loadFromDB();                          // Se nel corso del programma non è stata ancora caricata la lista degli articoli, si richiama il metodo loadFromDB
                mainMenuWindow.getIsNotFirstTimeLoad().set(0, true);
            }
        }
        catch (SQLException e) {
            System.err.println("Errore durante il riempimento della tabella");
        }
        ObservableList<Item> itemRows = FXCollections.observableArrayList(ItemManagementModel.getInstance().getItemList());
        listOfItemsWindow.setRows(itemRows);

    }

    @Override
    public void updateTable() {

        ObservableList<Supply> supplyRows;
        if(searchView) {
            supplyRows = FXCollections.observableArrayList(results);
        }
        else {
            supplyRows = FXCollections.observableArrayList(model.getSupplyList());
        }
        view.setRows(supplyRows);

    }

    public void displaySupplyInfo() throws SQLException {                // Metodo per aggiungere gli articoli e le informazioni di un ordine nella finestra OrderDisplayWindow

        itemsInSupplyManagement = new ItemsInSupplyManagement();
        Supply displayedSupply = supplyDisplayWindow.getDisplayedSupply();
        supplyDisplayWindow.getCodeLabel().setText("Dati fornitura n°" + displayedSupply.getCodice_fornitura());
        supplyDisplayWindow.getDatePicker().setValue(LocalDate.parse(displayedSupply.getData_fornitura()));
        resultSet = model.getSupplierList();
        while (resultSet.next()) {
            if (displayedSupply.getCodice_fornitore() == resultSet.getInt("Codice_fornitore")) {            // Cerco il nome del fornitore corrispondente al codice fornitore della fornitura
                supplyDisplayWindow.getSupplierList().setValue(resultSet.getString("Ragione_Sociale"));
                break;
            }
        }
        ObservableList<Item> items = FXCollections.observableArrayList(itemsInSupplyManagement.getItemsFromASupply(displayedSupply));
        supplyDisplayWindow.getItemList().setAll(items);
        ObservableList<Item> itemRows = FXCollections.observableArrayList(supplyDisplayWindow.getItemList());
        supplyDisplayWindow.setRows(itemRows);
        itemsInSupplyManagement = null;

    }

    @Override
    public void deleteRow(Object toBeDeleted) {

        if (HotelSupplyManagementMain.displayConfirmationAlert("Attenzione","Rimozione fornitura", "Sicuro di procedere con l'eliminazione della fornitura dalla banca dati?")) {
            Supply selectedSupply = (Supply) toBeDeleted;
            model.delete(toBeDeleted);
            if (searchView)
                results.remove(selectedSupply);                   // Se sto visualizzando una ricerca, effettuo gli aggiornamenti anche su questa view
            updateTable();
        }

    }

    @Override
    public void searchRow(ActionEvent event) {

        Supply toBeSearched = getSearchFilters();
        if (toBeSearched != null) {
            results.clear();
            try {
                results = HotelSupplyManagementMain.castArrayList(model.search(toBeSearched));             // effettuo il cast della lista
                int numberOfResults = results.size();
                searchView = true;
                ObservableList<Supply> searchResultRows = FXCollections.observableArrayList(results);
                view.setRows(searchResultRows);
                HotelSupplyManagementMain.generateAlert(Alert.AlertType.INFORMATION, "Completata", "Risultato ricerca",
                        "La ricerca ha restituito " + numberOfResults + " risultati");
                view.enableBackButton();
            }
            catch (NullPointerException e) {                            // Serve a gestire il caso in cui si lascino vuoti i campi di ricerca selezionati
                HotelSupplyManagementMain.generateAlert(Alert.AlertType.ERROR, "Errore", "Parametri mancanti",
                        "Inserire almeno un parametro di ricerca");
            }
            SupplySearchWindow.closeView(event);
        }

    }


    public Supply getSearchFilters() throws NumberFormatException {

        Supply searchSupply = new Supply(-1, -1, -1, null, -1, -1);
        int i = 0;
        try {
            while (i < 3) {
                switch (i) {
                    case 0 -> {
                        if (!supplySearchWindow.getCompanyField().isDisabled())
                            if (supplySearchWindow.getCompanyField().getValue() != null) {
                                try {
                                    resultSet = model.getSupplierList();
                                    while (resultSet.next()) {
                                        if (supplySearchWindow.getCompanyField().getValue().equals(resultSet.getString("Ragione_Sociale"))) {
                                            searchSupply.setCodice_fornitore(resultSet.getInt("Codice_fornitore"));
                                            break;
                                        }
                                    }
                                } catch (SQLException e) {
                                    System.err.println("Errore durante l'estrapolazione del codice cliente: " + e.getMessage());
                                }
                            }
                    }
                    case 1 -> {
                        if (!supplySearchWindow.getItemCodeField().isDisabled() && !"".equals(supplySearchWindow.getItemCodeField().getText()))
                            searchSupply.setCodice_articolo(Integer.parseInt(supplySearchWindow.getItemCodeField().getText()));
                    }
                    case 2 -> {
                        if (!supplySearchWindow.getDatePicker().isDisabled() && supplySearchWindow.getDatePicker().getValue() != null)
                            searchSupply.setData_fornitura(supplySearchWindow.getDatePicker().getValue().toString());
                    }
                }
                i++;
            }
            return searchSupply;
        }
        catch (NumberFormatException e) {
            HotelSupplyManagementMain.generateAlert(Alert.AlertType.ERROR, "Errore", "Formato errato",
                    "Inserire un valore numerico nei campi prezzo e quantità");
            return null;
        }

    }

    @Override
    public void openDifferentManagement(ActionEvent event) {

        Stage stage = (Stage) ((MenuItem) event.getSource()).getParentPopup().getOwnerWindow();
        String menuName = ((MenuItem) event.getSource()).getText();
        mainMenuWindow.getStageFromMenuBar(event, stage, menuName);

    }

    @Override
    public void displayView(ActionEvent event) {

        try {
         view = new SupplyManagementView();
            FXMLWindowLoader.loadFXML(getClass().getResource("/com/unifisweproject/hotelsupplymanagement/supply/SupplyManagementWindow.fxml"),
                    view, true, event, "Gestione forniture", false);
        }
        catch (IOException e) {
            System.err.println("Errore durante l'apertura del file SupplyManagementView.fxml: " + e.getMessage());
        }
        initializeRows();
        if (Objects.isNull(itemsInSupplyManagement))
            itemsInSupplyManagement = new ItemsInSupplyManagement();

    }

    @Override
    public void displayRowView(ActionEvent event, Object toBeDisplayed) {

        try {
            supplyDisplayWindow = new SupplyDisplayWindow(((Supply) toBeDisplayed));
            FXMLWindowLoader.loadFXML(getClass().getResource("/com/unifisweproject/hotelsupplymanagement/supply/SupplyDisplayWindow.fxml"),
                    supplyDisplayWindow, false, event, "Visualizza fornitura", false);
        }
        catch (IOException e) {
            System.err.println("Errore durante l'apertura del file SupplyDisplayWindow.fxml: " + e.getMessage());
        }

    }

    @Override
    public void displaySearchView(ActionEvent event) {

        try {
            supplySearchWindow = new SupplySearchWindow();
            FXMLWindowLoader.loadFXML(getClass().getResource("/com/unifisweproject/hotelsupplymanagement/supply/SupplySearchWindow.fxml"),
                    supplySearchWindow, false, event, "Ricerca fornitura", false);
        }
        catch(IOException e) {
            System.out.println("Errore durante il caricamento di SearchSupplyView: " + e);
        }

    }

    @Override
    public void displayAddView() {

        try {
            supplyAddWindow = new SupplyAddWindow();
            FXMLWindowLoader.loadFXML(getClass().getResource("/com/unifisweproject/hotelsupplymanagement/supply/SupplyAddWindow.fxml"), supplyAddWindow,
                    false, null, "Aggiungi fornitura", false);
        }
        catch (IOException e) {
            System.err.println("Errore durante il caricamento del file SupplyAddWindow.fxml: "+e.getMessage());
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
        if (Objects.isNull(itemsInSupplyManagement))
            itemsInSupplyManagement = new ItemsInSupplyManagement();

    }

    @Override
    public void handleActionEvent(ActionEvent event) {

        if (event.getSource() instanceof Button button) {
            switch (button.getId()) {
                case "addButton" -> displayAddView();
                case "searchButton" -> displaySearchView(event);
                case "backButton" -> view.exitSearch();
                case "modifyButton" -> {
                    Supply selectedSupply = view.getSupplyTable().getSelectionModel().getSelectedItem();
                    displayRowView(null, selectedSupply);
                }
                case "deleteButton" -> {
                    Supply selectedSupply = view.getSupplyTable().getSelectionModel().getSelectedItem();
                    deleteRow(selectedSupply);
                }
                case "addItemButton" -> openListOfItemView(event);
            }
        }
        else if (event.getSource() instanceof MenuItem menuItem) {
            if (menuItem.getId().equals("viewSupplyMenu") || menuItem.getId().equals("viewDeleteSupplyMenu")) {
                Supply selectedSupply = view.getSupplyTable().getSelectionModel().getSelectedItem();
                if (Objects.nonNull(selectedSupply)) {
                    if (menuItem.getId().equals("viewSupplyMenu"))
                        displayRowView(null, selectedSupply);
                    else
                        deleteRow(selectedSupply);
                }
            }
            else
                openDifferentManagement(event);
        }

    }

    public void handleMouseEvent(Supply selectedSupply) {
        displayRowView(null, selectedSupply);
    }

    @Override
    public void setSearchView(boolean searchView) {
        this.searchView = searchView;
    }


}
