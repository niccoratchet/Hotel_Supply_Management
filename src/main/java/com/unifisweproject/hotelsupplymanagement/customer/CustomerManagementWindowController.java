package com.unifisweproject.hotelsupplymanagement.customer;

import com.unifisweproject.hotelsupplymanagement.main.HotelSupplyManagementMain;
import com.unifisweproject.hotelsupplymanagement.main.MainMenuWindowController;
import javafx.application.Platform;
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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

public class CustomerManagementWindowController implements Initializable {

    private static final CustomerManagementWindowController instance = new CustomerManagementWindowController();        // Singleton
    @FXML
    private TableView<Customer> customerTable;
    @FXML
    private TableColumn<Customer, Integer> IDColumn;
    @FXML
    private TableColumn<Customer, String> BusinessNameColumn;
    @FXML
    private TableColumn<Customer, String> PivaColumn;
    @FXML
    private TableColumn<Customer, String> AddressColumn;
    @FXML
    private TableColumn<Customer, String> CapColumn;
    @FXML
    private TableColumn<Customer, String> CivicNumberColumn;
    @FXML
    private TableColumn<Customer, String> NameColumn;
    @FXML
    private TableColumn<Customer, String> SurnameColumn;
    @FXML
    private TableColumn<Customer, String> FiscalCodeColumn;
    @FXML
    private TableColumn<Customer, Integer> DiscountColumn;
    @FXML
    private TableColumn<Customer, String> DateColumn;
    @FXML
    private Button modifyButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Button backButton;
    @FXML
    private Button searchButton;
    @FXML
    private Button addButton;
    @FXML
    private AnchorPane tableAnchorPane;
    private final ContextMenu rightClickMenu = new ContextMenu();               // Content Menu e MenuItem per poter visualizzare menù tasto destro
    private final MenuItem viewCustomerMenu = new MenuItem("Visualizza");
    private final MenuItem viewDeleteCustomerMenu = new MenuItem("Elimina");
    private boolean searchView = false;

    private MainMenuWindowController mainMenuWindowController;
    private CustomerManagement customerManagement;

    ObservableList<Customer> customerRows = FXCollections.observableArrayList();    // Lista di righe presenti nella tabella, si aggiorna nel caso dell'aggiunta di una riga
    ObservableList<Customer> searchResultRows = FXCollections.observableArrayList();

    private ArrayList<Customer> results = new ArrayList<>();
    private long lastClickTime = 0;

    private CustomerManagementWindowController() {                              // Costruttore privato per Singleton
        mainMenuWindowController = MainMenuWindowController.getInstance();
        customerManagement = CustomerManagement.getInstance();
    }

    public static CustomerManagementWindowController getInstance() {            // Metodo per ottenere l'istanza del Singleton
        return instance;
    }


    @Override
    public void initialize(URL url, ResourceBundle rb) {            // Il metodo inizializza la tabella, inserendo tutte le righe presenti nel DataBase nella tabella Cliente

        Platform.runLater(this::createRows);

        customerTable.getSelectionModel().selectedItemProperty().addListener((observableValue, oldSelection, newSelection) -> {
            if(newSelection != null) {
                modifyButton.setDisable(false);
                deleteButton.setDisable(false);
            }
            else {
                modifyButton.setDisable(true);
                deleteButton.setDisable(true);
            }
        });

        rightClickMenu.getItems().addAll(viewCustomerMenu, viewDeleteCustomerMenu);
        viewCustomerMenu.setOnAction(event -> displayCustomerView(null));
        viewDeleteCustomerMenu.setOnAction(event -> deleteRow());

        customerTable.setOnMouseClicked(event -> {
            if (event.getButton().equals(MouseButton.PRIMARY)) {            // Controlla se il click è un doppio click e gestiscilo di conseguenza
                rightClickMenu.hide();
                if (event.getClickCount() == 2) {
                    long currentTime = System.currentTimeMillis();
                    if (currentTime - lastClickTime < 5000)
                       displayCustomerView(null);
                    lastClickTime = currentTime;
                }
            }
            else {
                SelectionModel<Customer> selectionModel = customerTable.getSelectionModel();        // verifico se è stato cliccato un elemento
                Customer selectedCostumer = selectionModel.getSelectedItem();
                if(selectedCostumer != null)
                    rightClickMenu.show(tableAnchorPane, event.getScreenX(), event.getScreenY()); // Mostra il menu contestuale alle coordinate del click
            }
        });

    }

    public void createRows()  {

        if (!mainMenuWindowController.getIsNotFirstTimeLoad().get(1)) {
            ResultSet resultSet = customerManagement.getRows(true, null);

            try {
                while (resultSet.next()) {
                    Customer customer = new Customer(resultSet.getInt(1), resultSet.getInt(3),
                            resultSet.getString(4), resultSet.getString(5), resultSet.getString(2), resultSet.getString(6),
                            resultSet.getString(7), resultSet.getString(8), resultSet.getString(9), resultSet.getString(11),
                            resultSet.getString(10));
                    customerManagement.getCustomerList().add(customer);
                }
                mainMenuWindowController.getIsNotFirstTimeLoad().set(1, true);
            }
            catch (SQLException e) {
                System.err.println("Errore durante il riempimento della tabella");
            }
        }

        customerRows.addAll(customerManagement.getCustomerList());
        setCellValueFactory();
        customerTable.setItems(customerRows);                       // Inserisce nella tabella tutte le righe dei Customer presenti nel DB

    }

    public void setCellValueFactory() {

        IDColumn.setCellValueFactory(new PropertyValueFactory<>("Codice_cliente"));
        BusinessNameColumn.setCellValueFactory(new PropertyValueFactory<>("Ragione_sociale"));
        PivaColumn.setCellValueFactory(new PropertyValueFactory<>("P_IVA"));
        AddressColumn.setCellValueFactory(new PropertyValueFactory<>("Indirizzo"));
        CapColumn.setCellValueFactory(new PropertyValueFactory<>("CAP"));
        CivicNumberColumn.setCellValueFactory(new PropertyValueFactory<>("Civico"));
        NameColumn.setCellValueFactory(new PropertyValueFactory<>("Nome"));
        SurnameColumn.setCellValueFactory(new PropertyValueFactory<>("Cognome"));
        FiscalCodeColumn.setCellValueFactory(new PropertyValueFactory<>("Codice_fiscale"));
        DiscountColumn.setCellValueFactory(new PropertyValueFactory<>("Sconto"));
        DateColumn.setCellValueFactory(new PropertyValueFactory<>("Data_inserimento"));

    }

    public void displayAddView() {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/unifisweproject/hotelsupplymanagement/customer/CustomerAddWindow.fxml"));
            Parent root = loader.load();
            CustomerAddWindowController addCustomerController = loader.getController();
            addCustomerController.setCustomerManagementSceneController(this);
            Stage addStage = new Stage();
            addStage.setTitle("Aggiungi cliente");
            addStage.setResizable(false);
            addStage.getIcons().add(HotelSupplyManagementMain.icon);
            addStage.initModality(Modality.APPLICATION_MODAL);
            addStage.setScene(new Scene(root));
            addStage.show();
        }
        catch (IOException e) {
            System.err.println(e.getMessage());
        }

    }

    public void addRow(Customer newCustomer) {

        newCustomer.setCodice_cliente(customerManagement.getNextCustomerCode() + 1);
        customerManagement.getCustomerList().add(newCustomer);
        customerManagement.add(newCustomer);
        updateTable();

    }

    public void modifyRow(Customer toBeModified) {

        customerManagement.modify(toBeModified);
        createConfirmedModifyAlert();
        updateTable();

    }

    public void updateTable() {

        Platform.runLater(() -> {

           if(searchView) {
               customerTable.getItems().clear();
               searchResultRows.clear();
               searchResultRows.setAll(results);
               customerTable.setItems(searchResultRows);
           }
           else {
               customerTable.getItems().clear();
               customerRows.clear();
               customerRows.setAll(customerManagement.getCustomerList());
               customerTable.setItems(customerRows);
               addButton.setDisable(false);
               addButton.setVisible(true);
               backButton.setVisible(true);
               backButton.setVisible(false);
           }

        });

    }

    public void exitSearch() {

        searchButton.setDisable(false);             // Riattivo bottone di ricerca
        searchButton.setVisible(true);
        searchView = false;
        updateTable();

    }

    public void setCustomerManagement(CustomerManagement customerManagement) {
        this.customerManagement = customerManagement;
    }

    public void displayCustomerView(ActionEvent ignoredEvent) {

        SelectionModel<Customer> selectionModel = customerTable.getSelectionModel();
        Customer selectedCustomer = selectionModel.getSelectedItem();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/unifisweproject/hotelsupplymanagement/customer/CustomerDisplayWindow.fxml"));
            Parent root = loader.load();
            CustomerDisplayWindowController customerDisplayWindowController = loader.getController();
            customerDisplayWindowController.setDisplayedCustomer(selectedCustomer);
            customerDisplayWindowController.setCustomerManagementSceneController(this);
            Stage viewStage = new Stage();
            viewStage.setTitle("Cliente n°" + selectedCustomer.getCodice_cliente());
            viewStage.setResizable(false);
            viewStage.getIcons().add(HotelSupplyManagementMain.icon);
            viewStage.initModality(Modality.APPLICATION_MODAL);
            viewStage.setScene(new Scene(root));
            viewStage.show();
        }
        catch (IOException e) {
            System.err.println("Errore durante il caricamento della pagina CustomerDisplayWindow.fxml: " + e.getMessage());
        }

    }

    public boolean createConfirmDeleteAlert() {            // crea la finestra di avviso di cancellazione di un Item con richiesta di conferma

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Rimozione cliente");
        alert.setContentText("Sicuro di procedere con l'eliminazione del cliente dalla banca dati?");
        ButtonType buttonTypeYes = new ButtonType("Sì");
        ButtonType buttonTypeNo = new ButtonType("No");
        alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == buttonTypeYes;

    }

    public void createConfirmedModifyAlert() {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Modifiche applicate");
        alert.setContentText("Le modifiche sono state eseguite");
        alert.showAndWait();

    }

    public void deleteRow() {

        if (createConfirmDeleteAlert()) {
            SelectionModel<Customer> selectionModel = customerTable.getSelectionModel();
            Customer selectedCustomer = selectionModel.getSelectedItem();
            customerManagement.getCustomerList().remove(selectedCustomer);
            customerManagement.delete(selectedCustomer.getCodice_cliente());
            if (searchView)
                results.remove(selectedCustomer);
            updateTable();
        }

    }

    public void searchRow(Customer toBeSearched) {

        results.clear();
        try {
            results = HotelSupplyManagementMain.castArrayList(customerManagement.search(toBeSearched));             // effettuo il cast della lista
            int numberOfResults = results.size();
            searchView = true;
            searchResultRows.clear();
            Platform.runLater(() -> {
                searchResultRows.setAll(results);
                customerTable.getItems().clear();
                customerTable.setItems(searchResultRows);
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

    public void displaySearchView(ActionEvent ignoredEvent) {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/unifisweproject/hotelsupplymanagement/customer/CustomerSearchWindow.fxml"));
            Parent root = loader.load();
            CustomerSearchWindowController customerSearchWindowController = loader.getController();
            customerSearchWindowController.setCustomerManagementSceneController(this);
            Stage searchStage = new Stage();
            searchStage.setTitle("Ricerca cliente");
            searchStage.setResizable(false);
            searchStage.getIcons().add(HotelSupplyManagementMain.icon);
            searchStage.initModality(Modality.APPLICATION_MODAL);
            searchStage.setScene(new Scene(root));
            searchStage.show();
        }
        catch(IOException e) {
            System.out.println("Errore durante il caricamento di CustomerView: " + e);
        }

    }

    public void openDifferentManagement(ActionEvent event) {

        Stage stage = (Stage) ((MenuItem) event.getSource()).getParentPopup().getOwnerWindow();
        String menuName = ((MenuItem) event.getSource()).getText();
        mainMenuWindowController.getStageFromMenuBar(event, stage, menuName);

    }

    public void setMainMenuController(MainMenuWindowController mainMenuWindowController) {
        this.mainMenuWindowController = mainMenuWindowController;
    }

}
