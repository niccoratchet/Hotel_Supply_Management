package com.unifisweproject.hotelsupplymanagement.supplier;

public class SupplierManagementController {

    /*
    private final SupplierManagement supplierManagement;
    private MainMenuWindowController mainMenuWindowController;
    private boolean searchView = false;
    private ArrayList<Supplier> results = new ArrayList<>();
    private static SupplierManagementController instance = null;
    private SupplierManagementView supplierManagementView;
    private SupplierAddWindow supplierAddWindow;
    private SupplierDisplayWindow supplierDisplayWindow;
    private SupplierSearchWindow supplierSearchWindow;
    private CompanyDetailsAddWindow companyDetailsAddWindow;
    private CompanyDetailsModifyWindow companyDetailsModifyWindow = null;
    private ContactDetailsAddWindow contactDetailsAddWindow;
    private ContactDetailsModifyWindow contactDetailsModifyWindow = null;
    private final ArrayList<String> companyDetails = new ArrayList<>();
    private final ArrayList<String> contactDetails = new ArrayList<>();
    private boolean isBadFormatted = false;


    private SupplierManagementController() {// Costruttore privato per evitare la creazione di nuove istanze (SingleTon)

        mainMenuWindowController = MainMenuWindowController.getInstance();
        supplierManagement = SupplierManagement.getInstance();

    }

    public static SupplierManagementController getInstance() {          // Metodo per ottenere l'istanza della classe (SingleTon)

        if (instance == null) {
            instance = new ItemManagementController();
        }
        return instance;

    }

    public void initializeRows()  {

        if (!mainMenuWindowController.getIsNotFirstTimeLoad().get(0)) {
            try {
                supplierManagement.loadFromDB();
                mainMenuWindowController.getIsNotFirstTimeLoad().set(0, true);
            }
            catch (SQLException e) {
                System.err.println("Errore durante il riempimento della tabella");
            }
        }
        ObservableList<Item> itemRows = FXCollections.observableArrayList(supplierManagement.getSupplierList());
        supplierManagementView.setRows(supplierRows);

    }

    public void createRow()  {

        LocalDate date = supplierAddWindow.getDatePicker().getValue();
        try {
            if (date == null)
                throw new RuntimeException("Parametri mancanti");
            if (contactDetails.isEmpty())
                throw new RuntimeException("Dati di contatto mancanti, inserire i dati nell'apposita sezione e premere 'Conferma modifiche'");
            ContactDetailsManagement.executeAddQuery(newSupplier.getIndirizzo(), newSupplier.getCAP(), newSupplier.getCivico(),
                    contactDetailsAddWindow.getLocationField().getText(), contactDetailsAddWindow.getProvinceField().getText(),
                    contactDetailsAddWindow.getPhoneNumberField().getText(), contactDetailsAddWindow.getMailField().getText());         // Inserimento dei dati di contatto nel DB attraverso la classe ContactDetailsManagement
            if (newSupplier.getP_IVA() != null && newSupplier.getRagione_sociale() != null)
                CompanyDetailsManagement.executeAddQuery(newSupplier.getP_IVA(), newSupplier.getRagione_sociale());         // Inserimento dei dati aziendali nel DB attraverso la classe CompanyDetailsManagement
            supplierManagement.add(newSupplier);
            updateTable();
            supplierAddWindow.closeAddView(event);
        }
        catch (RuntimeException missingParameters) {
            HotelSupplyManagementMain.generateAlert(Alert.AlertType.ERROR, "Errore", "Parametri assenti", "Inserire il valore di tutti i dati obbligatori.");
        }

    }

    public void modifyRow(ActionEvent event, Supplier displayedSupplier) {

        modifyCompanyAndContactDetails(displayedSupplier);
        LocalDate date = customerDisplayWindow.getDatePicker().getValue();
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

Supplier prima cosa si effettua la modifica dei dati aziendali e di contatto

        if (companyDetailsModifyWindow != null) {
            CompanyDetailsManagement.executeUpdateQuery(companyDetailsModifyWindow.getP_IVAField().getText(), companyDetailsModifyWindow.getRagioneSocialeField().getText());
        }
        ContactDetailsManagement.executeUpdateQuery(contactDetails.get(0), contactDetails.get(2), contactDetails.get(1),
                contactDetailsModifyWindow.getLocationField().getText(), contactDetailsModifyWindow.getProvinceField().getText(),
                contactDetailsModifyWindow.getPhoneNumberField().getText(), contactDetailsModifyWindow.getMailField().getText());         // Modifica dei dati di contatto nel DB attraverso la classe ContactDetailsManagement
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

    public void updateTable() {

        if(searchView) {
            ObservableList<Item> searchResultRows = FXCollections.observableArrayList(results);
            supplierManagementView.setRows(searchResultRows);
        }
        else {
            ObservableList<Item> itemRows = FXCollections.observableArrayList(supplierManagement.getItemList());
            supplierManagementView.setRows(supplierRows);
        }

    }

    public void exitSearch() {

        searchButton.setDisable(false);             // Riattivo bottone di ricerca
        searchButton.setVisible(true);
        searchView = false;
        updateTable();

    }

    public void setSupplierManagement(SupplierManagement supplierManagement) {
        this.supplierManagement = supplierManagement;
    }

    public void displaySupplierView(ActionEvent ignoredEvent) {

        SelectionModel<Supplier> selectionModel = supplierTable.getSelectionModel();
        Supplier selectedSupplier = selectionModel.getSelectedItem();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/unifisweproject/hotelsupplymanagement/supplier/SupplierDisplayWindow.fxml"));
            Parent root = loader.load();
            SupplierDisplayWindow supplierDisplayWindowController = loader.getController();
            supplierDisplayWindowController.setDisplayedSupplier(selectedSupplier);
            supplierDisplayWindowController.setSupplierManagementSceneController(this);
            Stage stage = new Stage();
            stage.setTitle(selectedSupplier.getRagione_sociale());
            stage.setResizable(false);
            stage.getIcons().add(HotelSupplyManagementMain.icon);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.show();
        }
        catch (IOException e) {
            System.err.println("Errore durante il caricamento della pagina SupplierDisplayWindow.fxml: " + e.getMessage());
        }

    }

    public boolean createConfirmDeleteAlert() {            // crea la finestra di avviso di cancellazione di un Item con richiesta di conferma

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Rimozione fornitore");
        alert.setContentText("Sicuro di procedere con l'eliminazione del fornitore dalla banca dati?");
        ButtonType buttonTypeYes = new ButtonType("Sì");
        ButtonType buttonTypeNo = new ButtonType("No");
        alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == buttonTypeYes;

    }

    public void createConfirmedSupplierModify() {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Modifiche applicate");
        alert.setContentText("Le modifiche sono state eseguite");
        alert.showAndWait();

    }

    public void deleteRow() {

        if (HotelSupplyManagementMain.displayConfirmationAlert("Attenzione", "Rimozione fornitore", "Sicuro di procedere con l'eliminazione del fornitore dalla banca dati?")) {
            supplierManagement.delete(selectedItem);
            if (searchView)
                results.remove(selectedItem);                   // Se sto visualizzando una ricerca, effettuo gli aggiornamenti anche su questa view
            updateTable();
        }

    }

    public Supplier getSearchFilters() {

        Supplier searchSupplier = new Supplier(null);   // NOTA: è un oggetto item fittizio utile alla ricerca
        int i = 0;
        try {
            while (i < 1) {
                switch (i) {
                    case 0 -> {
                        if (!supplierSearchWindow.getDatePicker().isDisabled())
                            if(supplierSearchWindow.getDatePicker().getValue() != null)
                                searchSupplier.setData_inserimento(supplierSearchWindow.getDatePicker().getValue().toString());
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

    public void searchRow(Supplier toBeSearched) {

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

    public void displayView(ActionEvent event) {            // Metodo per visualizzare la finestra di gestione degli articoli

        try {
            supplierManagementView = new SupplierManagementView();                                                                                                                                            // Apertura della finestra di gestione degli Item
            FXMLWindowLoader.loadFXML(getClass().getResource("/com/unifisweproject/hotelsupplymanagement/supplier/SupplierManagementWindow.fxml"),
                    supplierManagementView, true, event, "Gestione fornitori", false);
        }
        catch (IOException e) {
            System.err.println("Errore durante il caricamento di ItemManagementWindow.fxml: " + e.getMessage());
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
            supplierDisplayWindow = new SupplierDisplayWindow(selectedSupplier);
            FXMLWindowLoader.loadFXML(getClass().getResource("/com/unifisweproject/hotelsupplymanagement/supplier/SupplierDisplayWindow.fxml"),
                    supplierDisplayWindow, false, event, selectedSupplier.getRagione_sociale(), false);
        }
        catch (IOException e) {
            System.err.println("Errore durante l'apertura del file ItemDisplayWindow.fxml: " + e.getMessage());
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
                    Supplier selectedSupplier = supplierManagementView.getItemTable().getSelectionModel().getSelectedItem();
                    displayRowView(null, selectedSupplier);
                }
                case "deleteButton" -> {
                    Supplier selectedSupplier = supplierManagementView.getItemTable().getSelectionModel().getSelectedItem();
                    deleteRow(selectedSupplier);
                }
            }
        }
        else if (event.getSource() instanceof MenuItem menuItem) {
            if (menuItem.getId().equals("viewSupplierMenu") || menuItem.getId().equals("viewDeleteItemMenu")) {
                Supplier selectedSupplier = supplierManagementView.getItemTable().getSelectionModel().getSelectedItem();
                if (Objects.nonNull(selectedSupplier)) {
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

    public void setSearchView(boolean searchView) {
        this.searchView = searchView;
    }

     */
}
