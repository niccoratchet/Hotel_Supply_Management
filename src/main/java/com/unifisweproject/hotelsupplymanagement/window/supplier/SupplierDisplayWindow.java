package com.unifisweproject.hotelsupplymanagement.window.supplier;

import com.unifisweproject.hotelsupplymanagement.controller.supplier.SupplierManagementController;
import com.unifisweproject.hotelsupplymanagement.model.supplier.Supplier;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.stage.Stage;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class SupplierDisplayWindow implements Initializable {

    @FXML
    private DatePicker datePicker;
    @FXML
    private Button modifyButton;
    @FXML
    private Button backButton;
    @FXML
    private Button displayContactButton;
    @FXML
    private Button displayCompanyButton;
    private final Supplier displayedSupplier;
    private final SupplierManagementController controller;

    public SupplierDisplayWindow(Supplier displayedSupplier) {
        this.controller = SupplierManagementController.getInstance();
        this.displayedSupplier = displayedSupplier;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        addSupplierInfo();
        datePicker.setValue(LocalDate.now());
        modifyButton.setOnAction(this::modifySupplier);
        backButton.setOnAction(this::closeSupplierView);
        displayContactButton.setOnAction(this::viewModifyContactDetails);
        displayCompanyButton.setOnAction(this::viewModifyCompanyDetails);

    }

    public void addSupplierInfo() {
        datePicker.setValue(LocalDate.parse(displayedSupplier.getData_inserimento()));
    }

    public void modifySupplier(ActionEvent event) {
        controller.modifyRow(event, displayedSupplier);
    }

    public DatePicker getDatePicker() {
        return datePicker;
    }

    public void viewModifyCompanyDetails(ActionEvent event) {              // Apre la finestra di aggiunta dati sull'azienda
        controller.loadModifyCompanyDetailsView(event);
    }

    public void viewModifyContactDetails(ActionEvent event) {
        controller.loadModifyContactDetailsView(event);
    }

    public void closeSupplierView(ActionEvent event) {
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
    }

}
