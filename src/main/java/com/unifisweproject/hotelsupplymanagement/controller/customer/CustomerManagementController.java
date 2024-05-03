package com.unifisweproject.hotelsupplymanagement.controller.customer;

import com.unifisweproject.hotelsupplymanagement.FXMLWindowLoader;
import com.unifisweproject.hotelsupplymanagement.company.CompanyDetailsAddWindow;
import com.unifisweproject.hotelsupplymanagement.company.CompanyDetailsManagement;
import com.unifisweproject.hotelsupplymanagement.company.CompanyDetailsModifyWindow;
import com.unifisweproject.hotelsupplymanagement.contact.ContactDetailsAddWindow;
import com.unifisweproject.hotelsupplymanagement.contact.ContactDetailsManagement;
import com.unifisweproject.hotelsupplymanagement.contact.ContactDetailsModifyWindow;
import com.unifisweproject.hotelsupplymanagement.controller.DataManagementController;
import com.unifisweproject.hotelsupplymanagement.model.customer.Customer;
import com.unifisweproject.hotelsupplymanagement.model.customer.CustomerManagementModel;
import com.unifisweproject.hotelsupplymanagement.window.customer.CustomerAddWindow;
import com.unifisweproject.hotelsupplymanagement.window.customer.CustomerDisplayWindow;
import com.unifisweproject.hotelsupplymanagement.window.customer.CustomerSearchWindow;
import com.unifisweproject.hotelsupplymanagement.main.HotelSupplyManagementMain;
import com.unifisweproject.hotelsupplymanagement.main.MainMenuWindow;
import com.unifisweproject.hotelsupplymanagement.view.customer.CustomerManagementView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Objects;

public class CustomerManagementController implements DataManagementController {

    private static CustomerManagementController instance = null;        // Singleton
    private boolean searchView = false;
    private final MainMenuWindow mainMenuWindow;
    private final CustomerManagementModel model;
    private ArrayList<Customer> results = new ArrayList<>();
    private CustomerManagementView view;
    private CustomerAddWindow customerAddWindow;
    private CustomerDisplayWindow customerDisplayWindow;
    private CustomerSearchWindow customerSearchWindow;
    private CompanyDetailsAddWindow companyDetailsAddWindow;
    private CompanyDetailsModifyWindow companyDetailsModifyWindow = null;
    private ContactDetailsAddWindow contactDetailsAddWindow;
    private ContactDetailsModifyWindow contactDetailsModifyWindow = null;
    private final ArrayList<String> companyDetails = new ArrayList<>();
    private final ArrayList<String> contactDetails = new ArrayList<>();
    private Customer displayedCustomer = null;                                             // Variabile per gestire il cliente visualizzato
    private boolean isBadFormatted = false;                     // Variabile per gestire la correttezza o meno dei parametri inseriti nella ricerca di un cliente

    private CustomerManagementController() {                              // Costruttore privato per Singleton

        mainMenuWindow = MainMenuWindow.getInstance();
        model = CustomerManagementModel.getInstance();

    }

    public static CustomerManagementController getInstance() {            // Metodo per ottenere l'istanza del Singleton

        if (instance == null) {
            instance = new CustomerManagementController();
        }
        return instance;

    }

    @Override
    public void initializeRows()  {

        if (!mainMenuWindow.getIsNotFirstTimeLoad().get(1)) {
            try {
                model.loadFromDB();
                mainMenuWindow.getIsNotFirstTimeLoad().set(1, true);
            }
            catch (SQLException e) {
                System.err.println("Errore durante il riempimento della tabella: " + e.getMessage());
            }
        }
        ObservableList<Customer> customerRows = FXCollections.observableArrayList(model.getCustomerList());
        view.setRows(customerRows);

    }

    @Override
    public void createRow(ActionEvent event)  {

        String customerName = customerAddWindow.getNameField().getText();
        String customerSurname = customerAddWindow.getSurnameField().getText();
        String customerFiscalCode = customerAddWindow.getFiscalCodeField().getText();
        String discount = customerAddWindow.getDiscountField().getText();
        LocalDate date = customerAddWindow.getDatePicker().getValue();
        try {
            if ("".equals(customerName) || "".equals(customerSurname) || "".equals(discount) || date == null || "".equals(customerFiscalCode))
                throw new RuntimeException("Parametri mancanti");
            if (contactDetails.isEmpty())
                throw new RuntimeException("Dati di contatto mancanti, inserire i dati nell'apposita sezione e premere 'Conferma modifiche'");
            Customer newCustomer = verifyIfCompanyInfoNull();
            ContactDetailsManagement.executeAddQuery(newCustomer.getIndirizzo(), newCustomer.getCAP(), newCustomer.getCivico(),
                    contactDetailsAddWindow.getLocationField().getText(), contactDetailsAddWindow.getProvinceField().getText(),
                    contactDetailsAddWindow.getPhoneNumberField().getText(), contactDetailsAddWindow.getMailField().getText());         // Inserimento dei dati di contatto nel DB attraverso la classe ContactDetailsManagement
            if (newCustomer.getP_IVA() != null && newCustomer.getRagione_sociale() != null)
                CompanyDetailsManagement.executeAddQuery(newCustomer.getP_IVA(), newCustomer.getRagione_sociale());         // Inserimento dei dati aziendali nel DB attraverso la classe CompanyDetailsManagement
            model.add(newCustomer);
            updateTable();
            clearCompanyDetails();
            clearContactDetails();
            customerAddWindow.closeAddView(event);
        }
        catch (RuntimeException missingParameters) {
            HotelSupplyManagementMain.generateAlert(Alert.AlertType.ERROR, "Errore", "Parametri assenti", "Inserire il valore di tutti i dati obbligatori.");
        }

    }

    public Customer verifyIfCompanyInfoNull() {             // Controlla se sono state inserite info sull'azienda o meno (nel caso di privati)

        String discount = customerAddWindow.getDiscountField().getText();
        String customerName = customerAddWindow.getNameField().getText();
        String customerSurname = customerAddWindow.getSurnameField().getText();
        LocalDate date = customerAddWindow.getDatePicker().getValue();
        String fiscalCode = customerAddWindow.getFiscalCodeField().getText();
        if (!customerAddWindow.getPrivateCheckBox().isSelected()) {
            if (!companyDetails.isEmpty())
                return new Customer(Integer.parseInt(discount), customerName, customerSurname, date.toString(), fiscalCode,
                        companyDetails.get(0), companyDetails.get(1), contactDetails.get(0), contactDetails.get(1), contactDetails.get(2));
            else {
                throw new RuntimeException("Dati aziendali mancanti, inserire i dati nell'apposita sezione e premere 'Conferma modifiche'");
            }
        }
        else {
            return new Customer(Integer.parseInt(discount), customerName, customerSurname, date.toString(), fiscalCode,
                    null, null, contactDetails.get(0), contactDetails.get(1), contactDetails.get(2));
        }

    }

    public void disableCompanyData() {              // Il metodo serve alla finestra CustomerAddWindow per disabilitare e resettare i campi relativi ai dati aziendali nel caso in cui venga spuntata la CheckBox "Privato"

        Button addCompanyDataButton = customerAddWindow.getAddCompanyDataButton();
        addCompanyDataButton.setDisable(!addCompanyDataButton.isDisable());
        if (addCompanyDataButton.isDisable()) {
            companyDetails.clear();
            if (companyDetailsAddWindow != null)
                companyDetailsAddWindow.resetFields();
        }

    }

    public void modifyRow(ActionEvent event, Customer selectedCustomer) {

        modifyCompanyAndContactDetails(selectedCustomer);
        String customerName = customerDisplayWindow.getNameField().getText();
        String customerSurname = customerDisplayWindow.getSurnameField().getText();
        String customerFiscalCode = customerDisplayWindow.getFiscalCodeField().getText();
        String discount = customerDisplayWindow.getDiscountField().getText();
        LocalDate date = customerDisplayWindow.getDatePicker().getValue();
        try {
            if ("".equals(customerName) || "".equals(customerSurname) || "".equals(customerFiscalCode) || "".equals(discount) || date == null) {
                throw new RuntimeException("Campi vuoti");
            }
            selectedCustomer.setNome(customerName);
            selectedCustomer.setCognome(customerSurname);
            selectedCustomer.setCodice_fiscale(customerFiscalCode);
            selectedCustomer.setData_inserimento(date.toString());
            selectedCustomer.setSconto(Integer.parseInt(discount));
            model.modify(selectedCustomer);
            createConfirmedModifyAlert();
            updateTable();
            customerDisplayWindow.closeCustomerView(event);
            contactDetailsModifyWindow = null;
            companyDetailsModifyWindow = null;
        }
        catch (RuntimeException missingParameters) {
            HotelSupplyManagementMain.generateAlert(Alert.AlertType.ERROR, "Errore", "Parametri mancanti", missingParameters.getMessage());
        }

    }

    public void modifyCompanyAndContactDetails(Customer displayedCustomer) {                 // Per prima cosa si effettua la modifica dei dati aziendali e di contatto

        if (companyDetailsModifyWindow != null) {
            CompanyDetailsManagement.executeUpdateQuery(companyDetailsModifyWindow.getP_IVAField().getText(), companyDetailsModifyWindow.getRagioneSocialeField().getText());
        }
        if (contactDetailsModifyWindow != null) {
            ContactDetailsManagement.executeUpdateQuery(contactDetails.get(0), contactDetails.get(2), contactDetails.get(1),
                    contactDetailsModifyWindow.getLocationField().getText(), contactDetailsModifyWindow.getProvinceField().getText(),
                    contactDetailsModifyWindow.getPhoneNumberField().getText(), contactDetailsModifyWindow.getMailField().getText(),
                    displayedCustomer.getIndirizzo(), displayedCustomer.getCivico(), displayedCustomer.getCAP());         // Modifica dei dati di contatto nel DB attraverso la classe ContactDetailsManagement
        }
        if (!contactDetails.isEmpty()) {
            displayedCustomer.setIndirizzo(contactDetails.get(0));
            displayedCustomer.setCivico(contactDetails.get(2));
            displayedCustomer.setCAP(contactDetails.get(1));
        }
        if (!companyDetails.isEmpty()) {
            displayedCustomer.setP_IVA(companyDetails.get(0));
            displayedCustomer.setRagione_sociale(companyDetails.get(1));
        }

    }

    @Override
    public void updateTable() {

        if(searchView) {
            ObservableList<Customer> searchResultRows = FXCollections.observableArrayList(results);
            view.setRows(searchResultRows);
        }
        else {
            ObservableList<Customer> customerRows = FXCollections.observableArrayList(model.getCustomerList());
            view.setRows(customerRows);
        }

    }

    public void setCompanyDetails(String P_IVA, String ragione_sociale) {

        clearCompanyDetails();
        companyDetails.add(P_IVA);
        companyDetails.add(ragione_sociale);

    }

    public void setContactDetails(String address, String CAP, String civicNumber) {

        clearContactDetails();
        contactDetails.add(address);
        contactDetails.add(CAP);
        contactDetails.add(civicNumber);

    }

    public void clearContactDetails() {
        contactDetails.clear();
    }

    public void clearCompanyDetails() {
        companyDetails.clear();
    }

    @Override
    public void deleteRow(Object toBeDeleted) {

        Customer selectedCustomer = (Customer) toBeDeleted;
        if (HotelSupplyManagementMain.displayConfirmationAlert("Attenzione", "Rimozione cliente", "Sicuro di procedere con l'eliminazione del cliente dalla banca dati?")) {
            model.delete(selectedCustomer);
            if (searchView)
                results.remove(selectedCustomer);                   // Se sto visualizzando una ricerca, effettuo gli aggiornamenti anche su questa view
            updateTable();
        }

    }

    public Customer getSearchFilters() {

        TextField customerCodeField = customerSearchWindow.getCustomerCodeField();
        TextField nameField = customerSearchWindow.getNameField();
        TextField surnameField = customerSearchWindow.getSurnameField();
        TextField fiscalCodeField = customerSearchWindow.getFiscalCodeField();
        TextField discountField = customerSearchWindow.getDiscountField();
        TextField PIVAField = customerSearchWindow.getPIVAField();
        TextField ragioneSocialeField = customerSearchWindow.getRagioneSocialeField();
        TextField indirizzoField = customerSearchWindow.getIndirizzoField();
        TextField CAPField = customerSearchWindow.getCAPField();
        TextField civicNumberField = customerSearchWindow.getCivicNumberField();
        Customer searchCustomer = new Customer(-1, null, null, null, null,
                null, null, null, null, null);                                          // NOTA: è un oggetto item fittizio utile alla ricerca
        int i = 0;
        try {
            while (i < 10) {
                switch (i) {
                    case 0 -> {
                        if (!nameField.isDisabled())
                            if (! "".equals(nameField.getText()))
                                searchCustomer.setNome(nameField.getText());
                            else
                                return null;
                    }
                    case 1 -> {
                        if (!discountField.isDisabled())
                            if(! "".equals(discountField.getText()))
                                searchCustomer.setSconto(Integer.parseInt(discountField.getText()));
                            else
                                return null;
                    }
                    case 2 -> {
                        if (!surnameField.isDisabled())
                            if(! "".equals(surnameField.getText()))
                                searchCustomer.setCognome(surnameField.getText());
                            else
                                return null;
                    }
                    case 3 -> {
                        if (!customerCodeField.isDisabled())
                            if(! "".equals(customerCodeField.getText()))
                                searchCustomer.setCodice_cliente(Integer.parseInt(customerCodeField.getText()));
                            else
                                return null;
                    }
                    case 4 -> {
                        if (!fiscalCodeField.isDisabled())
                            if(! "".equals(fiscalCodeField.getText()))
                                searchCustomer.setCodice_fiscale(fiscalCodeField.getText());
                            else
                                return null;
                    }
                    case 5 -> {
                        if (!PIVAField.isDisabled())
                            if(! "".equals(PIVAField.getText()))
                                searchCustomer.setP_IVA(PIVAField.getText());
                            else
                                return null;
                    }
                    case 6 -> {
                        if (!ragioneSocialeField.isDisabled())
                            if(! "".equals(ragioneSocialeField.getText()))
                                searchCustomer.setRagione_sociale(ragioneSocialeField.getText());
                            else
                                return null;
                    }
                    case 7 -> {
                        if (!indirizzoField.isDisabled())
                            if (! "".equals(indirizzoField.getText()))
                                searchCustomer.setIndirizzo(indirizzoField.getText());
                            else
                                return null;
                    }
                    case 8 -> {
                        if (!CAPField.isDisabled())
                            if(! "".equals(CAPField.getText()))
                                searchCustomer.setCAP(CAPField.getText());
                            else
                                return null;
                    }
                    case 9 -> {
                        if (!civicNumberField.isDisabled())
                            if (! "".equals(civicNumberField.getText()))
                                searchCustomer.setCivico(civicNumberField.getText());
                            else
                                return null;
                    }
                }
                i++;
            }
            return searchCustomer;
        }
        catch (NumberFormatException e) {
            isBadFormatted = true;
            return null;
        }

    }

    @Override
    public void searchRow(ActionEvent event) {

        Customer toBeSearched = getSearchFilters();
        if (toBeSearched != null) {
            results.clear();
            try {
                results = HotelSupplyManagementMain.castArrayList(model.search(toBeSearched));             // effettuo il cast della lista
                int numberOfResults = results.size();
                searchView = true;
                ObservableList<Customer> searchResultRows = FXCollections.observableArrayList(results);
                view.setRows(searchResultRows);
                HotelSupplyManagementMain.generateAlert(Alert.AlertType.INFORMATION, "Avviso", "Risultato ricerca",
                        "La ricerca ha restituito " + numberOfResults + " risultati");
                view.enableBackButton();
            }
            catch (NullPointerException e) {                            // Serve a gestire il caso in cui si lascino vuoti i campi di ricerca selezionati
                HotelSupplyManagementMain.generateAlert(Alert.AlertType.ERROR, "Errore", "Errore",
                        "Parametri di ricerca vuoti: una volta spuntati inserire almeno un valore");
            }
            customerSearchWindow.closeSearchView(event);
        }
        else if(isBadFormatted) {
            HotelSupplyManagementMain.generateAlert(Alert.AlertType.ERROR, "Errore", "Errore di formattazione", "Valore del parametro 'Codice cliente' non valido. \nRiprovare.");
        }
        else {
            HotelSupplyManagementMain.generateAlert(Alert.AlertType.ERROR, "Errore", "Parametri assenti", "Hai spuntato dei parametri ma non hai inserito i valori corrispondenti. \nRiprovare.");
        }

    }

    @Override
    public void displayView(ActionEvent event) {

        try {
            view = new CustomerManagementView();                                                                                                                                            // Apertura della finestra di gestione degli Item
            FXMLWindowLoader.loadFXML(getClass().getResource("/com/unifisweproject/hotelsupplymanagement/customer/CustomerManagementWindow.fxml"),
                    view, true, event, "Gestione clienti", false);
        }
        catch (IOException e) {
            System.err.println("Errore durante il caricamento di CustomerManagementWindow.fxml: " + e.getMessage());
        }
        initializeRows();

    }

    @Override
    public void displaySearchView(ActionEvent event) {

        try {
            customerSearchWindow = new CustomerSearchWindow();
            FXMLWindowLoader.loadFXML(getClass().getResource("/com/unifisweproject/hotelsupplymanagement/customer/CustomerSearchWindow.fxml"),
                    customerSearchWindow, false, event, "Ricerca cliente", false);
        }
        catch(IOException e) {
            System.out.println("Errore durante il caricamento di SearchCustomerView: " + e);
        }

    }

    @Override
    public void displayAddView() {

        try {
            customerAddWindow = new CustomerAddWindow();
            FXMLWindowLoader.loadFXML(getClass().getResource("/com/unifisweproject/hotelsupplymanagement/customer/CustomerAddWindow.fxml"),
                    customerAddWindow, false, null, "Aggiungi cliente", false);
        }
        catch (IOException e) {
            System.err.println("Errore durante il caricamento di CustomerAddWindow.fxml: " + e.getMessage());
        }

    }

    @Override
    public void displayRowView(ActionEvent event, Object toBeDisplayed) {

        try {
            Customer selectedCustomer = (Customer) toBeDisplayed;
            this.displayedCustomer = selectedCustomer;
            setContactDetails(selectedCustomer.getIndirizzo(), selectedCustomer.getCAP(), selectedCustomer.getCivico());
            customerDisplayWindow = new CustomerDisplayWindow(selectedCustomer);
            FXMLWindowLoader.loadFXML(getClass().getResource("/com/unifisweproject/hotelsupplymanagement/customer/CustomerDisplayWindow.fxml"),
                    customerDisplayWindow, false, event, selectedCustomer.getNome(), false);
        }
        catch (IOException e) {
            System.err.println("Errore durante l'apertura del file CustomerDisplayWindow.fxml: " + e.getMessage());
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
            companyDetailsAddWindow = new CompanyDetailsAddWindow(this);
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

    @Override
    public void openDifferentManagement(ActionEvent event) {

        Stage stage = (Stage) ((MenuItem) event.getSource()).getParentPopup().getOwnerWindow();
        String menuName = ((MenuItem) event.getSource()).getText();                                         // Ottengo il nome del menuItem premuto
        mainMenuWindow.getStageFromMenuBar(event, stage, menuName);

    }


    public void handleActionEvent(ActionEvent event) {

        if (event.getSource() instanceof Button button) {
            switch (button.getId()) {
                case "addButton" -> displayAddView();
                case "searchButton" -> displaySearchView(event);
                case "backButton" -> view.exitSearch();
                case "modifyButton" -> {
                    Customer selectedCustomer = view.getCustomerTable().getSelectionModel().getSelectedItem();
                    displayRowView(null, selectedCustomer);
                }
                case "deleteButton" -> {
                    Customer selectedCustomer = view.getCustomerTable().getSelectionModel().getSelectedItem();
                    deleteRow(selectedCustomer);
                }
                case "addContactButton" -> loadAddContactDetailsView(event);
                case "addCompanyDataButton" -> loadAddCompanyDetailsView(event);
            }
        }
        else if (event.getSource() instanceof MenuItem menuItem) {
            if (menuItem.getId().equals("viewCustomerMenu") || menuItem.getId().equals("viewDeleteCustomerMenu")) {
                Customer selectedCustomer = view.getCustomerTable().getSelectionModel().getSelectedItem();
                if (Objects.nonNull(selectedCustomer)) {
                    if (menuItem.getId().equals("viewCustomerMenu"))
                        displayRowView(null, selectedCustomer);
                    else
                        deleteRow(selectedCustomer);
                }
            }
            else
                openDifferentManagement(event);
        }

    }

    public void handleMouseEvent(Customer selectedCustomer) {
        displayRowView(null, selectedCustomer);
    }

    @Override
    public void setSearchView(boolean searchView) {
        this.searchView = searchView;
    }

    public void createConfirmedModifyAlert() {
        HotelSupplyManagementMain.generateAlert(Alert.AlertType.INFORMATION, "Avviso", "Modifica cliente", "Modifica del cliente effettuata con successo");
    }
    public Customer getDisplayedCustomer() {
        return displayedCustomer;
    }

}
