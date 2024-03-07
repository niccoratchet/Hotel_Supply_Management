package com.unifisweproject.hotelsupplymanagement;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.time.LocalDate;
import java.util.Date;
import java.util.ResourceBundle;

public class SupplierViewController implements Initializable {


    @FXML
    private DatePicker datePicker;
    @FXML
    private TextField businessNameField;
    @FXML
    private TextField pivaField;
    @FXML
    private TextField addressField;
    @FXML
    private TextField civicField;
    @FXML
    private TextField capField;
    @FXML
    private Label codeLabel;
    private Supplier displayedSupplier;
    private SupplierManagementSceneController supplierManagementSceneController;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Platform.runLater(this::addItemInfo);
    }

    public void addItemInfo() {

        codeLabel.setText("Dati fornitore n°" + displayedSupplier.getCodice_fornitore());         // TODO: Modifica deve essere possibile premerlo SOLO se è stata selezionata una riga
        businessNameField.setText(displayedSupplier.getRagione_sociale());
        pivaField.setText(displayedSupplier.getP_IVA());
        addressField.setText(displayedSupplier.getIndirizzo());
        civicField.setText(displayedSupplier.getCivico());
        capField.setText(displayedSupplier.getCAP());
        datePicker.setValue(LocalDate.parse(displayedSupplier.getData_inserimento()));

    }

    public void setDisplayedSupplier(Supplier displayedSupplier) {
        this.displayedSupplier = displayedSupplier;
    }

    public void modifySupplier(ActionEvent event) {

        displayedSupplier.setRagione_sociale(businessNameField.getText());
        displayedSupplier.setP_IVA(pivaField.getText());
        displayedSupplier.setIndirizzo(addressField.getText());
        displayedSupplier.setCivico(civicField.getText());
        displayedSupplier.setCAP(capField.getText());
        displayedSupplier.setData_inserimento(datePicker.getValue().toString());
        supplierManagementSceneController.modifyRow(displayedSupplier);

        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();        // Istruzione per chiudere il form

    }

    public void closeSupplierView(ActionEvent event) {
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
    }

    public void setSupplierManagementSceneController(SupplierManagementSceneController supplierManagementSceneController) {
        this.supplierManagementSceneController = supplierManagementSceneController;
    }
}
