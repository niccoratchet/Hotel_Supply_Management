package com.unifisweproject.hotelsupplymanagement.supplier;

import com.unifisweproject.hotelsupplymanagement.FXMLWindowLoader;
import com.unifisweproject.hotelsupplymanagement.company.CompanyDetailsAddWindow;
import com.unifisweproject.hotelsupplymanagement.company.CompanyDetailsManagement;
import com.unifisweproject.hotelsupplymanagement.company.CompanyDetailsModifyWindow;
import com.unifisweproject.hotelsupplymanagement.contact.ContactDetailsManagement;
import com.unifisweproject.hotelsupplymanagement.contact.ContactDetailsModifyWindow;
import com.unifisweproject.hotelsupplymanagement.contact.ContactDetailsAddWindow;
import com.unifisweproject.hotelsupplymanagement.main.HotelSupplyManagementMain;
import com.unifisweproject.hotelsupplymanagement.main.MainMenuWindowController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Objects;

public class SupplierManagementController {

    private static SupplierManagementController instance = null;
    private boolean searchView = false;
    private final MainMenuWindowController mainMenuWindowController;
    private final SupplierManagement supplierManagement;
    private ArrayList<Supplier> results = new ArrayList<>();
    private SupplierManagementView supplierManagementView;
    private SupplierAddWindow supplierAddWindow;
    private SupplierDisplayWindow supplierDisplayWindow;
    private SupplierSearchWindow supplierSearchWindow;
    private CompanyDetailsModifyWindow companyDetailsModifyWindow = null;
    private ContactDetailsAddWindow contactDetailsAddWindow;
    private ContactDetailsModifyWindow contactDetailsModifyWindow = null;
    private final ArrayList<String> companyDetails = new ArrayList<>();
    private final ArrayList<String> contactDetails = new ArrayList<>();
    private Supplier displayedSupplier = null;
    private boolean isBadFormatted = false;

    private SupplierManagementController() {// Costruttore privato per evitare la creazione di nuove istanze (SingleTon)

        mainMenuWindowController = MainMenuWindowController.getInstance();
        supplierManagement = SupplierManagement.getInstance();

    }

    public static SupplierManagementController getInstance() {          // Metodo per ottenere l'istanza della classe (SingleTon)

        if (instance == null) {
            instance = new SupplierManagementController();
        }
        return instance;

    }

    public void initializeRows()  {

        if (!mainMenuWindowController.getIsNotFirstTimeLoad().get(2)) {
            try {
                supplierManagement.loadFromDB();
                mainMenuWindowController.getIsNotFirstTimeLoad().set(2, true);
            }
            catch (SQLException e) {
                System.err.println("Errore durante il riempimento della tabella");
            }
        }
        ObservableList<Supplier> supplierRows = FXCollections.observableArrayList(supplierManagement.getSupplierList());
        supplierManagementView.setRows(supplierRows);

    }

    public void createRow(ActionEvent event)  {

        LocalDate date = supplierAddWindow.getDatePicker().getValue();
        try {
            if (date == null)
                throw new RuntimeException("Parametri mancanti");
            if (contactDetails.isEmpty())
                throw new RuntimeException("Dati di contatto mancanti, inserire i dati nell'apposita sezione e premere 'Conferma modifiche'");
            Supplier newSupplier = new Supplier(date.toString(), companyDetails.get(0), companyDetails.get(1), contactDetails.get(0), contactDetails.get(1), contactDetails.get(2));
            ContactDetailsManagement.executeAddQuery(newSupplier.getIndirizzo(), newSupplier.getCivico(), newSupplier.getCAP(),
                    contactDetailsAddWindow.getLocationField().getText(), contactDetailsAddWindow.getProvinceField().getText(),
                    contactDetailsAddWindow.getPhoneNumberField().getText(), contactDetailsAddWindow.getMailField().getText());         // Inserimento dei dati di contatto nel DB attraverso la classe ContactDetailsManagement
            if (newSupplier.getP_IVA() != null && newSupplier.getRagione_sociale() != null)
                CompanyDetailsManagement.executeAddQuery(newSupplier.getP_IVA(), newSupplier.getRagione_sociale());         // Inserimento dei dati aziendali nel DB attraverso la classe CompanyDetailsManagement
            supplierManagement.add(newSupplier);
            updateTable();
            companyDetails.clear();
            contactDetails.clear();
            supplierAddWindow.closeAddView(event);
        }
        catch (RuntimeException missingParameters) {
            HotelSupplyManagementMain.generateAlert(Alert.AlertType.ERROR, "Errore", "Parametri assenti", "Inserire il valore di tutti i dati obbligatori.");
            System.err.println("Parametri mancanti: " + missingParameters.getMessage());
        }

    }


    public void modifyRow(ActionEvent event, Supplier displayedSupplier) {

        modifyCompanyAndContactDetails(displayedSupplier);
        try {
            if (supplierDisplayWindow.getDatePicker().getValue() == null)
                throw new RuntimeException("Parametri mancanti");
            displayedSupplier.setData_inserimento(supplierDisplayWindow.getDatePicker().getValue().toString());
            supplierManagement.modify(displayedSupplier);
            HotelSupplyManagementMain.generateAlert(Alert.AlertType.INFORMATION, "Avviso", "Modifica prodotto",
                    "Modifica del prodotto effettuata con successo");
            createConfirmedModifyAlert();
            updateTable();
            supplierDisplayWindow.closeSupplierView(event);
            contactDetailsModifyWindow = null;
            companyDetailsModifyWindow = null;
        }
        catch (RuntimeException missingParameters) {
            HotelSupplyManagementMain.generateAlert(Alert.AlertType.ERROR, "Errore", "Parametri mancanti", missingParameters.getMessage());
        }

    }

    public void modifyCompanyAndContactDetails(Supplier displayedSupplier) {

        if (companyDetailsModifyWindow != null) {
            CompanyDetailsManagement.executeUpdateQuery(companyDetailsModifyWindow.getP_IVAField().getText(), companyDetailsModifyWindow.getRagioneSocialeField().getText());
        }
        ContactDetailsManagement.executeUpdateQuery(contactDetails.get(0), contactDetails.get(2), contactDetails.get(1),
                contactDetailsModifyWindow.getLocationField().getText(), contactDetailsModifyWindow.getProvinceField().getText(),
                contactDetailsModifyWindow.getPhoneNumberField().getText(), contactDetailsModifyWindow.getMailField().getText(),
                displayedSupplier.getIndirizzo(), displayedSupplier.getCivico(), displayedSupplier.getCAP());         // Modifica dei dati di contatto nel DB attraverso la classe ContactDetailsManagement
        if (!contactDetails.isEmpty()) {
            displayedSupplier.setIndirizzo(contactDetails.get(0));
            displayedSupplier.setCivico(contactDetails.get(2));
            displayedSupplier.setCAP(contactDetails.get(1));
        }
        if (!companyDetails.isEmpty()) {
            displayedSupplier.setP_IVA(companyDetails.get(0));
            displayedSupplier.setRagione_sociale(companyDetails.get(1));
        }

    }

    public void setCompanyDetails(String P_IVA, String ragione_sociale) {

        companyDetails.clear();
        companyDetails.add(P_IVA);
        companyDetails.add(ragione_sociale);

    }

    public void setContactDetails(String address, String CAP, String civicNumber) {

        contactDetails.clear();
        contactDetails.add(address);
        contactDetails.add(CAP);
        contactDetails.add(civicNumber);

    }

    public void deleteRow(Supplier selectedSupplier) {

        if (HotelSupplyManagementMain.displayConfirmationAlert("Attenzione", "Rimozione fornitore", "Sicuro di procedere con l'eliminazione del fornitore dalla banca dati?")) {
            supplierManagement.delete(selectedSupplier);
            if (searchView)
                results.remove(selectedSupplier);                   // Se sto visualizzando una ricerca, effettuo gli aggiornamenti anche su questa view
            updateTable();
        }

    }

    public Supplier getSearchFilters() {

        TextField supplierCodeField = supplierSearchWindow.getSupplierCodeField();
        TextField PIVAField = supplierSearchWindow.getPivaField();
        TextField ragioneSocialeField = supplierSearchWindow.getRagioneSocialeField();
        TextField indirizzoField = supplierSearchWindow.getAddressField();
        TextField CAPField = supplierSearchWindow.getCapField();
        TextField civicNumberField = supplierSearchWindow.getCivicNumberField();
        Supplier searchSupplier = new Supplier(null, null, null, null, null, null);
        int i = 0;
        try {
            while (i < 6) {
                switch (i) {
                    case 0 -> {
                        if (!supplierCodeField.isDisabled())
                            if(! "".equals(supplierCodeField.getText()))
                                searchSupplier.setCodice_fornitore(Integer.parseInt(supplierCodeField.getText()));
                            else
                                return null;
                    }
                    case 1 -> {
                        if (!PIVAField.isDisabled())
                            if(! "".equals(PIVAField.getText()))
                                searchSupplier.setP_IVA(PIVAField.getText());
                            else
                                return null;
                    }
                    case 2 -> {
                        if (!ragioneSocialeField.isDisabled())
                            if(! "".equals(ragioneSocialeField.getText()))
                                searchSupplier.setRagione_sociale(ragioneSocialeField.getText());
                            else
                                return null;
                    }
                    case 3 -> {
                        if (!indirizzoField.isDisabled())
                            if (! "".equals(indirizzoField.getText()))
                                searchSupplier.setIndirizzo(indirizzoField.getText());
                            else
                                return null;
                    }
                    case 4 -> {
                        if (!CAPField.isDisabled())
                            if(! "".equals(CAPField.getText()))
                                searchSupplier.setCAP(CAPField.getText());
                            else
                                return null;
                    }
                    case 5 -> {
                        if (!civicNumberField.isDisabled())
                            if (! "".equals(civicNumberField.getText()))
                                searchSupplier.setCivico(civicNumberField.getText());
                            else
                                return null;
                    }
                }
                i++;
            }
            return searchSupplier;
        }
        catch (NumberFormatException e) {
            isBadFormatted = true;
            return null;
        }

    }

    public void searchRow(ActionEvent event) {

        Supplier toBeSearched = getSearchFilters();
        if(toBeSearched != null){
            results.clear();
            try {
                results = HotelSupplyManagementMain.castArrayList(supplierManagement.search(toBeSearched));             // effettuo il cast della lista
                int numberOfResults = results.size();
                searchView = true;
                ObservableList<Supplier> searchResultRows = FXCollections.observableArrayList(results);
                supplierManagementView.setRows(searchResultRows);
                HotelSupplyManagementMain.generateAlert(Alert.AlertType.INFORMATION, "Avviso", "Risultato ricerca",
                        "La ricerca ha restituito " + numberOfResults + " risultati");
                supplierManagementView.enableBackButton();
            }
            catch (NullPointerException e) {                            // Serve a gestire il caso in cui si lascino vuoti i campi di ricerca selezionati
                HotelSupplyManagementMain.generateAlert(Alert.AlertType.ERROR, "Errore", "Errore",
                        "Parametri di ricerca vuoti: una volta spuntati inserire almeno un valore");
            }
            supplierSearchWindow.closeSearchView(event);
        }
        else if(isBadFormatted) {
            HotelSupplyManagementMain.generateAlert(Alert.AlertType.ERROR, "Errore", "Errore di formattazione", "Valore del parametro 'Codice articolo' non valido. \nRiprovare.");
        }
        else {
            HotelSupplyManagementMain.generateAlert(Alert.AlertType.ERROR, "Errore", "Parametri assenti", "Hai spuntato dei parametri ma non hai inserito i valori corrispondenti. \nRiprovare.");
        }

    }

    public void updateTable() {

        if(searchView) {
            ObservableList<Supplier> searchResultRows = FXCollections.observableArrayList(results);
            supplierManagementView.setRows(searchResultRows);
        }
        else {
            ObservableList<Supplier> supplierRows = FXCollections.observableArrayList(supplierManagement.getSupplierList());
            supplierManagementView.setRows(supplierRows);
        }

    }

    public void displayView(ActionEvent event) {            // Metodo per visualizzare la finestra di gestione degli articoli

        try {
            supplierManagementView = new SupplierManagementView();                                                                                                                                            // Apertura della finestra di gestione degli Item
            FXMLWindowLoader.loadFXML(getClass().getResource("/com/unifisweproject/hotelsupplymanagement/supplier/SupplierManagementWindow.fxml"),
                    supplierManagementView, true, event, "Gestione fornitori", false);
        }
        catch (IOException e) {
            System.err.println("Errore durante il caricamento di SupplierManagementWindow.fxml: " + e.getMessage());
        }
        initializeRows();

    }

    public void displaySearchView(ActionEvent event) {

        try {
            supplierSearchWindow = new SupplierSearchWindow();
            FXMLWindowLoader.loadFXML(getClass().getResource("/com/unifisweproject/hotelsupplymanagement/supplier/SupplierSearchWindow.fxml"),
                    supplierSearchWindow, false, event, "Ricerca fornitore", false);
        }
        catch(IOException e) {
            System.out.println("Errore durante il caricamento di SearchSupplierView: " + e);
        }

    }

    public void displayAddView() {

        try {
            supplierAddWindow = new SupplierAddWindow();
            FXMLWindowLoader.loadFXML(getClass().getResource("/com/unifisweproject/hotelsupplymanagement/supplier/SupplierAddWindow.fxml"),
                    supplierAddWindow, false, null, "Aggiungi fornitore", false);
        }
        catch (IOException e) {
            System.err.println("Errore durante il caricamento di SupplierAddWindow.fxml: " + e.getMessage());
        }

    }

    public void displayRowView(ActionEvent event, Supplier selectedSupplier) {

        try {
            this.displayedSupplier = selectedSupplier;
            setContactDetails(selectedSupplier.getIndirizzo(), selectedSupplier.getCAP(), selectedSupplier.getCivico());
            supplierDisplayWindow = new SupplierDisplayWindow(selectedSupplier);
            FXMLWindowLoader.loadFXML(getClass().getResource("/com/unifisweproject/hotelsupplymanagement/supplier/SupplierDisplayWindow.fxml"),
                    supplierDisplayWindow, false, event, selectedSupplier.getRagione_sociale(), false);
        }
        catch (IOException e) {
            System.err.println("Errore durante l'apertura del file SupplierDisplayWindow.fxml: " + e.getMessage());
        }

    }

    public void loadAddContactDetailsView(ActionEvent event) {

        try {
            contactDetailsAddWindow = new ContactDetailsAddWindow(this);
            FXMLWindowLoader.loadFXML(getClass().getResource("/com/unifisweproject/hotelsupplymanagement/contact/ContactDetailsAddWindow.fxml"),
                    contactDetailsAddWindow, false, event, "Aggiungi info su indirizzo e recapito", false);
        }
        catch (IOException e) {
            System.err.println("Errore durante il caricamento di ContactDetailsAddWindow.fxml: " + e.getMessage());
        }

    }

    public void loadAddCompanyDetailsView(ActionEvent event) {

        try {
            CompanyDetailsAddWindow companyDetailsAddWindow = new CompanyDetailsAddWindow(this);
            FXMLWindowLoader.loadFXML(getClass().getResource("/com/unifisweproject/hotelsupplymanagement/company/CompanyDetailsAddWindow.fxml"),
                    companyDetailsAddWindow, false, event, "Aggiungi info sull'azienda", false);
        }
        catch (IOException e) {
            System.err.println("Errore durante il caricamento di CompanyDetailsAddWindow.fxml: " + e.getMessage());
        }

    }

    public void loadModifyContactDetailsView(ActionEvent event) {

        try {
            contactDetailsModifyWindow = new ContactDetailsModifyWindow(this);
            FXMLWindowLoader.loadFXML(getClass().getResource("/com/unifisweproject/hotelsupplymanagement/contact/ContactDetailsModifyWindow.fxml"),
                    contactDetailsModifyWindow, false, event, "Modifica info su indirizzo e recapito", false);
        }
        catch (IOException e) {
            System.err.println("Errore durante il caricamento di ContactDetailsModifyWindow.fxml: " + e.getMessage());
        }

    }

    public void loadModifyCompanyDetailsView(ActionEvent event) {

        try {
            companyDetailsModifyWindow = new CompanyDetailsModifyWindow(this);
            FXMLWindowLoader.loadFXML(getClass().getResource("/com/unifisweproject/hotelsupplymanagement/company/CompanyDetailsModifyWindow.fxml"),
                    companyDetailsModifyWindow, false, event, "Modifica info sull'azienda", false);
        }
        catch (IOException e) {
            System.err.println("Errore durante il caricamento di CompanyDetailsModifyWindow.fxml: " + e.getMessage());
        }

    }

    public void openDifferentManagement(ActionEvent event) {

        Stage stage = (Stage) ((MenuItem) event.getSource()).getParentPopup().getOwnerWindow();
        String menuName = ((MenuItem) event.getSource()).getText();
        mainMenuWindowController.getStageFromMenuBar(event, stage, menuName);

    }

    public void handleActionEvent(ActionEvent event) {

        if (event.getSource() instanceof Button button) {
            switch (button.getId()) {
                case "addButton" -> displayAddView();
                case "searchButton" -> displaySearchView(event);
                case "backButton" -> supplierManagementView.exitSearch();
                case "modifyButton" -> {
                    Supplier selectedSupplier = supplierManagementView.getSupplierTable().getSelectionModel().getSelectedItem();
                    displayRowView(null, selectedSupplier);
                }
                case "deleteButton" -> {
                    Supplier selectedSupplier = supplierManagementView.getSupplierTable().getSelectionModel().getSelectedItem();
                    deleteRow(selectedSupplier);
                }
                case "addContactButton" -> loadAddContactDetailsView(event);
                case "addCompanyDataButton" -> loadAddCompanyDetailsView(event);
            }
        }
        else if (event.getSource() instanceof MenuItem menuItem) {
            if (menuItem.getId().equals("viewSupplierMenu") || menuItem.getId().equals("viewDeleteSupplierMenu")) {
                Supplier selectedSupplier = supplierManagementView.getSupplierTable().getSelectionModel().getSelectedItem();
                if (Objects.nonNull(selectedSupplier)) {
                    if (menuItem.getId().equals("viewSupplierMenu"))
                        displayRowView(null, selectedSupplier);
                    else
                        deleteRow(selectedSupplier);
                }
            }
            else
                openDifferentManagement(event);
        }

    }

    public void handleMouseEvent(Supplier selectedSupplier) {
        displayRowView(null, selectedSupplier);
    }

    public void setSearchView(boolean searchView) {
        this.searchView = searchView;
    }

    public void createConfirmedModifyAlert() {
        HotelSupplyManagementMain.generateAlert(Alert.AlertType.INFORMATION, "Avviso", "Modifica fornitore", "Modifica del fornitore effettuata con successo");
    }

    public Supplier getDisplayedSupplier() {
        return displayedSupplier;
    }

}
