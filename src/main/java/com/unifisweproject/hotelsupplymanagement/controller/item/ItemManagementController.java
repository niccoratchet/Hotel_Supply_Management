package com.unifisweproject.hotelsupplymanagement.controller.item;

import com.unifisweproject.hotelsupplymanagement.FXMLWindowLoader;
import com.unifisweproject.hotelsupplymanagement.controller.DataManagementController;
import com.unifisweproject.hotelsupplymanagement.model.item.Item;
import com.unifisweproject.hotelsupplymanagement.model.item.ItemManagementModel;
import com.unifisweproject.hotelsupplymanagement.window.item.ItemAddWindow;
import com.unifisweproject.hotelsupplymanagement.window.item.ItemDisplayWindow;
import com.unifisweproject.hotelsupplymanagement.window.item.ItemSearchWindow;
import com.unifisweproject.hotelsupplymanagement.main.HotelSupplyManagementMain;
import com.unifisweproject.hotelsupplymanagement.main.MainMenuWindow;
import com.unifisweproject.hotelsupplymanagement.view.item.ItemManagementView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;

public class ItemManagementController implements DataManagementController {

    private final ItemManagementModel model;
    private final MainMenuWindow mainMenuWindow;
    private boolean searchView = false;
    private ArrayList<Item> results = new ArrayList<>();
    private static ItemManagementController instance = null;        // Applicazione SingleTon per la finestra di gestione degli Item
    private ItemManagementView view;
    private ItemAddWindow itemAddWindow;
    private ItemDisplayWindow itemDisplayWindow;
    private ItemSearchWindow itemSearchWindow;
    private boolean isBadFormatted = false;                     // Variabile per gestire la correttezza o meno dei parametri inseriti nella ricerca di un articolo

    private ItemManagementController() {

        mainMenuWindow = MainMenuWindow.getInstance();
        model = ItemManagementModel.getInstance();

    }

    public static ItemManagementController getInstance() {         // Metodo per ottenere l'istanza della classe (SingleTon)

        if (instance == null) {
            instance = new ItemManagementController();
        }
        return instance;

    }

    @Override
    public void initializeRows()  {

        if (!mainMenuWindow.getIsNotFirstTimeLoad().get(0)) {
            try {
                model.loadFromDB();
                mainMenuWindow.getIsNotFirstTimeLoad().set(0, true);
            }
            catch (SQLException e) {
                System.err.println("Errore durante il riempimento della tabella");
            }
        }
        ObservableList<Item> itemRows = FXCollections.observableArrayList(model.getItemList());
        view.setRows(itemRows);

    }

    @Override
    public void createRow(ActionEvent event) {             // Metodo chiamato quando si preme il pulsante di aggiunta di un nuovo articolo

        try {
            if ("".equals(itemAddWindow.getNameField().getText()) || "".equals(itemAddWindow.getPriceField().getText()) ||          // Verifica che tutti i campi siano stati riempiti
                    "".equals(itemAddWindow.getAmountField().getText()) || itemAddWindow.getDatePicker().getValue() == null)
                throw new RuntimeException("Parametri mancanti");
            model.add(new Item(Integer.parseInt(itemAddWindow.getAmountField().getText()), Double.parseDouble(itemAddWindow.getPriceField().getText()),
                    itemAddWindow.getNameField().getText(), itemAddWindow.getDescriptionField().getText(), itemAddWindow.getDatePicker().getValue().toString()));
            updateTable();
            itemAddWindow.closeAddView(event);
        }
        catch (RuntimeException missingParameters) {
            HotelSupplyManagementMain.generateAlert(Alert.AlertType.ERROR, "Errore", "Parametri assenti", "Inserire il valore di tutti i dati obbligatori.");
        }

    }


    public void modifyRow(ActionEvent event, Item selectedItem) {

        try {
            if ("".equals(itemDisplayWindow.getNameField().getText()) || "".equals(itemDisplayWindow.getPriceField().getText()) ||          // Verifica che tutti i campi siano stati riempiti
                    "".equals(itemDisplayWindow.getAmountField().getText()) || itemDisplayWindow.getDatePicker().getValue() == null)
                throw new RuntimeException("Parametri mancanti");
            selectedItem.setNome(itemDisplayWindow.getNameField().getText());
            selectedItem.setPrezzo(Double.parseDouble(itemDisplayWindow.getPriceField().getText()));
            selectedItem.setQuantita(Integer.parseInt(itemDisplayWindow.getAmountField().getText()));
            selectedItem.setData_inserimento(itemDisplayWindow.getDatePicker().getValue().toString());
            selectedItem.setDescrizione(itemDisplayWindow.getDescriptionField().getText());
            model.modify(selectedItem);
            HotelSupplyManagementMain.generateAlert(Alert.AlertType.INFORMATION, "Avviso", "Modifica prodotto",
                    "Modifica del prodotto " + selectedItem.getNome() + " effettuata con successo");
            updateTable();
            itemDisplayWindow.closeItemView(event);
        }
        catch (RuntimeException missingParameters) {
            HotelSupplyManagementMain.generateAlert(Alert.AlertType.ERROR, "Errore", "Parametri mancanti", missingParameters.getMessage());
        }

    }

    @Override
    public void updateTable() {

        if(searchView) {
            ObservableList<Item> searchResultRows = FXCollections.observableArrayList(results);
            view.setRows(searchResultRows);
        }
        else {
            ObservableList<Item> itemRows = FXCollections.observableArrayList(model.getItemList());
            view.setRows(itemRows);
        }

    }

    @Override
    public void deleteRow(Object toBeDeleted) {

        Item selectedItem = (Item) toBeDeleted;
        if (HotelSupplyManagementMain.displayConfirmationAlert("Attenzione", "Rimozione prodotto", "Sicuro di procedere con l'eliminazione del prodotto dalla banca dati?")) {
            model.delete(selectedItem);
            if (searchView)
                results.remove(selectedItem);                   // Se sto visualizzando una ricerca, effettuo gli aggiornamenti anche su questa view
            updateTable();
        }

    }

    public Item getSearchFilters() {            // Metodo per determinare per quali parametri ricerca un articolo: si crea un Item fittizio con i parametri inseriti

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

    @Override
    public void searchRow(ActionEvent event) {         // Metodo per cercare un articolo dato l'oggetto Item fittizio creato con i parametri inseriti

        Item toBeSearched = getSearchFilters();
        if (toBeSearched != null) {
            results.clear();
            try {
                results = HotelSupplyManagementMain.castArrayList(model.search(toBeSearched));             // effettuo il cast della lista
                int numberOfResults = results.size();
                searchView = true;
                ObservableList<Item> searchResultRows = FXCollections.observableArrayList(results);
                view.setRows(searchResultRows);
                HotelSupplyManagementMain.generateAlert(Alert.AlertType.INFORMATION, "Avviso", "Risultato ricerca",
                        "La ricerca ha restituito " + numberOfResults + " risultati");
                view.enableBackButton();
            }
            catch (NullPointerException e) {                            // Serve a gestire il caso in cui si lascino vuoti i campi di ricerca selezionati
                HotelSupplyManagementMain.generateAlert(Alert.AlertType.ERROR, "Errore", "Errore",
                        "Parametri di ricerca vuoti: una volta spuntati inserire almeno un valore");
            }
            itemSearchWindow.closeSearchView(event);
        }
        else if(isBadFormatted) {
            HotelSupplyManagementMain.generateAlert(Alert.AlertType.ERROR, "Errore", "Errore di formattazione", "Valore del parametro 'Codice articolo' non valido. \nRiprovare.");
        }
        else {
            HotelSupplyManagementMain.generateAlert(Alert.AlertType.ERROR, "Errore", "Parametri assenti", "Hai spuntato dei parametri ma non hai inserito i valori corrispondenti. \nRiprovare.");
        }

    }

    @Override
    public void displayView(ActionEvent event) {            // Metodo per visualizzare la finestra di gestione degli articoli

        try {
            view = new ItemManagementView();                                                                                                                                            // Apertura della finestra di gestione degli Item
            FXMLWindowLoader.loadFXML(getClass().getResource("/com/unifisweproject/hotelsupplymanagement/item/ItemManagementWindow.fxml"),
                    view, true, event, "Gestione prodotti", false);
        }
        catch (IOException e) {
            System.err.println("Errore durante il caricamento di ItemManagementWindow.fxml: " + e.getMessage());
        }
        initializeRows();

    }

    @Override
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

    @Override
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

    @Override
    public void displayRowView(ActionEvent event, Object toBeDisplayed) {

        try {
            itemDisplayWindow = new ItemDisplayWindow((Item) toBeDisplayed);
            FXMLWindowLoader.loadFXML(getClass().getResource("/com/unifisweproject/hotelsupplymanagement/item/ItemDisplayWindow.fxml"),
                    itemDisplayWindow, false, event, (((Item) toBeDisplayed).getNome()), false);
        }
        catch (IOException e) {
            System.err.println("Errore durante l'apertura del file ItemDisplayWindow.fxml: " + e.getMessage());
        }

    }

    @Override
    public void openDifferentManagement(ActionEvent event) {

        Stage stage = (Stage) ((MenuItem) event.getSource()).getParentPopup().getOwnerWindow();
        String menuName = ((MenuItem) event.getSource()).getText();                                         // Ottengo il nome del menuItem premuto
        mainMenuWindow.getStageFromMenuBar(event, stage, menuName);

    }

    @Override
    public void handleActionEvent(ActionEvent event) {

        if (event.getSource() instanceof Button button) {
            switch (button.getId()) {
                case "addButton" -> displayAddView();
                case "searchButton" -> displaySearchView(event);
                case "backButton" -> view.exitSearch();
                case "modifyButton" -> {
                    Item selectedItem = view.getItemTable().getSelectionModel().getSelectedItem();
                    displayRowView(null, selectedItem);
                }
                case "deleteButton" -> {
                    Item selectedItem = view.getItemTable().getSelectionModel().getSelectedItem();
                    deleteRow(selectedItem);
                }
            }
        }
        else if (event.getSource() instanceof MenuItem menuItem) {
            if (menuItem.getId().equals("viewItemMenu") || menuItem.getId().equals("viewDeleteItemMenu")) {
                Item selectedItem = view.getItemTable().getSelectionModel().getSelectedItem();
                if (Objects.nonNull(selectedItem)) {
                    if (menuItem.getId().equals("viewItemMenu"))
                        displayRowView(null, selectedItem);
                    else
                        deleteRow(selectedItem);
                }
            }
            else
                openDifferentManagement(event);
        }

    }

    public void handleMouseEvent(Item selectedItem) {
        displayRowView(null, selectedItem);
    }

    @Override
    public void setSearchView(boolean searchView) {
        this.searchView = searchView;
    }

}
