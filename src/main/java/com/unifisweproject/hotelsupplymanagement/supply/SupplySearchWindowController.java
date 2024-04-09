package com.unifisweproject.hotelsupplymanagement.supply;

import com.unifisweproject.hotelsupplymanagement.main.HotelSupplyManagementMain;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

public class SupplySearchWindowController implements Initializable {

    @FXML
    private ChoiceBox<String> companyField;
    @FXML
    private TextField itemCodeField;
    @FXML
    private DatePicker datePicker;
    @FXML
    private CheckBox enableCompanySearch;
    @FXML
    private CheckBox enableItemSearch;
    @FXML
    private CheckBox enableDateSearch;
    @FXML
    private Button confirmButton;
    private SuppliesManagementWindowController suppliesManagementWindowController;
    private ResultSet resultSet;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        Platform.runLater(() -> {
            enableCompanySearch.setOnAction(event -> handleCheckBoxAction(enableCompanySearch));
            enableItemSearch.setOnAction(event -> handleCheckBoxAction(enableItemSearch));
            enableDateSearch.setOnAction(event -> handleCheckBoxAction(enableDateSearch));
            getSupplierList();
        });

    }

    public void handleCheckBoxAction(CheckBox checkBox) {           // Metodo per l'attivazione o disattivazione dei filtri di ricerca

        if (checkBox.isSelected()) {
            verifyCheckBox(checkBox, false);
            enableConfirmButton();
        }
        else {
            verifyCheckBox(checkBox, true);
            enableConfirmButton();
        }

    }

    public void verifyCheckBox(CheckBox checkBox, boolean value) {
                                                                                            // Metodo per la verifica dei CheckBox
        if (checkBox.equals(enableCompanySearch)) {
            companyField.setDisable(value);
            companyField.setValue(null);
        }
        else if (checkBox.equals(enableItemSearch)) {
            itemCodeField.setDisable(value);
            itemCodeField.setText("");
        }
        else if (checkBox.equals(enableDateSearch)) {
            datePicker.setDisable(value);
            datePicker.setValue(null);
        }

    }

    public void enableConfirmButton() {                                                                                 // Metodo per l'abilitazione del pulsante di conferma
        confirmButton.setDisable(companyField.isDisabled() && itemCodeField.isDisabled() && datePicker.isDisabled());
    }

    public void getSupplierList() {

        resultSet = getResultSet();
        try {
            while (resultSet.next()) {
                companyField.getItems().add(resultSet.getString("Ragione_Sociale"));
            }
        }
        catch (SQLException e) {
            System.err.println("Errore durante l'estrapolazione dei dati dei fornitori: " + e.getMessage());
        }

    }

    public ResultSet getResultSet() {                   // Metodo per riottenere il ResultSet contenente i dati Codice_Fornitore e Ragione_Sociale dei fornitori dal DB

        try {
            Statement statement = HotelSupplyManagementMain.conn.createStatement();
            return statement.executeQuery("SELECT Codice_Fornitore, Ragione_Sociale FROM Fornitore");
        }
        catch (SQLException e) {
            System.err.println("Errore durante l'estrapolazione dei dati dei fornitori: " + e.getMessage());
            return null;
        }

    }

    public int getSupplierCode(String supplierName) {                       // Il metodo restituisce il codice del fornitore in base al nome del fornitore

        try {
            resultSet = getResultSet();
            while (resultSet.next()) {
                if (resultSet.getString(2).equals(supplierName)) {
                    return resultSet.getInt(1);
                }
            }
        }
        catch (SQLException e) {
            System.err.println("Errore durante l'estrapolazione dei dati dei fornitori: " + e.getMessage());
        }
        return -1;

    }

    public Supply getSearchFilters() throws NumberFormatException {

        Supply searchSupply = new Supply(-1, -1, -1, null, -1, -1);
        int i = 0;
        try {
            while (i < 3) {
                switch (i) {
                    case 0 -> {
                        if (!companyField.isDisabled()) {
                            if (companyField.getValue() != null) {
                                searchSupply.setCodice_fornitore(getSupplierCode(companyField.getValue()));
                            }
                            else
                                return null;
                        }
                    }
                    case 1 -> {
                        if (!itemCodeField.isDisabled()) {
                            if (!"".equals(itemCodeField.getText())) {
                            searchSupply.setCodice_articolo(Integer.parseInt(itemCodeField.getText()));
                            }
                            else
                                return null;
                        }
                    }
                    case 2 -> {
                        if (!datePicker.isDisabled()) {
                            if (datePicker.getValue() != null) {
                                searchSupply.setData_fornitura(datePicker.getValue().toString());
                            }
                            else
                                return null;
                        }
                    }
                }
                i++;
            }
            return searchSupply;
        }
        catch (NumberFormatException e) {
            throw new NumberFormatException();
        }

    }

    public void scanRows(ActionEvent event) {                                                                                   // Metodo per la ricerca delle righe

        try {
            Supply toBeSearched = getSearchFilters();
            if (toBeSearched != null) {
                suppliesManagementWindowController.searchRow(toBeSearched);
                ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
            }
            else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Errore");
                alert.setHeaderText("Parametri assenti");
                alert.setContentText("Hai spuntato dei parametri ma non hai inserito i valori corrispondenti. \nRiprovare.");
                alert.showAndWait();
            }
        }
        catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Errore");
            alert.setHeaderText("Errore di formattazione");
            alert.setContentText("Il codice articolo deve essere un numero intero. \nRiprovare.");
            alert.showAndWait();
        }

    }

    public void closeView(ActionEvent event) {
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
    }

    public void setSuppliesManagementSceneController(SuppliesManagementWindowController suppliesManagementWindowController) {
        this.suppliesManagementWindowController = suppliesManagementWindowController;
    }



}
