package com.unifisweproject.hotelsupplymanagement;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;


public class AddOrderViewController implements Initializable {


    @FXML
    private DatePicker datePicker;
    @FXML
    private ChoiceBox BFField;
    @FXML
    private TextField TypeOfPaymentField;
    @FXML
    private TextField CustomerCodeField;

    private OrderManagementSceneController orderManagementSceneController;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        datePicker.setValue(LocalDate.now());
    }

    public void closeAddView(ActionEvent event) {
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
    }

    public void createOrder(ActionEvent event) {
        Order newSupplier = new Order(Integer.parseInt(CustomerCodeField.getText()), true , TypeOfPaymentField.getText(), datePicker.getValue().toString());
        orderManagementSceneController.addRow(newSupplier);

        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();        // Istruzione per chiudere il form

    }

    public void setOrderManagementSceneController(OrderManagementSceneController orderManagementSceneController) {
        this.orderManagementSceneController = orderManagementSceneController;
    }

}
