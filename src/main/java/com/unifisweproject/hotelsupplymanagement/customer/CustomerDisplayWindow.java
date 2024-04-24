package com.unifisweproject.hotelsupplymanagement.customer;

import com.unifisweproject.hotelsupplymanagement.main.HotelSupplyManagementMain;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class CustomerDisplayWindow implements Initializable {

    @FXML
    public CheckBox privateCheckBox;
    @FXML
    public Label codeLabel;
    @FXML
    private TextField nameField;
    @FXML
    private DatePicker datePicker;
    @FXML
    private TextField surnameField;
    @FXML
    private TextField discountField;
    @FXML
    private TextField fiscalCodeField;
    @FXML
    private Button modifyButton;
    @FXML
    private Button backButton;
    @FXML
    private Button displayContactButton;
    @FXML
    private Button displayCompanyDataButton;
    private final Customer displayedCustomer;
    private final CustomerManagementController controller;

    public CustomerDisplayWindow(Customer displayedCustomer) {

        this.controller = CustomerManagementController.getInstance();
        this.displayedCustomer = displayedCustomer;

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        addCustomerInfo();
        datePicker.setValue(LocalDate.now());
        fiscalCodeField.setTextFormatter(HotelSupplyManagementMain.getFiscalCodeFormatter());
        discountField.setTextFormatter(HotelSupplyManagementMain.getDiscountFormatter());
        modifyButton.setOnAction(this::modifyCustomer);
        backButton.setOnAction(this::closeItemView);
        displayContactButton.setOnAction(this::viewModifyContactDetails);
        displayCompanyDataButton.setOnAction(this::viewModifyCompanyDetails);
        if (displayedCustomer.getP_IVA() != null && displayedCustomer.getRagione_sociale() != null) {               // Se il cliente è un'azienda, abilita il pulsante per l'aggiunta dei dati aziendali
            privateCheckBox.setSelected(false);
            controller.setCompanyDetails(displayedCustomer.getP_IVA(), displayedCustomer.getRagione_sociale());
        }
        else {
            displayCompanyDataButton.setDisable(true);
            privateCheckBox.setSelected(true);
        }

    }

    public void addCustomerInfo() {                                                         // Metodo per l'aggiunta delle informazioni del cliente nei TextField

        codeLabel.setText("Dati cliente n°" + displayedCustomer.getCodice_cliente());
        nameField.setText(displayedCustomer.getNome());
        surnameField.setText(displayedCustomer.getCognome());
        fiscalCodeField.setText(displayedCustomer.getCodice_fiscale());
        discountField.setText(Integer.toString(displayedCustomer.getSconto()));
        datePicker.setValue(LocalDate.parse(displayedCustomer.getData_inserimento()));

    }

    public void modifyCustomer(ActionEvent event) {
        controller.modifyRow(event, displayedCustomer);
    }

    public void closeItemView(ActionEvent event) {
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
    }

    public TextField getSurnameField() {
        return surnameField;
    }

    public DatePicker getDatePicker() {
        return datePicker;
    }

    public TextField getNameField() {
        return nameField;
    }

    public TextField getDiscountField() {
        return discountField;
    }

    public TextField getFiscalCodeField() {
        return fiscalCodeField;
    }

    public void viewModifyContactDetails(ActionEvent event) {
        controller.loadModifyContactDetailsView(event);
    }

    public void viewModifyCompanyDetails(ActionEvent event) {
        controller.loadModifyCompanyDetailsView(event);
    }

    public void closeCustomerView(ActionEvent event) {
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
    }

}
