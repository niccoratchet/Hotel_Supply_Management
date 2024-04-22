package com.unifisweproject.hotelsupplymanagement.item;

import com.unifisweproject.hotelsupplymanagement.FXMLWindowLoader;
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
import java.util.ArrayList;
import java.util.Objects;

public class ItemManagementWindowController {

    private final ItemManagement itemManagement;
    private final MainMenuWindowController mainMenuWindowController;
    private boolean searchView = false;
    private ArrayList<Item> results = new ArrayList<>();
    private static ItemManagementWindowController instance = null;        // Applicazione SingleTon per la finestra di gestione degli Item
    private ItemManagementView itemManagementView;
    private ItemAddWindow itemAddWindow;
    private ItemDisplayWindow itemDisplayWindow;
    private ItemSearchWindow itemSearchWindow;
    private boolean isBadFormatted = false;                     // Variabile per gestire la correttezza o meno dei parametri inseriti nella ricerca di un articolo

    private ItemManagementWindowController() {

        mainMenuWindowController = MainMenuWindowController.getInstance();
        itemManagement = ItemManagement.getInstance();

    }

    public static ItemManagementWindowController getInstance() {         // Metodo per ottenere l'istanza della classe (SingleTon)

        if (instance == null) {
            instance = new ItemManagementWindowController();
        }
        return instance;

    }

    public void displayView(ActionEvent event) {            // Metodo per visualizzare la finestra di gestione degli articoli

        try {
            itemManagementView = new ItemManagementView();                                                                                                                                            // Apertura della finestra di gestione degli Item
            FXMLWindowLoader.loadFXML(getClass().getResource("/com/unifisweproject/hotelsupplymanagement/item/ItemManagementWindow.fxml"),
                    itemManagementView, true, event, "Gestione prodotti", false);
        }
        catch (IOException e) {
            System.err.println("Errore durante il caricamento di ItemManagementWindow.fxml: " + e.getMessage());
        }
        createRows();

    }

    public void createRows()  {

        if (!mainMenuWindowController.getIsNotFirstTimeLoad().get(0)) {
            ResultSet resultSet = itemManagement.getRows(true, null);
            try {
                while (resultSet.next()) {
                    Item item = new Item(resultSet.getInt(1), resultSet.getInt(4),
                            resultSet.getDouble(3), resultSet.getString(2), resultSet.getString(5), resultSet.getString(6));
                    itemManagement.getItemList().add(item);             // FIXME: Deve l'ItemManagement ad aggiungere l'Item alla lista
                }
                mainMenuWindowController.getIsNotFirstTimeLoad().set(0, true);
            }
            catch (SQLException e) {
                System.err.println("Errore durante il riempimento della tabella");
            }
        }
        ObservableList<Item> itemRows = FXCollections.observableArrayList(itemManagement.getItemList());
        itemManagementView.setItemRows(itemRows);

    }

    public void addRow(Item newItem) {

        newItem.setCodice_articolo(itemManagement.getNextItemCode() + 1);
        itemManagement.getItemList().add(newItem);
        itemManagement.add(newItem);
        updateTable();

    }

    public void createItem(ActionEvent event) {             // Metodo chiamato quando si preme il pulsante di aggiunta di un nuovo articolo

        try {
            if ("".equals(itemAddWindow.getNameField().getText()) || "".equals(itemAddWindow.getPriceField().getText()) ||          // Verifica che tutti i campi siano stati riempiti
                    "".equals(itemAddWindow.getAmountField().getText()) || itemAddWindow.getDatePicker().getValue() == null)
                throw new RuntimeException("Parametri mancanti");
            Item newItem = new Item(Integer.parseInt(itemAddWindow.getAmountField().getText()), Double.parseDouble(itemAddWindow.getPriceField().getText()),
                    itemAddWindow.getNameField().getText(), itemAddWindow.getDescriptionField().getText(), itemAddWindow.getDatePicker().getValue().toString());
            addRow(newItem);
            itemAddWindow.closeAddView(event);
        }
        catch (RuntimeException missingParameters) {
            HotelSupplyManagementMain.generateAlert(Alert.AlertType.ERROR, "Errore", "Parametri assenti", "Inserire il valore di tutti i dati obbligatori.");
        }

    }

    public void modifyItem(ActionEvent event, Item displayedItem) {

        try {
            if ("".equals(itemDisplayWindow.getNameField().getText()) || "".equals(itemDisplayWindow.getPriceField().getText()) ||          // Verifica che tutti i campi siano stati riempiti
                    "".equals(itemDisplayWindow.getAmountField().getText()) || itemDisplayWindow.getDatePicker().getValue() == null)
                throw new RuntimeException("Parametri mancanti");
            displayedItem.setNome(itemDisplayWindow.getNameField().getText());                                 // TODO: Forse meglio farlo nel modello?
            displayedItem.setPrezzo(Double.parseDouble(itemDisplayWindow.getPriceField().getText()));
            displayedItem.setQuantita(Integer.parseInt(itemDisplayWindow.getAmountField().getText()));
            displayedItem.setData_inserimento(itemDisplayWindow.getDatePicker().getValue().toString());
            displayedItem.setDescrizione(itemDisplayWindow.getDescriptionField().getText());
            modifyRow(displayedItem);
            itemDisplayWindow.closeItemView(event);
        }
        catch (RuntimeException missingParameters) {
            HotelSupplyManagementMain.generateAlert(Alert.AlertType.ERROR, "Errore", "Parametri mancanti", missingParameters.getMessage());
        }

    }

    public void modifyRow(Item toBeModified) {

        itemManagement.modify(toBeModified);
        HotelSupplyManagementMain.generateAlert(Alert.AlertType.INFORMATION, "Avviso", "Modifica prodotto",
                "Modifica del prodotto " + toBeModified.getNome() + " effettuata con successo");
        updateTable();

    }

    public void updateTable() {

        if(searchView) {
            ObservableList<Item> searchResultRows = FXCollections.observableArrayList(results);
            itemManagementView.setItemRows(searchResultRows);
        }
        else {
            ObservableList<Item> itemRows = FXCollections.observableArrayList(itemManagement.getItemList());
            itemManagementView.setItemRows(itemRows);
        }

    }

    public void deleteRow(Item selectedItem) {

        if (HotelSupplyManagementMain.displayConfirmationAlert("Attenzione", "Rimozione prodotto", "Sicuro di procedere con l'eliminazione del prodotto dalla banca dati?")) {
            itemManagement.getItemList().remove(selectedItem);          // TODO: La rimozione dee essere effettuata nel modello (ItemManagement)
            itemManagement.delete(selectedItem.getCodice_articolo());
            if (searchView)
                results.remove(selectedItem);                   // Se sto visualizzando una ricerca, effettuo gli aggiornamenti anche su questa view
            updateTable();
        }

    }

    public Item getSearchFilters() {

        Item searchItem = new Item(-1, -1, null, null, null);   // NOTA: è un oggetto item fittizio utile alla ricerca
        int i = 0;
        try {
            while (i < 6) {
                switch (i) {
                    case 0 -> {
                        if (!itemSearchWindow.getNameField().isDisabled())
                            if(! "".equals(itemSearchWindow.getNameField().getText()))
                                searchItem.setNome(itemSearchWindow.getNameField().getText());
                            else
                                return null;
                    }
                    case 1 -> {
                        if (!itemSearchWindow.getPriceField().isDisabled())
                            if(! "".equals(itemSearchWindow.getPriceField().getText()))
                                searchItem.setPrezzo(Double.parseDouble(itemSearchWindow.getPriceField().getText()));
                            else
                                return null;
                    }
                    case 2 -> {
                        if (!itemSearchWindow.getAmountField().isDisabled())
                            if(! "".equals(itemSearchWindow.getAmountField().getText()))
                                searchItem.setQuantita(Integer.parseInt(itemSearchWindow.getAmountField().getText()));
                            else
                                return null;
                    }
                    case 3 -> {
                        if (!itemSearchWindow.getDatePicker().isDisabled())
                            if(itemSearchWindow.getDatePicker().getValue() != null)
                                searchItem.setData_inserimento(itemSearchWindow.getDatePicker().getValue().toString());
                            else
                                return null;
                    }
                    case 4 -> {
                        if (!itemSearchWindow.getDescriptionField().isDisabled())
                            if(! "".equals(itemSearchWindow.getDescriptionField().getText()))
                                searchItem.setDescrizione(itemSearchWindow.getDescriptionField().getText());
                            else
                                return null;
                    }
                    case 5 -> {
                        if(!itemSearchWindow.getCodeField().isDisabled())
                            if(! "".equals(itemSearchWindow.getCodeField().getText()))
                                searchItem.setCodice_articolo(Integer.parseInt(itemSearchWindow.getCodeField().getText()));
                            else
                                return null;
                    }
                }
                i++;
            }
            return searchItem;
        }
        catch (NumberFormatException e) {
            isBadFormatted = true;
            return null;
        }

    }

    public void scanRows(ActionEvent event) {

        Item toBeSearched = getSearchFilters();
        if (toBeSearched != null) {
            searchRow(toBeSearched);
            itemSearchWindow.closeSearchView(event);
        }
        else if(isBadFormatted) {
            HotelSupplyManagementMain.generateAlert(Alert.AlertType.ERROR, "Errore", "Errore di formattazione", "Valore del parametro 'Codice articolo' non valido. \nRiprovare.");
        }
        else {
            HotelSupplyManagementMain.generateAlert(Alert.AlertType.ERROR, "Errore", "Parametri assenti", "Hai spuntato dei parametri ma non hai inserito i valori corrispondenti. \nRiprovare.");
        }
    }

    public void searchRow(Item toBeSearched) {

        results.clear();
        try {
            results = HotelSupplyManagementMain.castArrayList(itemManagement.search(toBeSearched));             // effettuo il cast della lista
            int numberOfResults = results.size();
            searchView = true;
            ObservableList<Item> searchResultRows = FXCollections.observableArrayList(results);
            itemManagementView.setItemRows(searchResultRows);
            HotelSupplyManagementMain.generateAlert(Alert.AlertType.INFORMATION, "Avviso", "Risultato ricerca",
                    "La ricerca ha restituito " + numberOfResults + " risultati");
            itemManagementView.enableBackButton();
        }
        catch (NullPointerException e) {                            // Serve a gestire il caso in cui si lascino vuoti i campi di ricerca selezionati
            HotelSupplyManagementMain.generateAlert(Alert.AlertType.ERROR, "Errore", "Errore",
                    "Parametri di ricerca vuoti: una volta spuntati inserire almeno un valore");
        }

    }

    public void displaySearchView(ActionEvent event) {

        try {
            itemSearchWindow = new ItemSearchWindow();
            FXMLWindowLoader.loadFXML(getClass().getResource("/com/unifisweproject/hotelsupplymanagement/item/ItemSearchWindow.fxml"),
                    itemSearchWindow, false, event, "Ricerca prodotto", false);
        }
        catch(IOException e) {
            System.out.println("Errore durante il caricamento di SearchItemView: " + e);
        }

    }

    public void displayAddView() {

        try {
            itemAddWindow = new ItemAddWindow();
            FXMLWindowLoader.loadFXML(getClass().getResource("/com/unifisweproject/hotelsupplymanagement/item/ItemAddWindow.fxml"),
                   itemAddWindow, false, null, "Aggiungi prodotto", false);
        }
        catch (IOException e) {
            System.err.println("Errore durante il caricamento di ItemAddWindow.fxml: " + e.getMessage());
        }

    }

    public void displayItemView(ActionEvent event, Item selectedItem) {

        try {
            itemDisplayWindow = new ItemDisplayWindow(selectedItem);
            FXMLWindowLoader.loadFXML(getClass().getResource("/com/unifisweproject/hotelsupplymanagement/item/ItemDisplayWindow.fxml"),
                    itemDisplayWindow, false, event, selectedItem.getNome(), false);
        }
        catch (IOException e) {
            System.err.println("Errore durante l'apertura del file ItemDisplayWindow.fxml: " + e.getMessage());
        }

    }

    public void openDifferentManagement(ActionEvent event) {

        Stage stage = (Stage) ((MenuItem) event.getSource()).getParentPopup().getOwnerWindow();
        String menuName = ((MenuItem) event.getSource()).getText();                                         // Ottengo il nome del menuItem premuto
        mainMenuWindowController.getStageFromMenuBar(event, stage, menuName);

    }

    public void handleActionEvent(ActionEvent event) {

        if (event.getSource() instanceof Button button) {
            switch (button.getId()) {
                case "addButton" -> displayAddView();
                case "searchButton" -> displaySearchView(event);
                case "backButton" -> itemManagementView.exitSearch();
                case "modifyButton" -> {
                    Item selectedItem = itemManagementView.getItemTable().getSelectionModel().getSelectedItem();
                    displayItemView(null, selectedItem);
                }
                case "deleteButton" -> {
                    Item selectedItem = itemManagementView.getItemTable().getSelectionModel().getSelectedItem();
                    deleteRow(selectedItem);
                }
            }
        }
        else if (event.getSource() instanceof MenuItem menuItem) {
            if (menuItem.getId().equals("viewItemMenu") || menuItem.getId().equals("viewDeleteItemMenu")) {
                Item selectedItem = itemManagementView.getItemTable().getSelectionModel().getSelectedItem();
                if (Objects.nonNull(selectedItem)) {
                    if (menuItem.getId().equals("viewItemMenu"))
                        displayItemView(null, selectedItem);
                    else
                        deleteRow(selectedItem);
                }
            }
            else
                openDifferentManagement(event);
        }

    }

    public void handleMouseEvent(Item selectedItem) {
        displayItemView(null, selectedItem);
    }

    public void setSearchView(boolean searchView) {
        this.searchView = searchView;
    }

}
