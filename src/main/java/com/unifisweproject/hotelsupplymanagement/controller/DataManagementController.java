package com.unifisweproject.hotelsupplymanagement.controller;

import javafx.event.ActionEvent;

public interface DataManagementController {

    void initializeRows();
    void createRow(ActionEvent event);
    void deleteRow(Object toBeDeleted);
    void updateTable();
    void searchRow(ActionEvent event);
    void openDifferentManagement(ActionEvent event);
    void displayView(ActionEvent event);
    void displayRowView(ActionEvent event, Object toBeDisplayed);
    void displaySearchView(ActionEvent event);
    void displayAddView();
    void handleActionEvent(ActionEvent event);
    void setSearchView(boolean searchView);

}
