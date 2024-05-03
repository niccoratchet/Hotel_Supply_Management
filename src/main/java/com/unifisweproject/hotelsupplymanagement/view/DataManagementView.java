package com.unifisweproject.hotelsupplymanagement.view;

import javafx.event.ActionEvent;

public interface DataManagementView {

    void initializeView();                  // Costruisce la finestra assegnando i Listener alle tabelle e le azioni ai vari bottoni
    void refreshButtons();                  // Una volta usciti dalla ricerca, vengano riattivati i bottoni per l'aggiunta e per una nuova ricerca
    void enableBackButton();                // Abilita il bottone che appare dopo una ricerca per poter visualizzare nuovamente la tabella intera
    void setCellValueFactory();             // Setta i valori delle celle delle tabelle
    void exitSearch();                      // Gestisce l'uscita dalla ricerca insieme a refreshButtons
    void handleActionEvent(ActionEvent actionEvent);        // Gestisce l'evento di pressione di un bottone o di menu
    void handleMouseEvent(Object selectedItem);             // Gestisce l'evento di pressione di un elemento della tabella con il mouse sia con click destro che con il doppio click sinistro

}
